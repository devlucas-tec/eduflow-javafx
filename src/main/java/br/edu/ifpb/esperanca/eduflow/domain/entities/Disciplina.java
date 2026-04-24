package br.edu.ifpb.esperanca.eduflow.domain.entities;

/**
 * Representa uma disciplina do catálogo acadêmico.
 */
public class Disciplina {

    private Long id;
    private String nome;
    private String codigo;
    private String semestreLetivo;

    public Disciplina() {}

    public Disciplina(Long id, String nome, String codigo, String semestreLetivo) {
        this.id = id;
        this.nome = nome;
        this.codigo = codigo;
        this.semestreLetivo = semestreLetivo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getSemestreLetivo() { return semestreLetivo; }
    public void setSemestreLetivo(String v) { this.semestreLetivo = v; }

    @Override
    public String toString() { return nome + " (" + codigo + ")"; }
}
