package br.edu.ifpb.esperanca.eduflow.service;

import br.edu.ifpb.esperanca.eduflow.domain.entities.Usuario;
import br.edu.ifpb.esperanca.eduflow.repository.UsuarioRepository;

import java.util.Optional;

public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService() {
        this.usuarioRepository = new UsuarioRepository();
    }

    public Optional<Usuario> autenticar(String email, String senha) {
        // Por enquanto, apenas repassa a chamada para o repositório.
        // Futuramente, podemos adicionar mais regras de negócio aqui.
        return usuarioRepository.autenticar(email, senha);
    }
}

