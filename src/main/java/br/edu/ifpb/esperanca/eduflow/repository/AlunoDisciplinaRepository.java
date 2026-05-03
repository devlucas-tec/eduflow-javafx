package br.edu.ifpb.esperanca.eduflow.repository;

import br.edu.ifpb.esperanca.eduflow.domain.entities.Disciplina;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AlunoDisciplinaRepository {

    private Connection conn() {
        return ConectionFactory.getConnection();
    }

    public void matricular(Long alunoId, Long disciplinaId) {
        String sql = "INSERT INTO aluno_disciplina (aluno_id, disciplina_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
        try (PreparedStatement stmt = conn().prepareStatement(sql)) {
            stmt.setLong(1, alunoId);
            stmt.setLong(2, disciplinaId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao matricular: " + e.getMessage(), e);
        }
    }

    public void cancelarMatricula(Long alunoId, Long disciplinaId) {
        String sql = "DELETE FROM aluno_disciplina WHERE aluno_id = ? AND disciplina_id = ?";
        try (PreparedStatement stmt = conn().prepareStatement(sql)) {
            stmt.setLong(1, alunoId);
            stmt.setLong(2, disciplinaId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cancelar matrícula: " + e.getMessage(), e);
        }
    }

    public List<Disciplina> listarMatriculadas(Long alunoId) {
        String sql = """
            SELECT d.* FROM disciplinas d
            JOIN aluno_disciplina ad ON ad.disciplina_id = d.id
            WHERE ad.aluno_id = ?
            ORDER BY d.nome
            """;
        return listar(sql, alunoId);
    }

    public List<Disciplina> listarDisponiveis(Long alunoId) {
        String sql = """
            SELECT d.* FROM disciplinas d
            WHERE d.id NOT IN (
                SELECT disciplina_id FROM aluno_disciplina WHERE aluno_id = ?
            )
            ORDER BY d.nome
            """;
        return listar(sql, alunoId);
    }

    public boolean estaMatriculado(Long alunoId, Long disciplinaId) {
        String sql = "SELECT 1 FROM aluno_disciplina WHERE aluno_id = ? AND disciplina_id = ?";
        try (PreparedStatement stmt = conn().prepareStatement(sql)) {
            stmt.setLong(1, alunoId);
            stmt.setLong(2, disciplinaId);
            return stmt.executeQuery().next();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar matrícula: " + e.getMessage(), e);
        }
    }

    /**
     * ✅ NOVO: Remove todas as matrículas de uma disciplina.
     * Usado pela exclusão em cascata do DisciplinaService.
     */
    public void excluirPorDisciplina(Long disciplinaId) {
        String sql = "DELETE FROM aluno_disciplina WHERE disciplina_id = ?";
        try (PreparedStatement stmt = conn().prepareStatement(sql)) {
            stmt.setLong(1, disciplinaId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir matrículas da disciplina: " + e.getMessage(), e);
        }
    }

    private List<Disciplina> listar(String sql, Long alunoId) {
        List<Disciplina> lista = new ArrayList<>();
        try (PreparedStatement stmt = conn().prepareStatement(sql)) {
            stmt.setLong(1, alunoId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) lista.add(new Disciplina(
                    rs.getLong("id"),
                    rs.getString("nome"),
                    rs.getString("codigo"),
                    rs.getString("semestre_letivo")));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar disciplinas: " + e.getMessage(), e);
        }
        return lista;
    }
}