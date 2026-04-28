package br.edu.ifpb.esperanca.eduflow.domain.entities;

import br.edu.ifpb.esperanca.eduflow.domain.enums.Role;

public abstract class Usuario {

    private Long id;
    private String nome;
    private String email;
    private String senhaHash;
    private String matricula;
    protected Role role;
    private boolean ativo = true;

    public Usuario(Long id, String nome, String email, String senhaHash, String matricula, Role role) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senhaHash = senhaHash;
        this.matricula = matricula;
        this.role = role;
        this.ativo = true;
    }

    public Usuario() {}

    /** Edita nome e e-mail. Matrícula não pode ser alterada (F1.2). */
    public void editarPerfil(String novoNome, String novoEmail) {
        if (novoNome  != null && !novoNome.isBlank())  setNome(novoNome);
        if (novoEmail != null && !novoEmail.isBlank()) setEmail(novoEmail);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenhaHash() { return senhaHash; }
    public void setSenhaHash(String senhaHash) { this.senhaHash = senhaHash; }
    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
}
