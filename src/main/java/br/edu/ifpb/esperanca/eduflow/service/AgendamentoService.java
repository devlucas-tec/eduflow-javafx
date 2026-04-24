package br.edu.ifpb.esperanca.eduflow.service;

import br.edu.ifpb.esperanca.eduflow.domain.entities.*;
import br.edu.ifpb.esperanca.eduflow.domain.enums.StatusAgendamento;
import br.edu.ifpb.esperanca.eduflow.repository.AgendaRepository;
import br.edu.ifpb.esperanca.eduflow.repository.AgendamentoRepository;

import java.util.List;

public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final AgendaRepository agendaRepository;

    public AgendamentoService() {
        this.agendamentoRepository = new AgendamentoRepository();
        this.agendaRepository = new AgendaRepository();
    }

    /**
     * Aluno agenda uma monitoria.
     * Delega validações RN01/RN03/RN04/RN08 ao Modelo Rico.
     */
    public Agendamento agendarMonitoria(Aluno aluno, Agenda agenda, String assunto) {
        List<Agendamento> ativos = agendamentoRepository.listarAtivosPorAluno(aluno.getId());
        // Rehidrata agendas nos ativos para que temConflito() possa comparar horários
        ativos.forEach(a -> agendaRepository.buscarPorId(a.getAgenda() != null
                ? a.getAgenda().getId() : 0L).ifPresent(a::setAgenda));

        Agendamento novo = aluno.agendarMonitoria(agenda, ativos, assunto);
        agendaRepository.atualizarVagas(agenda);
        return agendamentoRepository.salvar(novo);
    }

    /**
     * Aluno cancela um agendamento (RN05).
     */
    public void cancelarPeloAluno(Agendamento agendamento, Aluno aluno, String justificativa) {
        agendamento.cancelarPeloAluno(justificativa);
        agendaRepository.atualizarVagas(agendamento.getAgenda());
        agendamentoRepository.atualizarStatus(agendamento.getId(), StatusAgendamento.CANCELADO_ALUNO);
    }

    /**
     * Monitor cancela um agendamento (RN05).
     */
    public void cancelarPeloMonitor(Agendamento agendamento, String justificativa) {
        agendamento.cancelarPeloMonitor(justificativa);
        agendaRepository.atualizarVagas(agendamento.getAgenda());
        agendamentoRepository.atualizarStatus(agendamento.getId(), StatusAgendamento.CANCELADO_MONITOR);
    }

    /**
     * Monitor registra atendimento (RN06).
     */
    public Atendimento registrarAtendimento(Monitor monitor, Agendamento agendamento,
                                            boolean presenca, String conteudo) {
        Atendimento at = monitor.registrarAtendimento(agendamento, presenca, conteudo);
        agendamentoRepository.atualizarStatus(agendamento.getId(), agendamento.getStatus());
        return at;
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
