package br.edu.ifpb.esperanca.eduflow.repository;

import br.edu.ifpb.esperanca.eduflow.domain.entities.Agendamento;
import br.edu.ifpb.esperanca.eduflow.domain.entities.Atendimento;
import br.edu.ifpb.esperanca.eduflow.domain.enums.StatusAgendamento;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AtendimentoRepository {

    private Connection conn() {
        return ConectionFactory.getConnection();
    }

    public Atendimento salvar(Atendimento at) {
        String sql = """
            INSERT INTO atendimentos (conteudo_trabalhado, presenca, data_hora_registro, agendamento_id)
            VALUES (?, ?, ?, ?)
            ON CONFLICT (agendamento_id) DO UPDATE
                SET conteudo_trabalhado = EXCLUDED.conteudo_trabalhado,
                    presenca            = EXCLUDED.presenca,
                    data_hora_registro  = EXCLUDED.data_hora_registro
            """;
        try (PreparedStatement stmt = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, at.getConteudoTrabalhado());
            stmt.setBoolean(2, at.isPresenca());
            stmt.setTimestamp(3, Timestamp.valueOf(at.getDataHoraRegistro()));
            stmt.setLong(4, at.getAgendamento().getId());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) at.setId(rs.getLong(1));
            return at;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar atendimento: " + e.getMessage(), e);
        }
    }

    /**
     * Lista atendimentos das disciplinas do professor para validação.
     *
     * Parte da tabela agendamentos (status REALIZADO/FALTOU) com LEFT JOIN
     * em atendimentos — garante que o professor veja os registros mesmo que
     * o INSERT em atendimentos não tenha ocorrido.
     * Inclui d.id para que validarAtendimento() não lance BusinessException.
     */
    public List<Atendimento> listarParaValidacaoPorProfessor(Long professorId) {
        String sql = """
            SELECT
                COALESCE(at.id, 0)                  AS id,
                at.conteudo_trabalhado,
                COALESCE(at.presenca, false)         AS presenca,
                at.data_hora_registro,
                ag.id                               AS ag_id,
                ag.assunto,
                ag.status,
                ag.aluno_id,
                ag.agenda_id,
                ag.justificativa,
                u.nome                              AS aluno_nome,
                a.data_hora_inicio                  AS agenda_inicio,
                d.id                                AS disciplina_id,
                d.nome                              AS disciplina_nome
            FROM agendamentos ag
            JOIN agendas a              ON a.id  = ag.agenda_id
            JOIN disciplinas d          ON d.id  = a.disciplina_id
            JOIN usuarios u             ON u.id  = ag.aluno_id
            JOIN professor_disciplina pd ON pd.disciplina_id = d.id
            LEFT JOIN atendimentos at   ON at.agendamento_id = ag.id
            WHERE pd.professor_id = ?
              AND ag.status IN ('REALIZADO', 'FALTOU')
            ORDER BY ag.id DESC
            """;
        List<Atendimento> lista = new ArrayList<>();
        try (PreparedStatement stmt = conn().prepareStatement(sql)) {
            stmt.setLong(1, professorId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar atendimentos para validação: " + e.getMessage(), e);
        }
        return lista;
    }

    private Atendimento mapear(ResultSet rs) throws SQLException {
        Atendimento at = new Atendimento();
        at.setId(rs.getLong("id"));

        String conteudo = rs.getString("conteudo_trabalhado");
        at.setConteudoTrabalhado(conteudo != null ? conteudo : "");
        at.setPresenca(rs.getBoolean("presenca"));

        Timestamp registro = rs.getTimestamp("data_hora_registro");
        at.setDataHoraRegistro(registro != null ? registro.toLocalDateTime() : null);

        Agendamento ag = new Agendamento();
        ag.setId(rs.getLong("ag_id"));
        ag.setAssunto(rs.getString("assunto"));
        ag.setStatus(StatusAgendamento.valueOf(rs.getString("status")));
        ag.setJustificativa(rs.getString("justificativa"));

        br.edu.ifpb.esperanca.eduflow.domain.entities.Aluno aluno =
                new br.edu.ifpb.esperanca.eduflow.domain.entities.Aluno();
        aluno.setId(rs.getLong("aluno_id"));
        try { aluno.setNome(rs.getString("aluno_nome")); } catch (SQLException ignored) {}
        ag.setAluno(aluno);

        br.edu.ifpb.esperanca.eduflow.domain.entities.Agenda agenda =
                new br.edu.ifpb.esperanca.eduflow.domain.entities.Agenda();
        agenda.setId(rs.getLong("agenda_id"));
        try {
            Timestamp ts = rs.getTimestamp("agenda_inicio");
            if (ts != null) agenda.setDataHoraInicio(ts.toLocalDateTime());
        } catch (SQLException ignored) {}
        try {
            br.edu.ifpb.esperanca.eduflow.domain.entities.Disciplina d =
                    new br.edu.ifpb.esperanca.eduflow.domain.entities.Disciplina();
            d.setId(rs.getLong("disciplina_id"));
            d.setNome(rs.getString("disciplina_nome"));
            agenda.setDisciplina(d);
        } catch (SQLException ignored) {}
        ag.setAgenda(agenda);

        at.setAgendamento(ag);
        return at;
    }

    /** Remove o registro de atendimento de um agendamento (usado ao rejeitar). */
    public void deletarPorAgendamento(Long agendamentoId) {
        String sql = "DELETE FROM atendimentos WHERE agendamento_id = ?";
        try (PreparedStatement stmt = conn().prepareStatement(sql)) {
            stmt.setLong(1, agendamentoId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar atendimento: " + e.getMessage(), e);
        }
    }
}