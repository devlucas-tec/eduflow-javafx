package br.edu.ifpb.esperanca.eduflow.service;

import br.edu.ifpb.esperanca.eduflow.domain.entities.*;
import br.edu.ifpb.esperanca.eduflow.domain.enums.StatusAgendamento;
import br.edu.ifpb.esperanca.eduflow.repository.AgendaRepository;
import br.edu.ifpb.esperanca.eduflow.repository.AgendamentoRepository;
import br.edu.ifpb.esperanca.eduflow.repository.AtendimentoRepository;

import java.util.List;

public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final AgendaRepository agendaRepository;
    private final AtendimentoRepository atendimentoRepository;

    public AgendamentoService() {
        this.agendamentoRepository = new AgendamentoRepository();
        this.agendaRepository = new AgendaRepository();
        this.atendimentoRepository = new AtendimentoRepository();
    }

    public Agendamento agendarMonitoria(Aluno aluno, Agenda agenda, String assunto) {
        List<Agendamento> ativos = agendamentoRepository.listarAtivosPorAluno(aluno.getId());
        ativos.forEach(a -> agendaRepository.buscarPorId(a.getAgenda() != null
                ? a.getAgenda().getId() : 0L).ifPresent(a::setAgenda));
        Agendamento novo = aluno.agendarMonitoria(agenda, ativos, assunto);
        agendaRepository.atualizarVagas(agenda);
        return agendamentoRepository.salvar(novo);
    }

    public void cancelarPeloAluno(Agendamento agendamento, Aluno aluno) {
        agendamento.cancelarPeloAluno();
        agendaRepository.atualizarVagas(agendamento.getAgenda());
        agendamentoRepository.cancelarAgendamento(agendamento.getId(),
                StatusAgendamento.CANCELADO_ALUNO, null);
    }

    /**
     * Monitor cancela um agendamento (RN05).
     * A justificativa fica salva na coluna justificativa de agendamentos —
     * aluno e professor a visualizam nas suas respectivas tabelas.
     */
    public void cancelarPeloMonitor(Agendamento agendamento, String justificativa) {
        agendamento.cancelarPeloMonitor(justificativa);
        agendamentoRepository.cancelarAgendamento(agendamento.getId(),
                StatusAgendamento.CANCELADO_MONITOR, justificativa);
    }

    public Atendimento registrarAtendimento(Monitor monitor, Agendamento agendamento,
                                            boolean presenca, String conteudo) {
        Atendimento at = monitor.registrarAtendimento(agendamento, presenca, conteudo);
        // Persiste o status no agendamento
        agendamentoRepository.atualizarStatus(agendamento.getId(), agendamento.getStatus());
        // Persiste o atendimento na tabela atendimentos
        atendimentoRepository.salvar(at);
        return at;
    }

    /** Lista atendimentos registrados pelo monitor pendentes de validação pelo professor. */
    public List<Atendimento> listarParaValidacaoPorProfessor(Long professorId) {
        return atendimentoRepository.listarParaValidacaoPorProfessor(professorId);
    }

    /** Professor valida um atendimento — atualiza o status do agendamento para VALIDADO. */
    public void validarAtendimento(Agendamento agendamento, Professor professor) {
        Agenda agenda = agendamento.getAgenda();
        Disciplina disciplina = agenda != null ? agenda.getDisciplina() : null;
        if (disciplina == null)
            throw new br.edu.ifpb.esperanca.eduflow.domain.exceptions.BusinessException(
                    "Disciplina do agendamento não encontrada.");
        professor.validarAtendimento(agendamento, disciplina);
        agendamentoRepository.atualizarStatus(agendamento.getId(), agendamento.getStatus());
    }

    /**
     * Professor rejeita um atendimento registrado pelo monitor.
     * Devolve o status para PENDENTE e remove o registro de atendimento,
     * permitindo que o monitor registre novamente.
     */
    public void rejeitarAtendimento(Agendamento agendamento) {
        agendamento.setStatus(br.edu.ifpb.esperanca.eduflow.domain.enums.StatusAgendamento.PENDENTE);
        agendamentoRepository.atualizarStatus(agendamento.getId(), agendamento.getStatus());
        atendimentoRepository.deletarPorAgendamento(agendamento.getId());
    }

    public List<Agendamento> listarPorAgenda(Long agendaId) {
        return agendamentoRepository.listarPorAgenda(agendaId);
    }

    public List<Agendamento> listarAtivosPorAluno(Long alunoId) {
        return agendamentoRepository.listarAtivosPorAluno(alunoId);
    }

    public List<Agendamento> listarTodosPorAluno(Long alunoId) {
        return agendamentoRepository.listarTodosPorAluno(alunoId);
    }

    public List<Agendamento> listarParaValidacao(Long monitorId) {
        return agendamentoRepository.listarParaValidacaoPorMonitor(monitorId);
    }
}