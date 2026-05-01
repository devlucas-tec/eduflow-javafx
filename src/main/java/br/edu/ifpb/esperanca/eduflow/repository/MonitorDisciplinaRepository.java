package br.edu.ifpb.esperanca.eduflow.repository;

import br.edu.ifpb.esperanca.eduflow.domain.entities.Disciplina;

import java.sql.*;
import java.util.Optional;

public class MonitorDisciplinaRepository {

    private Connection conn() {
        return ConectionFactory.getConnection();
    }

    /** Vincula monitor a uma disciplina. Remove o vínculo anterior antes (1 disciplina por vez). */
    public void vincular(Long monitorId, Long disciplinaId) {
        // Remove vínculo anterior se existir
        String del = "DELETE FROM monitor_disciplina WHERE monitor_id = ?";
        try (PreparedStatement stmt = conn().prepareStatement(del)) {
            stmt.setLong(1, monitorId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover vínculo anterior: " + e.getMessage(), e);
        }

        String ins = "INSERT INTO monitor_disciplina (monitor_id, disciplina_id) VALUES (?, ?)";
        try (PreparedStatement stmt = conn().prepareStatement(ins)) {
            stmt.setLong(1, monitorId);
            stmt.setLong(2, disciplinaId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao vincular monitor: " + e.getMessage(), e);
        }
    }

    /** Remove o vínculo do monitor com qualquer disciplina. */
    public void desvincular(Long monitorId) {
        String sql = "DELETE FROM monitor_disciplina WHERE monitor_id = ?";
        try (PreparedStatement stmt = conn().prepareStatement(sql)) {
            stmt.setLong(1, monitorId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao desvincular monitor: " + e.getMessage(), e);
        }
    }

    /** Retorna a disciplina atual do monitor, se houver. */
    public Optional<Disciplina> buscarDisciplinaDoMonitor(Long monitorId) {
        String sql = """
            SELECT d.* FROM disciplinas d
            JOIN monitor_disciplina md ON md.disciplina_id = d.id
            WHERE md.monitor_id = ?
            LIMIT 1
            """;
        try (PreparedStatement stmt = conn().prepareStatement(sql)) {
            stmt.setLong(1, monitorId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return Optional.of(new Disciplina(
                    rs.getLong("id"),
                    rs.getString("nome"),
                    rs.getString("codigo"),
                    rs.getString("semestre_letivo")));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar disciplina do monitor: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public boolean estaVinculado(Long monitorId) {
        String sql = "SELECT 1 FROM monitor_disciplina WHERE monitor_id = ?";
        try (PreparedStatement stmt = conn().prepareStatement(sql)) {
            stmt.setLong(1, monitorId);
            return stmt.executeQuery().next();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar vínculo: " + e.getMessage(), e);
        }
    }
}
