package br.edu.ifpb.esperanca.eduflow.domain.entities;

import br.edu.ifpb.esperanca.eduflow.domain.enums.StatusAgendamento;
import br.edu.ifpb.esperanca.eduflow.domain.exceptions.BusinessException;

import java.time.LocalDateTime;

/**
 * Representa a reserva de um aluno em uma Agenda de monitoria.
 * RN05: cancelamento apenas com >= 2h de antecedência.
 */
public class Agendamento {

    private Long id;
    private String assunto;
    private LocalDateTime dataHoraSolicitacao;
    private StatusAgendamento status;
    private Aluno aluno;
    private Agenda agenda;

    public Agendamento() {}

    // ── RN05: Prazo de cancelamento ────────────────────────────────────────────

    /**
     * Retorna true se o agendamento ainda pode ser cancelado (>= 2h de antecedência).
     */
    public boolean podeCancelar() {
        if (status == StatusAgendamento.CANCELADO_ALUNO
                || status == StatusAgendamento.CANCELADO_MONITOR
                || status == StatusAgendamento.REALIZADO
                || status == StatusAgendamento.VALIDADO) {
            return false;
        }
        LocalDateTime limiteParaCancelar = agenda.getDataHoraInicio().minusHours(2);
        return LocalDateTime.now().isBefore(limiteParaCancelar);
    }

    /**
     * Cancela este agendamento pelo aluno (RN05).
     */
    public void cancelarPeloAluno(String justificativa) {
        if (!podeCancelar())
            throw new BusinessException(
                    "O cancelamento só pode ser feito com no mínimo 2 horas de antecedência. (RN05)");
        this.status = StatusAgendamento.CANCELADO_ALUNO;
        agenda.liberarVaga();
    }

    /**
     * Cancela este agendamento pelo monitor (RN05).
     */
    public void cancelarPeloMonitor(String justificativa) {
        if (!podeCancelar())
            throw new BusinessException(
                    "O cancelamento só pode ser feito com no mínimo 2 horas de antecedência. (RN05)");
        this.status = StatusAgendamento.CANCELADO_MONITOR;
        agenda.liberarVaga();
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
}
