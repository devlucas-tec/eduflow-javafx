package br.edu.ifpb.esperanca.eduflow.domain.entities;

import java.time.LocalDateTime;

/**
 * Representa uma aula no calendário de um professor.
 * Tipos: REGULAR, REPOSICAO, EXTRA
 */
public class Aula {

    private Long id;
    private LocalDateTime dataHora;
    private String tipo; // REGULAR, REPOSICAO, EXTRA
    private String justificativa;
    private Long professorId;
    private String professorNome;
    private Long disciplinaId;
    private String disciplinaNome;

    public Aula() {}

    public Aula(Long id, LocalDateTime dataHora, String tipo, String justificativa,
                Long professorId, Long disciplinaId) {
        this.id = id;
        this.dataHora = dataHora;
        this.tipo = tipo;
        this.justificativa = justificativa;
        this.professorId = professorId;
        this.disciplinaId = disciplinaId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getJustificativa() { return justificativa; }
    public void setJustificativa(String justificativa) { this.justificativa = justificativa; }
    public Long getProfessorId() { return professorId; }
    public void setProfessorId(Long professorId) { this.professorId = professorId; }
    public String getProfessorNome() { return professorNome; }
    public void setProfessorNome(String professorNome) { this.professorNome = professorNome; }
    public Long getDisciplinaId() { return disciplinaId; }
    public void setDisciplinaId(Long disciplinaId) { this.disciplinaId = disciplinaId; }
    public String getDisciplinaNome() { return disciplinaNome; }
    public void setDisciplinaNome(String disciplinaNome) { this.disciplinaNome = disciplinaNome; }
}
