package br.edu.ifpb.esperanca.eduflow.domain.entities;

import br.edu.ifpb.esperanca.eduflow.domain.enums.Role;
import br.edu.ifpb.esperanca.eduflow.domain.enums.StatusAgendamento;
import br.edu.ifpb.esperanca.eduflow.domain.exceptions.BusinessException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Representa um aluno. Contém lógica de negócio de agendamento (RN01, RN03, RN04, RN05).
 */
public class Aluno extends Usuario {

    private int faltasAcumuladas;
    private LocalDate bloqueadoAte;

    private static final int LIMITE_FALTAS = 3;
    private static final int DIAS_BLOQUEIO = 7;

    public Aluno() {
        this.role = Role.ALUNO;
        this.faltasAcumuladas = 0;
    }

    public Aluno(Long id, String nome, String email, String senhaHash, String matricula) {
        super(id, nome, email, senhaHash, matricula, Role.ALUNO);
        this.faltasAcumuladas = 0;
    }

    // ── RN04: Bloqueio por faltas ──────────────────────────────────────────────

    /** Retorna true se o aluno estiver no período de bloqueio (RN04). */
    public boolean estaBloqueado() {
        if (bloqueadoAte == null) return false;
        return LocalDate.now().isBefore(bloqueadoAte);
    }

    /** Incrementa faltas e aplica bloqueio se atingir o limite (RN04). */
    public void registrarFalta() {
        this.faltasAcumuladas++;
        if (this.faltasAcumuladas >= LIMITE_FALTAS) {
            this.bloqueadoAte = LocalDate.now().plusDays(DIAS_BLOQUEIO);
        }
    }

    /** Zera faltas (uso pelo Admin ou virada de semestre). */
    public void resetarFaltas() {
        this.faltasAcumuladas = 0;
        this.bloqueadoAte = null;
    }

    // ── RN01: Conflito de horários ─────────────────────────────────────────────

    public boolean temConflito(List<Agendamento> agendamentosAtivos,
                               LocalDateTime novoInicio,
                               LocalDateTime novoFim) {
        for (Agendamento a : agendamentosAtivos) {
            boolean ativo = a.getStatus() == StatusAgendamento.PENDENTE
                    || a.getStatus() == StatusAgendamento.CONFIRMADO;
            if (!ativo) continue;
            LocalDateTime ini = a.getAgenda().getDataHoraInicio();
            LocalDateTime fim = a.getAgenda().getDataHoraFim();
            if (novoInicio.isBefore(fim) && novoFim.isAfter(ini)) return true;
        }
        return false;
    }

    // ── RN03: Disponibilidade de vagas ────────────────────────────────────────

    public boolean podeAgendar(Agenda agenda) {
        return agenda.getVagasDisponiveis() > 0;
    }

    // ── Ação principal ────────────────────────────────────────────────────────

    public Agendamento agendarMonitoria(Agenda agenda,
                                        List<Agendamento> agendamentosAtivos,
                                        String assunto) {
        if (estaBloqueado())
            throw new BusinessException("Você está bloqueado até " + bloqueadoAte + " por faltas sem aviso. (RN04)");

        if (!podeAgendar(agenda))
            throw new BusinessException("Não há vagas disponíveis neste horário. (RN03)");

        if (temConflito(agendamentosAtivos, agenda.getDataHoraInicio(), agenda.getDataHoraFim()))
            throw new BusinessException("Você já possui um agendamento neste horário. (RN01)");

        Agendamento novo = new Agendamento();
        novo.setAssunto(assunto);
        novo.setDataHoraSolicitacao(LocalDateTime.now());
        novo.setStatus(StatusAgendamento.PENDENTE);
        novo.setAluno(this);
        novo.setAgenda(agenda);
        agenda.ocuparVaga();
        return novo;
    }

    // Getters / Setters
    public int getFaltasAcumuladas() { return faltasAcumuladas; }
    public void setFaltasAcumuladas(int v) { this.faltasAcumuladas = v; }
    public LocalDate getBloqueadoAte() { return bloqueadoAte; }
    public void setBloqueadoAte(LocalDate v) { this.bloqueadoAte = v; }
}
