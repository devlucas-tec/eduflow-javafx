package br.edu.ifpb.esperanca.eduflow.domain.entities;

import br.edu.ifpb.esperanca.eduflow.domain.enums.Role;
import br.edu.ifpb.esperanca.eduflow.domain.enums.StatusAgendamento;
import br.edu.ifpb.esperanca.eduflow.domain.exceptions.BusinessException;

import java.util.ArrayList;
import java.util.List;

/**
 * Monitor é um Aluno com responsabilidades adicionais.
 * Gerencia sua agenda de horários e registra atendimentos.
 * RN07, RN08, RN06 tratados aqui (Modelo Rico).
 */
public class Monitor extends Aluno {

    private List<Disciplina> disciplinasVinculadas = new ArrayList<>();
    private List<Agenda> agendas = new ArrayList<>();

    public Monitor() {
        this.role = Role.MONITOR;
    }

    public Monitor(Long id, String nome, String email, String senhaHash, String matricula) {
        super(id, nome, email, senhaHash, matricula);
        this.role = Role.MONITOR;
    }

    // ── RN07: Vínculo obrigatório ─────────────────────────────────────────────

    /** Verifica se o monitor está formalmente vinculado à disciplina (RN07). */
    public boolean estaVinculadoA(Disciplina disciplina) {
        return disciplinasVinculadas.stream()
                .anyMatch(d -> d.getId().equals(disciplina.getId()));
    }

    /**
     * Cadastra um horário de monitoria para uma disciplina.
     * Valida RN07 (vínculo) e RN10 (local ou link obrigatório).
     */
    public Agenda cadastrarAgenda(Agenda agenda, Disciplina disciplina) {
        if (!estaVinculadoA(disciplina))
            throw new BusinessException(
                    "Monitor não está vinculado a esta disciplina no semestre atual. (RN07)");

        if ((agenda.getLocal() == null || agenda.getLocal().isBlank())
                && (agenda.getLink() == null || agenda.getLink().isBlank()))
            throw new BusinessException(
                    "Todo horário deve ter local físico ou link de videoconferência. (RN10)");

        agenda.setMonitor(this);
        agenda.setDisciplina(disciplina);
        return agenda;
    }

    // ── RN08: Auto-agendamento bloqueado ──────────────────────────────────────

    /**
     * Sobrescreve agendarMonitoria para bloquear auto-agendamento (RN08).
     * Se o monitor estiver escalado para aquele horário, lança exceção.
     */
    @Override
    public Agendamento agendarMonitoria(Agenda agenda,
                                        java.util.List<Agendamento> agendamentosAtivos,
                                        String assunto) {
        boolean estaEscalado = agendas.stream()
                .anyMatch(a -> a.getId() != null && a.getId().equals(agenda.getId()));
        if (estaEscalado)
            throw new BusinessException(
                    "Um monitor não pode se agendar como aluno em horário que ele mesmo está escalado. (RN08)");

        return super.agendarMonitoria(agenda, agendamentosAtivos, assunto);
    }

    // ── RN06: Registro de atendimento ─────────────────────────────────────────

    /**
     * Registra desfecho de uma monitoria: presença e conteúdo.
     * Após este registro, as horas ficam disponíveis para validação do professor (RN06).
     */
    public Atendimento registrarAtendimento(Agendamento agendamento,
                                            boolean presenca,
                                            String conteudoTrabalhado) {
        if (agendamento.getStatus() != StatusAgendamento.CONFIRMADO
                && agendamento.getStatus() != StatusAgendamento.PENDENTE)
            throw new BusinessException(
                    "Só é possível registrar atendimento em agendamentos PENDENTE ou CONFIRMADO.");

        // Conteúdo obrigatório apenas quando o aluno esteve presente
        if (presenca && (conteudoTrabalhado == null || conteudoTrabalhado.isBlank()))
            throw new BusinessException(
                    "O conteúdo trabalhado é obrigatório quando o aluno esteve presente. (RN06)");
        if (!presenca && (conteudoTrabalhado == null || conteudoTrabalhado.isBlank()))
            conteudoTrabalhado = "Aluno faltou";

        agendamento.setStatus(presenca ? StatusAgendamento.REALIZADO : StatusAgendamento.FALTOU);

        Atendimento at = new Atendimento();
        at.setPresenca(presenca);
        at.setConteudoTrabalhado(conteudoTrabalhado);
        at.setDataHoraRegistro(java.time.LocalDateTime.now());
        at.setAgendamento(agendamento);
        return at;
    }

    // ── Getters / Setters ─────────────────────────────────────────────────────

    public List<Disciplina> getDisciplinasVinculadas() { return disciplinasVinculadas; }
    public void setDisciplinasVinculadas(List<Disciplina> v) { this.disciplinasVinculadas = v; }
    public List<Agenda> getAgendas() { return agendas; }
    public void setAgendas(List<Agenda> v) { this.agendas = v; }
}
