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
        // SQL com JOIN para pegar o nome da disciplina ao buscar por ID
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

    /** Busca agendas futuras com vagas disponíveis, trazendo o NOME da disciplina */
    public List<Agenda> listarDisponiveis() {
        String sql = """
            SELECT a.*, d.nome AS disciplina_nome 
            FROM agendas a
            JOIN disciplinas d ON a.disciplina_id = d.id
            WHERE a.data_hora_inicio > NOW()
              AND a.vagas_ocupadas < a.vagas_totais
              AND (a.cancelada IS NULL OR a.cancelada = FALSE)
            ORDER BY a.data_hora_inicio
            """;
        List<Agenda> lista = new ArrayList<>();
        try (PreparedStatement stmt = conn().prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar agendas disponíveis", e);
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
     * Os agendamentos vinculados são cancelados separadamente pelo AgendamentoService.
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

        // SOLUÇÃO: Criamos o objeto Disciplina e preenchemos o nome que veio do JOIN
        Disciplina d = new Disciplina();
        d.setId(rs.getLong("disciplina_id"));

        // Tentamos pegar o nome da disciplina vindo do alias 'disciplina_nome' no SQL
        try {
            d.setNome(rs.getString("disciplina_nome"));
        } catch (SQLException e) {
            // Caso o SQL não tenha o join, o nome ficará nulo
        }

        a.setDisciplina(d);

        return a;
    }
}