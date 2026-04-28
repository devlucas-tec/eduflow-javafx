package br.edu.ifpb.esperanca.eduflow.repository;

import br.edu.ifpb.esperanca.eduflow.domain.entities.Disciplina;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProfessorDisciplinaRepository {

    private Connection conn() {
        return ConectionFactory.getConnection();
    }

    public void vincular(Long professorId, Long disciplinaId) {
        String sql = "INSERT INTO professor_disciplina (professor_id, disciplina_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
        try (PreparedStatement stmt = conn().prepareStatement(sql)) {
            stmt.setLong(1, professorId);
            stmt.setLong(2, disciplinaId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao vincular professor à disciplina: " + e.getMessage(), e);
        }
    }

    public void desvincular(Long professorId, Long disciplinaId) {
        String sql = "DELETE FROM professor_disciplina WHERE professor_id = ? AND disciplina_id = ?";
        try (PreparedStatement stmt = conn().prepareStatement(sql)) {
            stmt.setLong(1, professorId);
            stmt.setLong(2, disciplinaId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao desvincular professor da disciplina: " + e.getMessage(), e);
        }
    }

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
            while (rs.next()) lista.add(new Disciplina(
                    rs.getLong("id"),
                    rs.getString("nome"),
                    rs.getString("codigo"),
                    rs.getString("semestre_letivo")));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar disciplinas do professor: " + e.getMessage(), e);
        }
        return lista;
    }

    public boolean estaVinculado(Long professorId, Long disciplinaId) {
        String sql = "SELECT 1 FROM professor_disciplina WHERE professor_id = ? AND disciplina_id = ?";
        try (PreparedStatement stmt = conn().prepareStatement(sql)) {
            stmt.setLong(1, professorId);
            stmt.setLong(2, disciplinaId);
            return stmt.executeQuery().next();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar vínculo: " + e.getMessage(), e);
        }
    }
}
