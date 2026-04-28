package br.edu.ifpb.esperanca.eduflow.service;

import br.edu.ifpb.esperanca.eduflow.domain.entities.Usuario;
import br.edu.ifpb.esperanca.eduflow.domain.enums.Role;
import br.edu.ifpb.esperanca.eduflow.domain.exceptions.BusinessException;
import br.edu.ifpb.esperanca.eduflow.repository.UsuarioRepository;

import java.util.List;
import java.util.Optional;

public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService() {
        this.usuarioRepository = new UsuarioRepository();
    }

    public Optional<Usuario> autenticar(String email, String senha) {
        return usuarioRepository.autenticar(email, senha);
    }

    public Usuario cadastrar(Usuario usuario) {
        if (usuario.getEmail() == null || usuario.getEmail().isBlank())
            throw new BusinessException("E-mail é obrigatório.");
        String senhaPura = usuario.getSenhaHash();
        if (senhaPura == null || senhaPura.length() < 6)
            throw new BusinessException("Senha deve ter no mínimo 6 caracteres.");
        if (usuario.getNome() == null || usuario.getNome().isBlank())
            throw new BusinessException("Nome é obrigatório.");
        return usuarioRepository.salvar(usuario);
    }

    public void editarPerfil(Usuario usuario, String novoNome, String novoEmail) {
        usuario.editarPerfil(novoNome, novoEmail);
        usuarioRepository.atualizar(usuario);
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.buscarPorId(id);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.listarTodos();
    }

    public List<Usuario> listarPorFiltro(String role, String status) {
        return usuarioRepository.listarPorFiltro(role, status);
    }

    public void alterarRole(Usuario usuario, Role novoRole) {
        if (usuario.getRole() == novoRole)
            throw new BusinessException("O usuário já possui este role.");
        usuarioRepository.alterarRole(usuario.getId(), novoRole);
    }

    public void alterarStatus(Usuario usuario, boolean ativo) {
        usuarioRepository.alterarStatus(usuario.getId(), ativo);
    }
}
