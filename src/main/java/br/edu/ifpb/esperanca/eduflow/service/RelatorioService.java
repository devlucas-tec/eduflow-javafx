package br.edu.ifpb.esperanca.eduflow.service;

import br.edu.ifpb.esperanca.eduflow.domain.entities.Agendamento;
import br.edu.ifpb.esperanca.eduflow.domain.enums.StatusAgendamento;
import br.edu.ifpb.esperanca.eduflow.repository.AgendamentoRepository;

import java.util.List;

public class RelatorioService {

    private final AgendamentoRepository agendamentoRepository = new AgendamentoRepository();

    /** Total de atendimentos realizados pelo monitor (status REALIZADO). */
    public long totalAtendimentos(Long monitorId) {
        return agendamentoRepository.listarParaValidacaoPorMonitor(monitorId)
                .stream()
                .filter(a -> a.getStatus() == StatusAgendamento.REALIZADO)
                .count();
    }

    /** Total de faltas registradas (status FALTOU). */
    public long totalFaltas(Long monitorId) {
        return agendamentoRepository.listarParaValidacaoPorMonitor(monitorId)
                .stream()
                .filter(a -> a.getStatus() == StatusAgendamento.FALTOU)
                .count();
    }

    /** Lista todos os agendamentos finalizados (REALIZADO ou FALTOU) do monitor. */
    public List<Agendamento> listarAtendimentosFinalizados(Long monitorId) {
        return agendamentoRepository.listarParaValidacaoPorMonitor(monitorId);
    }

    /**
     * Horas de monitoria cumpridas.
     * Considera cada atendimento REALIZADO como 1 hora (simplificação acadêmica).
     * Em produção, calcularia pela duração real da agenda.
     */
    public long horasDeMonitoria(Long monitorId) {
        return totalAtendimentos(monitorId);
    }
}
