package br.edu.ifpb.esperanca.eduflow.repository;

import br.edu.ifpb.esperanca.eduflow.domain.entities.Agenda;
import br.edu.ifpb.esperanca.eduflow.domain.entities.Disciplina;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AgendaRepository {

    private Connection conn() {
        return ConectionFactory.getConnection();
    }

    public Agenda salvar(Agenda agenda) {
        String sql = """
            INSERT INTO agendas (data_hora_inicio, data_hora_fim, local, link,
                                 vagas_totais, vagas_ocupadas, monitor_id, disciplina_id)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement stmt = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setTimestamp(1, Timestamp.valueOf(agenda.getDataHoraInicio()));
            stmt.setTimestamp(2, Timestamp.valueOf(agenda.getDataHoraFim()));
            stmt.setString(3, agenda.getLocal());
            stmt.setString(4, agenda.getLink());
            stmt.setInt(5, agenda.getVagasTotais());
            stmt.setInt(6, agenda.getVagasOcupadas());
            stmt.setLong(7, agenda.getMonitor().getId());
            stmt.setLong(8, agenda.getDisciplina().getId());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) agenda.setId(rs.getLong(1));
            return agenda;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar agenda", e);
        }
    }

    public Optional<Agenda> buscarPorId(Long id) {
        String sql = """
            SELECT a.*, d.nome AS disciplina_nome 
            FROM agendas a 
            JOIN disciplinas d ON a.disciplina_id = d.id 
            WHERE a.id = ?
            """;
        try (PreparedStatement stmt = conn().prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return Optional.of(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar agenda", e);
        }
        return Optional.empty();
    }

    /**
     * Busca agendas disponíveis APENAS das disciplinas em que o aluno está matriculado.
     */
    public List<Agenda> listarDisponiveisPorAluno(Long alunoId) {
        String sql = """
            SELECT a.*, d.nome AS disciplina_nome
            FROM agendas a
            JOIN disciplinas d ON a.disciplina_id = d.id
            JOIN aluno_disciplina ad ON ad.disciplina_id = d.id
            WHERE ad.aluno_id = ?
              AND a.data_hora_inicio > NOW()
              AND a.vagas_ocupadas < a.vagas_totais
              AND (a.cancelada IS NULL OR a.cancelada = FALSE)
            ORDER BY a.data_hora_inicio
            """;
        List<Agenda> lista = new ArrayList<>();
        try (PreparedStatement stmt = conn().prepareStatement(sql)) {
            stmt.setLong(1, alunoId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar agendas disponíveis: " + e.getMessage(), e);
        }
        return lista;
    }

    /** Busca agendas de um monitor específico, trazendo o NOME da disciplina */
    public List<Agenda> listarPorMonitor(Long monitorId) {
        String sql = """
            SELECT a.*, d.nome AS disciplina_nome 
            FROM agendas a
            JOIN disciplinas d ON a.disciplina_id = d.id
            WHERE a.monitor_id = ?
              AND (a.cancelada IS NULL OR a.cancelada = FALSE)
            ORDER BY a.data_hora_inicio
            """;
        List<Agenda> lista = new ArrayList<>();
        try (PreparedStatement stmt = conn().prepareStatement(sql)) {
            stmt.setLong(1, monitorId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar agendas do monitor", e);
        }
        return lista;
    }

    /** Busca todas as agendas de uma disciplina, incluindo canceladas. */
    public List<Agenda> listarPorDisciplina(Long disciplinaId) {
        String sql = """
            SELECT a.*, d.nome AS disciplina_nome,
                   u.nome AS monitor_nome
            FROM agendas a
            JOIN disciplinas d ON a.disciplina_id = d.id
            JOIN usuarios u ON a.monitor_id = u.id
            WHERE a.disciplina_id = ?
            ORDER BY a.data_hora_inicio DESC
            """;
        List<Agenda> lista = new ArrayList<>();
        try (PreparedStatement stmt = conn().prepareStatement(sql)) {
            stmt.setLong(1, disciplinaId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Agenda ag = mapear(rs);
                try { ag.setMonitorNome(rs.getString("monitor_nome")); } catch (SQLException ignored) {}
                lista.add(ag);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar agendas da disciplina: " + e.getMessage(), e);
        }
        return lista;
    }

    public void atualizarVagas(Agenda agenda) {
        String sql = "UPDATE agendas SET vagas_ocupadas = ? WHERE id = ?";
        try (PreparedStatement stmt = conn().prepareStatement(sql)) {
            stmt.setInt(1, agenda.getVagasOcupadas());
            stmt.setLong(2, agenda.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar vagas", e);
        }
    }

    /**
     * Marca a agenda como cancelada e salva a justificativa.
     */
    public void cancelarSessao(Long agendaId, String justificativa) {
        String sql = "UPDATE agendas SET cancelada = TRUE, justificativa = ? WHERE id = ?";
        try (PreparedStatement stmt = conn().prepareStatement(sql)) {
            stmt.setString(1, justificativa);
            stmt.setLong(2, agendaId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cancelar sessão de monitoria", e);
        }
    }

    /**
     * ✅ NOVO: Remove todas as agendas de uma disciplina.
     * Usado pela exclusão em cascata do DisciplinaService.
     */
    public void excluirPorDisciplina(Long disciplinaId) {
        String sql = "DELETE FROM agendas WHERE disciplina_id = ?";
        try (PreparedStatement stmt = conn().prepareStatement(sql)) {
            stmt.setLong(1, disciplinaId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir agendas da disciplina: " + e.getMessage(), e);
        }
    }

    private Agenda mapear(ResultSet rs) throws SQLException {
        Agenda a = new Agenda();
        a.setId(rs.getLong("id"));
        a.setDataHoraInicio(rs.getTimestamp("data_hora_inicio").toLocalDateTime());
        a.setDataHoraFim(rs.getTimestamp("data_hora_fim").toLocalDateTime());
        a.setLocal(rs.getString("local"));
        a.setLink(rs.getString("link"));
        a.setVagasTotais(rs.getInt("vagas_totais"));
        a.setVagasOcupadas(rs.getInt("vagas_ocupadas"));
        a.setCancelada(rs.getBoolean("cancelada"));
        try { a.setJustificativa(rs.getString("justificativa")); } catch (SQLException ignored) {}

        Disciplina d = new Disciplina();
        d.setId(rs.getLong("disciplina_id"));
        try {
            d.setNome(rs.getString("disciplina_nome"));
        } catch (SQLException e) {
            // nome não disponível na query
        }
        a.setDisciplina(d);

        return a;
    }
}