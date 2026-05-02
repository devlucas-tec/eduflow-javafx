package br.edu.ifpb.esperanca.eduflow.repository;

import br.edu.ifpb.esperanca.eduflow.domain.entities.Agendamento;
import br.edu.ifpb.esperanca.eduflow.domain.entities.Agenda;
import br.edu.ifpb.esperanca.eduflow.domain.enums.StatusAgendamento;
import br.edu.ifpb.esperanca.eduflow.domain.entities.Aluno;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AgendamentoRepository {

    private Connection conn() {
        return ConectionFactory.getConnection();
    }

    public Agendamento salvar(Agendamento ag) {
        String sql = """
            INSERT INTO agendamentos (assunto, data_hora_solicitacao, status, aluno_id, agenda_id)
            VALUES (?, ?, ?, ?, ?)
            """;
        try (PreparedStatement stmt = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, ag.getAssunto());
            stmt.setTimestamp(2, Timestamp.valueOf(ag.getDataHoraSolicitacao()));
            stmt.setString(3, ag.getStatus().name());
            stmt.setLong(4, ag.getAluno().getId());
            stmt.setLong(5, ag.getAgenda().getId());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) ag.setId(rs.getLong(1));
            return ag;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar agendamento", e);
        }
    }

    public void atualizarStatus(Long id, StatusAgendamento status) {
        String sql = "UPDATE agendamentos SET status = ? WHERE id = ?";
        try (PreparedStatement stmt = conn().prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setLong(2, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar status do agendamento", e);
        }
    }

    /** Lista agendamentos ativos de um aluno. */
    public List<Agendamento> listarAtivosPorAluno(Long alunoId) {
        String sql = """
            SELECT ag.id AS ag_id, ag.assunto, ag.data_hora_solicitacao, ag.status, ag.aluno_id, ag.agenda_id, ag.justificativa,
                   a.data_hora_inicio AS agenda_inicio,
                   a.data_hora_fim    AS agenda_fim,
                   u.nome             AS aluno_nome
            FROM agendamentos ag
            JOIN agendas a ON ag.agenda_id = a.id
            JOIN usuarios u ON ag.aluno_id = u.id
            WHERE ag.aluno_id = ? AND ag.status IN ('PENDENTE', 'CONFIRMADO')
            ORDER BY ag.data_hora_solicitacao DESC
            """;
        return listarComFiltroComAgendaCompleta(sql, alunoId);
    }

    /** Lista todos os agendamentos de um aluno. */
    public List<Agendamento> listarTodosPorAluno(Long alunoId) {
        String sql = """
            SELECT ag.id AS ag_id, ag.assunto, ag.data_hora_solicitacao, ag.status, ag.aluno_id, ag.agenda_id, ag.justificativa,
                   a.data_hora_inicio AS agenda_inicio,
                   a.data_hora_fim    AS agenda_fim,
                   u.nome             AS aluno_nome
            FROM agendamentos ag
            JOIN agendas a ON ag.agenda_id = a.id
            JOIN usuarios u ON ag.aluno_id = u.id
            WHERE ag.aluno_id = ?
            ORDER BY ag.data_hora_solicitacao DESC
            """;
        return listarComFiltroComAgendaCompleta(sql, alunoId);
    }

    /** Lista agendamentos de uma agenda específica. */
    public List<Agendamento> listarPorAgenda(Long agendaId) {
        String sql = """
        SELECT ag.id AS ag_id, ag.assunto, ag.data_hora_solicitacao, ag.status, ag.aluno_id, ag.agenda_id, ag.justificativa,
               a.data_hora_inicio AS agenda_inicio,
               a.data_hora_fim    AS agenda_fim,
               u.nome             AS aluno_nome
        FROM agendamentos ag
        JOIN agendas a ON ag.agenda_id = a.id
        JOIN usuarios u ON ag.aluno_id = u.id
        WHERE ag.agenda_id = ?
          AND ag.status IN ('PENDENTE', 'CONFIRMADO')
        ORDER BY ag.data_hora_solicitacao
        """;
        return listarComFiltroComAgendaCompleta(sql, agendaId);
    }

    /**
     * MÉTODO QUE ESTAVA VAZIO:
     * Agora busca PENDENTE ou CONFIRMADO para o monitor poder registrar.
     */
    public List<Agendamento> listarParaValidacaoPorMonitor(Long monitorId) {
        String sql = """
            SELECT ag.id AS ag_id, ag.assunto, ag.data_hora_solicitacao, ag.status, ag.aluno_id, ag.agenda_id, ag.justificativa, u.nome AS aluno_nome,
                   a.data_hora_inicio AS agenda_inicio,
                   a.data_hora_fim    AS agenda_fim
            FROM agendamentos ag
            JOIN agendas a ON a.id = ag.agenda_id
            JOIN usuarios u ON u.id = ag.aluno_id
            WHERE a.monitor_id = ? AND ag.status IN ('PENDENTE', 'CONFIRMADO', 'CANCELADO_ALUNO', 'CANCELADO_MONITOR')
            ORDER BY a.data_hora_inicio ASC
            """;

        List<Agendamento> lista = new ArrayList<>();
        try (PreparedStatement stmt = conn().prepareStatement(sql)) {
            stmt.setLong(1, monitorId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) lista.add(mapearComAgendaCompleta(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar atendimentos pendentes", e);
        }
        return lista;
    }

    private List<Agendamento> listarComFiltro(String sql, Long parametro) {
        List<Agendamento> lista = new ArrayList<>();
        try (PreparedStatement stmt = conn().prepareStatement(sql)) {
            stmt.setLong(1, parametro);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(mapearCompleto(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar agendamentos", e);
        }
        return lista;
    }

    /** Variante que usa mapearComAgendaCompleta (aliases agenda_inicio / agenda_fim). */
    private List<Agendamento> listarComFiltroComAgendaCompleta(String sql, Long parametro) {
        List<Agendamento> lista = new ArrayList<>();
        try (PreparedStatement stmt = conn().prepareStatement(sql)) {
            stmt.setLong(1, parametro);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(mapearComAgendaCompleta(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar agendamentos", e);
        }
        return lista;
    }

    public void cancelarAgendamento(Long id, StatusAgendamento status, String justificativa) {
        String sql = "UPDATE agendamentos SET status = ?, justificativa = ? WHERE id = ?";
        try (PreparedStatement stmt = conn().prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setString(2, justificativa);
            stmt.setLong(3, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cancelar agendamento", e);
        }
    }

    /**
     * Mapeador para queries que usam aliases agenda_inicio / agenda_fim.
     * Garante que dataHoraInicio da agenda seja lido corretamente
     * sem conflito com data_hora_solicitacao do agendamento.
     */
    private Agendamento mapearComAgendaCompleta(ResultSet rs) throws SQLException {
        Agendamento ag = mapearCompleto(rs);
        Agenda agenda = ag.getAgenda();
        if (agenda == null) { agenda = new Agenda(); agenda.setId(rs.getLong("agenda_id")); }
        try {
            Timestamp inicio = rs.getTimestamp("agenda_inicio");
            if (inicio != null) agenda.setDataHoraInicio(inicio.toLocalDateTime());
            Timestamp fim = rs.getTimestamp("agenda_fim");
            if (fim != null) agenda.setDataHoraFim(fim.toLocalDateTime());
        } catch (SQLException ignored) {}
        ag.setAgenda(agenda);
        return ag;
    }

    /**
     * Mapeador unificado que evita NullPointerException e carrega nomes.
     */
    private Agendamento mapearCompleto(ResultSet rs) throws SQLException {
        Agendamento ag = new Agendamento();
        ag.setId(rs.getLong("ag_id"));
        ag.setAssunto(rs.getString("assunto"));

        Timestamp solicitacao = rs.getTimestamp("data_hora_solicitacao");
        if (solicitacao != null) {
            ag.setDataHoraSolicitacao(solicitacao.toLocalDateTime());
        }

        ag.setStatus(StatusAgendamento.valueOf(rs.getString("status")));

        // Hidrata o Aluno
        Aluno aluno = new Aluno();
        aluno.setId(rs.getLong("aluno_id"));
        try {
            // Tenta pegar o nome se o JOIN de usuários foi feito
            aluno.setNome(rs.getString("aluno_nome"));
        } catch (SQLException e) {
            aluno.setNome("Aluno");
        }
        ag.setAluno(aluno);

        // Hidrata a Agenda (apenas o id — mapearComAgendaCompleta preenche as datas via aliases)
        Agenda agenda = new Agenda();
        agenda.setId(rs.getLong("agenda_id"));
        ag.setAgenda(agenda);
        ag.setJustificativa(rs.getString("justificativa"));
        return ag;
    }
}