package br.edu.ifpb.esperanca.eduflow.repository;

import br.edu.ifpb.esperanca.eduflow.domain.entities.Aula;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AulaRepository {

    private Connection conn() {
        return ConectionFactory.getConnection();
    }

    public Aula salvar(Aula aula) {
        String sql = """
            INSERT INTO aulas (data_hora, tipo, justificativa, professor_id, disciplina_id)
            VALUES (?, ?, ?, ?, ?)
            """;
        try (PreparedStatement stmt = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setTimestamp(1, Timestamp.valueOf(aula.getDataHora()));
            stmt.setString(2, aula.getTipo());
            stmt.setString(3, aula.getJustificativa());
            stmt.setLong(4, aula.getProfessorId());
            stmt.setLong(5, aula.getDisciplinaId());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) aula.setId(rs.getLong(1));
            return aula;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar aula: " + e.getMessage(), e);
        }
    }

    public void atualizar(Aula aula) {
        String sql = """
            UPDATE aulas SET data_hora = ?, tipo = ?, justificativa = ?, disciplina_id = ?
            WHERE id = ?
            """;
        try (PreparedStatement stmt = conn().prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(aula.getDataHora()));
            stmt.setString(2, aula.getTipo());
            stmt.setString(3, aula.getJustificativa());
            stmt.setLong(4, aula.getDisciplinaId());
            stmt.setLong(5, aula.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar aula: " + e.getMessage(), e);
        }
    }

    public void excluir(Long id) {
        String sql = "DELETE FROM aulas WHERE id = ?";
        try (PreparedStatement stmt = conn().prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir aula: " + e.getMessage(), e);
        }
    }

    /** Lista aulas de um professor específico, com nome da disciplina. */
    public List<Aula> listarPorProfessor(Long professorId) {
        String sql = """
            SELECT a.*, d.nome AS disciplina_nome
            FROM aulas a
            JOIN disciplinas d ON d.id = a.disciplina_id
            WHERE a.professor_id = ?
            ORDER BY a.data_hora
            """;
        List<Aula> lista = new ArrayList<>();
        try (PreparedStatement stmt = conn().prepareStatement(sql)) {
            stmt.setLong(1, professorId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar aulas: " + e.getMessage(), e);
        }
        return lista;
    }

    /** Lista todas as aulas de todos os professores (visão admin). */
    public List<Aula> listarTodas() {
        String sql = """
            SELECT a.*, d.nome AS disciplina_nome, u.nome AS professor_nome
            FROM aulas a
            JOIN disciplinas d ON d.id = a.disciplina_id
            JOIN usuarios u ON u.id = a.professor_id
            ORDER BY a.data_hora
            """;
        List<Aula> lista = new ArrayList<>();
        try (PreparedStatement stmt = conn().prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Aula au = mapear(rs);
                au.setProfessorNome(rs.getString("professor_nome"));
                lista.add(au);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar aulas: " + e.getMessage(), e);
        }
        return lista;
    }

    private Aula mapear(ResultSet rs) throws SQLException {
        Aula a = new Aula();
        a.setId(rs.getLong("id"));
        a.setDataHora(rs.getTimestamp("data_hora").toLocalDateTime());
        a.setTipo(rs.getString("tipo"));
        a.setJustificativa(rs.getString("justificativa"));
        a.setProfessorId(rs.getLong("professor_id"));
        a.setDisciplinaId(rs.getLong("disciplina_id"));
        a.setDisciplinaNome(rs.getString("disciplina_nome"));
        return a;
    }
}
