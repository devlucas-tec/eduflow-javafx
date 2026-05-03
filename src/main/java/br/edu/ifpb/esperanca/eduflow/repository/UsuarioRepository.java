package br.edu.ifpb.esperanca.eduflow.repository;

import br.edu.ifpb.esperanca.eduflow.domain.entities.*;
import br.edu.ifpb.esperanca.eduflow.domain.enums.Role;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioRepository {

    private Connection conn() {
        return ConectionFactory.getConnection();
    }

    public Usuario salvar(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nome, email, senha_hash, matricula, role, ativo) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, BCrypt.hashpw(usuario.getSenhaHash(), BCrypt.gensalt()));
            stmt.setString(4, usuario.getMatricula());
            stmt.setString(5, usuario.getRole().name());
            stmt.setBoolean(6, true);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) usuario.setId(rs.getLong(1));
            return usuario;
        } catch (SQLException e) {
            if (e.getSQLState() != null && e.getSQLState().startsWith("23")) {
                throw new RuntimeException("E-mail ou matrícula já cadastrado.", e);
            }
            throw new RuntimeException("Erro ao salvar usuário: " + e.getMessage(), e);
        }
    }

    public Optional<Usuario> autenticar(String email, String senha) {
        String sql = "SELECT * FROM usuarios WHERE email = ? AND ativo = TRUE";
        try (PreparedStatement stmt = conn().prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String hashNoBanco = rs.getString("senha_hash");
                if (BCrypt.checkpw(senha, hashNoBanco)) {
                    return Optional.of(mapearUsuario(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao autenticar usuário: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public List<Usuario> listarTodos() {
        String sql = "SELECT * FROM usuarios ORDER BY nome";
        List<Usuario> lista = new ArrayList<>();
        try (PreparedStatement stmt = conn().prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) lista.add(mapearUsuario(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar usuários: " + e.getMessage(), e);
        }
        return lista;
    }

    public List<Usuario> listarPorFiltro(String roleFilter, String statusFilter) {
        StringBuilder sql = new StringBuilder("SELECT * FROM usuarios WHERE 1=1");
        if (roleFilter != null && !roleFilter.equals("TODOS")) sql.append(" AND role = '").append(roleFilter).append("'");
        if (statusFilter != null && !statusFilter.equals("TODOS")) {
            sql.append(" AND ativo = ").append(statusFilter.equals("ATIVO") ? "TRUE" : "FALSE");
        }
        sql.append(" ORDER BY nome");

        List<Usuario> lista = new ArrayList<>();
        try (PreparedStatement stmt = conn().prepareStatement(sql.toString())) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) lista.add(mapearUsuario(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar usuários: " + e.getMessage(), e);
        }
        return lista;
    }

    public void alterarRole(Long id, Role novoRole) {
        String sql = "UPDATE usuarios SET role = ? WHERE id = ?";
        try (PreparedStatement stmt = conn().prepareStatement(sql)) {
            stmt.setString(1, novoRole.name());
            stmt.setLong(2, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao alterar role: " + e.getMessage(), e);
        }
    }

    public void alterarStatus(Long id, boolean ativo) {
        String sql = "UPDATE usuarios SET ativo = ? WHERE id = ?";
        try (PreparedStatement stmt = conn().prepareStatement(sql)) {
            stmt.setBoolean(1, ativo);
            stmt.setLong(2, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao alterar status: " + e.getMessage(), e);
        }
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

    public List<Usuario> listarProfessores() {
        String sql = "SELECT * FROM usuarios WHERE role = 'PROFESSOR' AND ativo = TRUE ORDER BY nome";
        List<Usuario> lista = new ArrayList<>();
        try (PreparedStatement stmt = conn().prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) lista.add(mapearUsuario(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar professores: " + e.getMessage(), e);
        }
        return lista;
    }

    public List<Usuario> listarMonitores() {
        String sql = "SELECT * FROM usuarios WHERE role = 'MONITOR' AND ativo = TRUE ORDER BY nome";
        List<Usuario> lista = new ArrayList<>();
        try (PreparedStatement stmt = conn().prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) lista.add(mapearUsuario(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar monitores: " + e.getMessage(), e);
        }
        return lista;
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

    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Role role = Role.valueOf(rs.getString("role"));
        Long id = rs.getLong("id");
        String nome = rs.getString("nome");
        String email = rs.getString("email");
        String senhaHash = rs.getString("senha_hash");
        String matricula = rs.getString("matricula");
        boolean ativo = rs.getBoolean("ativo");

        Usuario u = switch (role) {
            case ALUNO         -> new Aluno(id, nome, email, senhaHash, matricula);
            case MONITOR       -> new Monitor(id, nome, email, senhaHash, matricula);
            case PROFESSOR     -> new Professor(id, nome, email, senhaHash, matricula);
            case ADMINISTRADOR -> new Administrador(id, nome, email, senhaHash, matricula);
        };
        u.setAtivo(ativo);
        return u;
    }
}