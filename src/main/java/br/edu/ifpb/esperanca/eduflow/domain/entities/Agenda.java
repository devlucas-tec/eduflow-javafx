package br.edu.ifpb.esperanca.eduflow.domain.entities;

import java.time.LocalDateTime;

/**
 * Representa um slot de monitoria cadastrado pelo Monitor.
 * RN10: local físico ou link são obrigatórios (validado em Monitor.cadastrarAgenda).
 */
public class Agenda {

    private Long id;
    private LocalDateTime dataHoraInicio;
    private LocalDateTime dataHoraFim;
    private String local;
    private String link;
    private int vagasTotais;
    private int vagasOcupadas;
    private Monitor monitor;
    private Disciplina disciplina;
    private boolean cancelada;
    private String justificativa;
    private String monitorNome;

    public String getMonitorNome() { return monitorNome; }
    public void setMonitorNome(String monitorNome) { this.monitorNome = monitorNome; }

    public Agenda() {}

    public Agenda(Long id, LocalDateTime dataHoraInicio, LocalDateTime dataHoraFim,
                  String local, String link, int vagasTotais) {
        this.id = id;
        this.dataHoraInicio = dataHoraInicio;
        this.dataHoraFim = dataHoraFim;
        this.local = local;
        this.link = link;
        this.vagasTotais = vagasTotais;
        this.vagasOcupadas = 0;
    }

    /** Vagas ainda disponíveis (usada na RN03). */
    public int getVagasDisponiveis() {
        return vagasTotais - vagasOcupadas;
    }

    /** Ocupa uma vaga ao confirmar um agendamento. */
    public void ocuparVaga() {
        if (getVagasDisponiveis() <= 0)
            throw new br.edu.ifpb.esperanca.eduflow.domain.exceptions.BusinessException(
                    "Não há vagas disponíveis. (RN03)");
        this.vagasOcupadas++;
    }

    /** Libera uma vaga ao cancelar um agendamento. */
    public void liberarVaga() {
        if (vagasOcupadas > 0) this.vagasOcupadas--;
    }

    // Getters / Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getDataHoraInicio() { return dataHoraInicio; }
    public void setDataHoraInicio(LocalDateTime v) { this.dataHoraInicio = v; }
    public LocalDateTime getDataHoraFim() { return dataHoraFim; }
    public void setDataHoraFim(LocalDateTime v) { this.dataHoraFim = v; }
    public String getLocal() { return local; }
    public void setLocal(String local) { this.local = local; }
    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }
    public int getVagasTotais() { return vagasTotais; }
    public void setVagasTotais(int v) { this.vagasTotais = v; }
    public int getVagasOcupadas() { return vagasOcupadas; }
    public void setVagasOcupadas(int v) { this.vagasOcupadas = v; }
    public Monitor getMonitor() { return monitor; }
    public void setMonitor(Monitor monitor) { this.monitor = monitor; }
    public Disciplina getDisciplina() { return disciplina; }
    public void setDisciplina(Disciplina disciplina) { this.disciplina = disciplina; }
    public boolean isCancelada() { return cancelada; }
    public void setCancelada(boolean cancelada) { this.cancelada = cancelada; }
    public String getJustificativa() { return justificativa; }
    public void setJustificativa(String justificativa) { this.justificativa = justificativa; }
}