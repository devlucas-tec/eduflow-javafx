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

    /**
     * Cadastra novo usuário.
     * A senha pura é validada aqui (antes do hash).
     * O hash BCrypt é feito dentro do UsuarioRepository.salvar().
     */
    public Usuario cadastrar(Usuario usuario) {
        if (usuario.getEmail() == null || usuario.getEmail().isBlank())
            throw new BusinessException("E-mail é obrigatório.");

        // getSenhaHash() contém a senha PURA neste ponto — validamos o tamanho antes do hash
        String senhaPura = usuario.getSenhaHash();
        if (senhaPura == null || senhaPura.length() < 6)
            throw new BusinessException("Senha deve ter no mínimo 6 caracteres.");

        if (usuario.getNome() == null || usuario.getNome().isBlank())
            throw new BusinessException("Nome é obrigatório.");

        return usuarioRepository.salvar(usuario);
    }

    /** Atualiza nome e e-mail (matrícula é imutável). */
    public void editarPerfil(Usuario usuario, String novoNome, String novoEmail) {
        usuario.editarPerfil(novoNome, novoEmail);
        usuarioRepository.atualizar(usuario);
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.buscarPorId(id);
    }
}