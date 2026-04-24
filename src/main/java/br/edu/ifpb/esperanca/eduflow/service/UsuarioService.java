package br.edu.ifpb.esperanca.eduflow.service;

import br.edu.ifpb.esperanca.eduflow.domain.entities.Usuario;
import br.edu.ifpb.esperanca.eduflow.domain.exceptions.BusinessException;
import br.edu.ifpb.esperanca.eduflow.repository.UsuarioRepository;

import java.util.Optional;

public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService() {
        this.usuarioRepository = new UsuarioRepository();
    }

    public Optional<Usuario> autenticar(String email, String senha) {
        return usuarioRepository.autenticar(email, senha);
    }

    /** Cadastra novo usuário. Valida email único. */
    public Usuario cadastrar(Usuario usuario) {
        if (usuario.getEmail() == null || usuario.getEmail().isBlank())
            throw new BusinessException("E-mail é obrigatório.");
        if (usuario.getSenhaHash() == null || usuario.getSenhaHash().length() < 6)
            throw new BusinessException("Senha deve ter no mínimo 6 caracteres.");
        return usuarioRepository.salvar(usuario);
    }

    /** Atualiza nome e e-mail (F1.2 — matrícula imutável). */
    public void editarPerfil(Usuario usuario, String novoNome, String novoEmail) {
        usuario.editarPerfil(novoNome, novoEmail);
        usuarioRepository.atualizar(usuario);
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.buscarPorId(id);
    }
}
