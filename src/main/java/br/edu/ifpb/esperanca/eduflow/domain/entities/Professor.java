package br.edu.ifpb.esperanca.eduflow.domain.entities;

import br.edu.ifpb.esperanca.eduflow.domain.enums.Role;
import br.edu.ifpb.esperanca.eduflow.domain.enums.StatusAgendamento;
import br.edu.ifpb.esperanca.eduflow.domain.exceptions.BusinessException;

import java.util.ArrayList;
import java.util.List;

/**
 * Professor supervisiona disciplinas e valida relatórios de monitores.
 * RN02: só o professor responsável pode validar (nunca o próprio monitor).
 */
public class Professor extends Usuario {

    private List<Disciplina> disciplinas = new ArrayList<>();

    public Professor() {
        this.role = Role.PROFESSOR;
    }

    public Professor(Long id, String nome, String email, String senhaHash, String matricula) {
        super(id, nome, email, senhaHash, matricula, Role.PROFESSOR);
    }

    // ── RN02: Validação de horas ───────────────────────────────────────────────

    /**
     * Valida (aprova) um atendimento registrado pelo monitor.
     * Muda status do agendamento para VALIDADO.
     * Apenas professores vinculados à disciplina do agendamento podem validar (RN02).
     */
    public void validarAtendimento(Agendamento agendamento, Disciplina disciplina) {
        boolean responsavel = disciplinas.stream()
                .anyMatch(d -> d.getId().equals(disciplina.getId()));

        if (!responsavel)
            throw new BusinessException(
                    "Professor não é responsável por esta disciplina. (RN02)");

        if (agendamento.getStatus() != StatusAgendamento.REALIZADO
                && agendamento.getStatus() != StatusAgendamento.FALTOU)
            throw new BusinessException(
                    "Só é possível validar agendamentos com status REALIZADO ou FALTOU.");

        agendamento.setStatus(StatusAgendamento.VALIDADO);
    }

    // ── F2.2: Vinculação de monitor à disciplina ──────────────────────────────

    /** Vincula um monitor a uma disciplina (somente professor responsável). */
    public void vincularMonitor(Monitor monitor, Disciplina disciplina) {
        boolean responsavel = disciplinas.stream()
                .anyMatch(d -> d.getId().equals(disciplina.getId()));
        if (!responsavel)
            throw new BusinessException(
                    "Professor não pode vincular monitores a disciplinas que não leciona.");

        if (!monitor.getDisciplinasVinculadas().contains(disciplina)) {
            monitor.getDisciplinasVinculadas().add(disciplina);
        }
    }

    // ── Getters / Setters ─────────────────────────────────────────────────────

    public List<Disciplina> getDisciplinas() { return disciplinas; }
    public void setDisciplinas(List<Disciplina> v) { this.disciplinas = v; }
}
