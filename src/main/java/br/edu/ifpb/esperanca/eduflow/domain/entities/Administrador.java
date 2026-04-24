package br.edu.ifpb.esperanca.eduflow.domain.entities;

import br.edu.ifpb.esperanca.eduflow.domain.enums.Role;

/**
 * Administrador gerencia o cadastro base de usuários e catálogo de disciplinas.
 * Toda lógica de negócio do admin é delegada aos respectivos Services.
 */
public class Administrador extends Usuario {

    public Administrador() {
        this.role = Role.ADMINISTRADOR;
    }

    public Administrador(Long id, String nome, String email, String senhaHash, String matricula) {
        super(id, nome, email, senhaHash, matricula, Role.ADMINISTRADOR);
    }
}
