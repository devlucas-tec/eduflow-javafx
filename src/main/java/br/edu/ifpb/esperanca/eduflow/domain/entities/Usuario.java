package br.edu.ifpb.esperanca.eduflow.domain.entities;

import br.edu.ifpb.esperanca.eduflow.domain.enums.Role;

public abstract class Usuario {

    private Long id;
    private String nome;
    private String email;
    private String senhaHash;
    private String matricula;
    protected Role role;


    public void editarPerfil() {

    }
}
