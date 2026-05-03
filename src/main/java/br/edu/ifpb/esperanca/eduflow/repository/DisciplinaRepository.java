package br.edu.ifpb.esperanca.eduflow.repository;

import br.edu.ifpb.esperanca.eduflow.domain.entities.Disciplina;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DisciplinaRepository {

    // ✅ CORREÇÃO: não armazena a conexão como campo final.
    // Usa um método conn() que busca sempre a conexão atual do ConectionFactory,
    // evitando falhas quando a conexão expira (comum com Neon/serverless).
    private Connection conn() {
        return ConectionFactory.getConnection();
    }

    public Disciplina salvar(Disciplina disciplina) {
        String sql = "INSERT INTO disciplinas (nome, codigo, semestre_letivo) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, disciplina.getNome());
            stmt.setString(2, disciplina.getCodigo());
            stmt.setString(3, disciplina.getSemestreLetivo());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) disciplina.setId(rs.getLong(1));
            return disciplina;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar disciplina", e);
        }
    }

    public List<Disciplina> listarTodas() {
        String sql = "SELECT * FROM disciplinas ORDER BY nome";
        List<Disciplina> lista = new ArrayList<>();
        try (PreparedStatement stmt = conn().prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar disciplinas", e);
        }
        return lista;
    }

    public Optional<Disciplina> buscarPorId(Long id) {
        String sql = "SELECT * FROM disciplinas WHERE id = ?";
        try (PreparedStatement stmt = conn().prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return Optional.of(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar disciplina", e);
        }
        return Optional.empty();
    }

    /** Busca disciplinas vinculadas a um monitor. */
    public List<Disciplina> listarPorMonitor(Long monitorId) {
        String sql = """
            SELECT d.* FROM disciplinas d
            JOIN monitor_disciplina md ON md.disciplina_id = d.id
            WHERE md.monitor_id = ?
            ORDER BY d.nome
            """;
        List<Disciplina> lista = new ArrayList<>();
        try (PreparedStatement stmt = conn().prepareStatement(sql)) {
            stmt.setLong(1, monitorId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar disciplinas do monitor", e);
        }
        return lista;
    }

    /** Busca disciplinas de um professor. */
    public List<Disciplina> listarPorProfessor(Long professorId) {
        String sql = """
            SELECT d.* FROM disciplinas d
            JOIN professor_disciplina pd ON pd.disciplina_id = d.id
            WHERE pd.professor_id = ?
            ORDER BY d.nome
            """;
        List<Disciplina> lista = new ArrayList<>();
        try (PreparedStatement stmt = conn().prepareStatement(sql)) {
            stmt.setLong(1, professorId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar disciplinas do professor", e);
        }
        return lista;
    }

    public void excluir(Long id) {
        String sql = "DELETE FROM disciplinas WHERE id = ?";
        try (PreparedStatement stmt = conn().prepareStatement(sql)) {
            stmt.setLong(1, id);
            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("Nenhuma disciplina encontrada com o id " + id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir disciplina: " + e.getMessage(), e);
        }
    }

    private Disciplina mapear(ResultSet rs) throws SQLException {
        return new Disciplina(
                rs.getLong("id"),
                rs.getString("nome"),
                rs.getString("codigo"),
                rs.getString("semestre_letivo")
        );
    }
}