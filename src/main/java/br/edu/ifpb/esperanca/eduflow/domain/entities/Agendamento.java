package br.edu.ifpb.esperanca.eduflow.domain.entities;

import br.edu.ifpb.esperanca.eduflow.domain.enums.StatusAgendamento;
import br.edu.ifpb.esperanca.eduflow.domain.exceptions.BusinessException;

import java.time.LocalDateTime;

/**
 * Representa a reserva de um aluno em uma Agenda de monitoria.
 *
 * RN05 — Cancelamento pelo MONITOR:
 *   - Só pode cancelar com >= 2h de antecedência.
 *   - Justificativa obrigatória.
 *
 * Cancelamento pelo ALUNO:
 *   - Sem restrição de horário.
 *   - Sem justificativa obrigatória.
 */
public class Agendamento {

    private Long id;
    private String assunto;
    private LocalDateTime dataHoraSolicitacao;
    private StatusAgendamento status;
    private Aluno aluno;
    private Agenda agenda;
    private String justificativa;

    public Agendamento() {}

    // ── Verificação de status já encerrado ────────────────────────────────────

    private boolean jaEncerrado() {
        return status == StatusAgendamento.CANCELADO_ALUNO
                || status == StatusAgendamento.CANCELADO_MONITOR
                || status == StatusAgendamento.REALIZADO
                || status == StatusAgendamento.VALIDADO;
    }

    // ── Cancelamento pelo ALUNO (sem restrições) ──────────────────────────────

    public void cancelarPeloAluno() {
        if (jaEncerrado())
            throw new BusinessException("Este agendamento já está encerrado.");
        this.status = StatusAgendamento.CANCELADO_ALUNO;
        if (agenda != null) agenda.liberarVaga();
    }

    // ── RN05: Cancelamento pelo MONITOR (>= 2h de antecedência + justificativa) ──

    public boolean podeCancelarComoMonitor() {
        if (jaEncerrado()) return false;
        if (agenda == null || agenda.getDataHoraInicio() == null) return false;
        return LocalDateTime.now().isBefore(agenda.getDataHoraInicio().minusHours(2));
    }

    public void cancelarPeloMonitor(String justificativa) {
        if (justificativa == null || justificativa.isBlank())
            throw new BusinessException("A justificativa é obrigatória para cancelamento pelo monitor. (RN05)");
        if (!podeCancelarComoMonitor())
            throw new BusinessException("O cancelamento pelo monitor só pode ser feito com no mínimo 2 horas de antecedência. (RN05)");
        this.justificativa = justificativa;
        this.status = StatusAgendamento.CANCELADO_MONITOR;
        if (agenda != null) agenda.liberarVaga();
    }

    // Getters / Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAssunto() { return assunto; }
    public void setAssunto(String assunto) { this.assunto = assunto; }
    public LocalDateTime getDataHoraSolicitacao() { return dataHoraSolicitacao; }
    public void setDataHoraSolicitacao(LocalDateTime v) { this.dataHoraSolicitacao = v; }
    public StatusAgendamento getStatus() { return status; }
    public void setStatus(StatusAgendamento status) { this.status = status; }
    public Aluno getAluno() { return aluno; }
    public void setAluno(Aluno aluno) { this.aluno = aluno; }
    public Agenda getAgenda() { return agenda; }
    public void setAgenda(Agenda agenda) { this.agenda = agenda; }
    public String getJustificativa() { return justificativa; }
    public void setJustificativa(String justificativa) { this.justificativa = justificativa; }
}