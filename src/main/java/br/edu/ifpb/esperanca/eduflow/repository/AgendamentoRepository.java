package br.edu.ifpb.esperanca.eduflow.repository;

import br.edu.ifpb.esperanca.eduflow.domain.entities.Agendamento;
import br.edu.ifpb.esperanca.eduflow.domain.enums.StatusAgendamento;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AgendamentoRepository {

    private final Connection conn;

    public AgendamentoRepository() {
        this.conn = ConectionFactory.getConnection();
    }

    public Agendamento salvar(Agendamento ag) {
        String sql = """
            INSERT INTO agendamentos (assunto, data_hora_solicitacao, status, aluno_id, agenda_id)
            VALUES (?, ?, ?, ?, ?)
            """;
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
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
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setLong(2, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar status do agendamento", e);
        }
    }

    /** Lista agendamentos ativos (PENDENTE/CONFIRMADO) de um aluno. */
    public List<Agendamento> listarAtivosPorAluno(Long alunoId) {
        String sql = """
            SELECT * FROM agendamentos
            WHERE aluno_id = ? AND status IN ('PENDENTE', 'CONFIRMADO')
            ORDER BY data_hora_solicitacao DESC
            """;
        return listarComFiltro(sql, alunoId);
    }

    /** Lista todos os agendamentos de um aluno. */
    public List<Agendamento> listarTodosPorAluno(Long alunoId) {
        String sql = "SELECT * FROM agendamentos WHERE aluno_id = ? ORDER BY data_hora_solicitacao DESC";
        return listarComFiltro(sql, alunoId);
    }

    /** Lista agendamentos de uma agenda específica (para o monitor ver quem agendou). */
    public List<Agendamento> listarPorAgenda(Long agendaId) {
        String sql = "SELECT * FROM agendamentos WHERE agenda_id = ? ORDER BY data_hora_solicitacao";
        return listarComFiltro(sql, agendaId);
    }

    /** Lista agendamentos REALIZADOS ou FALTOU vinculados às agendas do monitor — para validação. */
    public List<Agendamento> listarParaValidacaoPorMonitor(Long monitorId) {
        String sql = """
            SELECT ag.* FROM agendamentos ag
            JOIN agendas a ON a.id = ag.agenda_id
            WHERE a.monitor_id = ? AND ag.status IN ('REALIZADO', 'FALTOU')
            ORDER BY ag.data_hora_solicitacao DESC
            """;
        return listarComFiltro(sql, monitorId);
    }

    private List<Agendamento> listarComFiltro(String sql, Long parametro) {
        List<Agendamento> lista = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, parametro);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar agendamentos", e);
        }
        return lista;
    }

    private Agendamento mapear(ResultSet rs) throws SQLException {
        Agendamento ag = new Agendamento();
        ag.setId(rs.getLong("id"));
        ag.setAssunto(rs.getString("assunto"));
        ag.setDataHoraSolicitacao(rs.getTimestamp("data_hora_solicitacao").toLocalDateTime());
        ag.setStatus(StatusAgendamento.valueOf(rs.getString("status")));
        // aluno e agenda são hidratados pela Service quando necessário
        return ag;
    }
}
