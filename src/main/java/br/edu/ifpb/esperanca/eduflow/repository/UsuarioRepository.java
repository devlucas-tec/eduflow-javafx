package br.edu.ifpb.esperanca.eduflow.repository;

import br.edu.ifpb.esperanca.eduflow.domain.entities.*;
import br.edu.ifpb.esperanca.eduflow.domain.enums.Role;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.Optional;

public class UsuarioRepository {

    /**
     * Obtém a conexão em cada operação (via singleton com reconexão automática).
     * Isso evita usar uma conexão fechada pelo timeout do Neon.
     */
    private Connection conn() {
        return ConectionFactory.getConnection();
    }

    public Usuario salvar(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nome, email, senha_hash, matricula, role) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            // Gera o hash BCrypt da senha pura aqui
            stmt.setString(3, BCrypt.hashpw(usuario.getSenhaHash(), BCrypt.gensalt()));
            stmt.setString(4, usuario.getMatricula());
            stmt.setString(5, usuario.getRole().name());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) usuario.setId(rs.getLong(1));
            return usuario;
        } catch (SQLException e) {
            // Trata violação de unicidade (e-mail ou matrícula duplicada)
            if (e.getSQLState() != null && e.getSQLState().startsWith("23")) {
                throw new RuntimeException("E-mail ou matrícula já cadastrado.", e);
            }
            throw new RuntimeException("Erro ao salvar usuário: " + e.getMessage(), e);
        }
    }

    public Optional<Usuario> autenticar(String email, String senha) {
        String sql = "SELECT * FROM usuarios WHERE email = ?";
        try (PreparedStatement stmt = conn().prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String hashNoBanco = rs.getString("senha_hash");
                // Verifica a senha pura contra o hash armazenado
                if (BCrypt.checkpw(senha, hashNoBanco)) {
                    return Optional.of(mapearUsuario(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao autenticar usuário: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public Optional<Usuario> buscarPorId(Long id) {
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        try (PreparedStatement stmt = conn().prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return Optional.of(mapearUsuario(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuário: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public void atualizar(Usuario usuario) {
        String sql = "UPDATE usuarios SET nome = ?, email = ? WHERE id = ?";
        try (PreparedStatement stmt = conn().prepareStatement(sql)) {
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setLong(3, usuario.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar usuário: " + e.getMessage(), e);
        }
    }

    /** Mapeia o ResultSet para a subclasse correta conforme o Role. */
    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Role role = Role.valueOf(rs.getString("role"));
        Long id = rs.getLong("id");
        String nome = rs.getString("nome");
        String email = rs.getString("email");
        String senhaHash = rs.getString("senha_hash");
        String matricula = rs.getString("matricula");

        return switch (role) {
            case ALUNO         -> new Aluno(id, nome, email, senhaHash, matricula);
            case MONITOR       -> new Monitor(id, nome, email, senhaHash, matricula);
            case PROFESSOR     -> new Professor(id, nome, email, senhaHash, matricula);
            case ADMINISTRADOR -> new Administrador(id, nome, email, senhaHash, matricula);
        };
    }
}