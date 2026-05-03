package br.edu.ifpb.esperanca.eduflow.service;

import br.edu.ifpb.esperanca.eduflow.domain.entities.Disciplina;
import br.edu.ifpb.esperanca.eduflow.domain.exceptions.BusinessException;
import br.edu.ifpb.esperanca.eduflow.repository.*;

import java.util.List;

public class DisciplinaService {

    private final DisciplinaRepository disciplinaRepository;
    private final AgendaRepository agendaRepository;
    private final AgendamentoRepository agendamentoRepository;
    private final AtendimentoRepository atendimentoRepository;
    private final AlunoDisciplinaRepository alunoDisciplinaRepository;
    private final MonitorDisciplinaRepository monitorDisciplinaRepository;
    private final ProfessorDisciplinaRepository professorDisciplinaRepository;

    public DisciplinaService() {
        this.disciplinaRepository       = new DisciplinaRepository();
        this.agendaRepository           = new AgendaRepository();
        this.agendamentoRepository      = new AgendamentoRepository();
        this.atendimentoRepository      = new AtendimentoRepository();
        this.alunoDisciplinaRepository  = new AlunoDisciplinaRepository();
        this.monitorDisciplinaRepository = new MonitorDisciplinaRepository();
        this.professorDisciplinaRepository = new ProfessorDisciplinaRepository();
    }

    public Disciplina cadastrar(Disciplina disciplina) {
        if (disciplina.getNome() == null || disciplina.getNome().isBlank())
            throw new BusinessException("Nome da disciplina é obrigatório.");
        if (disciplina.getCodigo() == null || disciplina.getCodigo().isBlank())
            throw new BusinessException("Código da disciplina é obrigatório.");
        return disciplinaRepository.salvar(disciplina);
    }

    public List<Disciplina> listarTodas() {
        return disciplinaRepository.listarTodas();
    }

    public List<Disciplina> listarPorMonitor(Long monitorId) {
        return disciplinaRepository.listarPorMonitor(monitorId);
    }

    public List<Disciplina> listarPorProfessor(Long professorId) {
        return disciplinaRepository.listarPorProfessor(professorId);
    }

    /**
     * Exclui uma disciplina apagando todos os dados dependentes primeiro,
     * respeitando a ordem das foreign keys:
     *
     *  1. atendimentos  (FK → agendamentos)
     *  2. agendamentos  (FK → agendas)
     *  3. agendas       (FK → disciplinas)
     *  4. aluno_disciplina     (FK → disciplinas)
     *  5. monitor_disciplina   (FK → disciplinas)
     *  6. professor_disciplina (FK → disciplinas)
     *  7. disciplinas
     */
    public void excluir(Long disciplinaId) {
        // 1. Busca todas as agendas dessa disciplina
        var agendas = agendaRepository.listarPorDisciplina(disciplinaId);

        for (var agenda : agendas) {
            // 2. Para cada agenda, busca os agendamentos
            var agendamentos = agendamentoRepository.listarPorAgenda(agenda.getId());

            for (var agendamento : agendamentos) {
                // 3. Apaga o atendimento vinculado ao agendamento (se existir)
                atendimentoRepository.deletarPorAgendamento(agendamento.getId());
            }

            // 4. Apaga todos os agendamentos da agenda
            agendamentoRepository.excluirPorAgenda(agenda.getId());
        }

        // 5. Apaga todas as agendas da disciplina
        agendaRepository.excluirPorDisciplina(disciplinaId);

        // 6. Remove vínculos aluno ↔ disciplina
        alunoDisciplinaRepository.excluirPorDisciplina(disciplinaId);

        // 7. Remove vínculos monitor ↔ disciplina
        monitorDisciplinaRepository.excluirPorDisciplina(disciplinaId);

        // 8. Remove vínculos professor ↔ disciplina
        professorDisciplinaRepository.excluirPorDisciplina(disciplinaId);

        // 9. Finalmente apaga a disciplina
        disciplinaRepository.excluir(disciplinaId);
    }
}