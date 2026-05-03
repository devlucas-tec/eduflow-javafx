package br.edu.ifpb.esperanca.eduflow.service;

import br.edu.ifpb.esperanca.eduflow.domain.entities.Agenda;
import br.edu.ifpb.esperanca.eduflow.domain.entities.Disciplina;
import br.edu.ifpb.esperanca.eduflow.domain.entities.Monitor;
import br.edu.ifpb.esperanca.eduflow.repository.AgendaRepository;

import java.util.List;
import java.util.Optional;

public class AgendaService {

    private final AgendaRepository agendaRepository;

    public AgendaService() {
        this.agendaRepository = new AgendaRepository();
    }

    /**
     * Cadastra uma nova agenda para o monitor.
     * Delega validação RN07 e RN10 ao Modelo Rico (Monitor.cadastrarAgenda).
     */
    public Agenda cadastrarAgenda(Monitor monitor, Disciplina disciplina, Agenda agenda) {
        Agenda validada = monitor.cadastrarAgenda(agenda, disciplina);
        return agendaRepository.salvar(validada);
    }

    public List<Agenda> listarPorDisciplina(Long disciplinaId) {
        return agendaRepository.listarPorDisciplina(disciplinaId);
    }

    /** Lista agendas disponíveis filtrando pelas disciplinas matriculadas do aluno. */
    public List<Agenda> listarAgendasDisponiveisPorAluno(Long alunoId) {
        return agendaRepository.listarDisponiveisPorAluno(alunoId);
    }

    public List<Agenda> listarAgendasDoMonitor(Long monitorId) {
        return agendaRepository.listarPorMonitor(monitorId);
    }

    public Optional<Agenda> buscarPorId(Long id) {
        return agendaRepository.buscarPorId(id);
    }

    /**
     * Cancela a sessão de monitoria: marca a agenda como cancelada
     * e persiste a justificativa na tabela agendas.
     */
    public void cancelarSessao(Long agendaId, String justificativa) {
        agendaRepository.cancelarSessao(agendaId, justificativa);
    }
}