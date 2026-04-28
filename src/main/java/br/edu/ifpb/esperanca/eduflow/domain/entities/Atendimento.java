package br.edu.ifpb.esperanca.eduflow.domain.entities;

import java.time.LocalDateTime;

/**
 * Registro do desfecho de uma monitoria feito pelo Monitor (RN06).
 * Necessário para que as horas fiquem disponíveis para validação.
 */
public class Atendimento {

    private Long id;
    private String conteudoTrabalhado;
    private boolean presenca;
    private LocalDateTime dataHoraRegistro;
    private Agendamento agendamento;

    public Atendimento() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getConteudoTrabalhado() { return conteudoTrabalhado; }
    public void setConteudoTrabalhado(String v) { this.conteudoTrabalhado = v; }
    public boolean isPresenca() { return presenca; }
    public void setPresenca(boolean presenca) { this.presenca = presenca; }
    public LocalDateTime getDataHoraRegistro() { return dataHoraRegistro; }
    public void setDataHoraRegistro(LocalDateTime v) { this.dataHoraRegistro = v; }
    public Agendamento getAgendamento() { return agendamento; }
    public void setAgendamento(Agendamento agendamento) { this.agendamento = agendamento; }
}
