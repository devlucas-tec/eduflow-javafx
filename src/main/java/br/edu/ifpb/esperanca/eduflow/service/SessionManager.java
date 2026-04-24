package br.edu.ifpb.esperanca.eduflow.service;

import br.edu.ifpb.esperanca.eduflow.domain.entities.Usuario;

/**
 * Singleton que mantém o usuário autenticado em memória durante a sessão.
 */
public class SessionManager {

    private static SessionManager instance;
    private Usuario usuarioLogado;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) instance = new SessionManager();
        return instance;
    }

    public void iniciarSessao(Usuario usuario) {
        this.usuarioLogado = usuario;
    }

    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    public void encerrarSessao() {
        this.usuarioLogado = null;
    }

    public boolean estaAutenticado() {
        return usuarioLogado != null;
    }
}
