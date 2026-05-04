package br.edu.ifpb.esperanca.eduflow.service;

import br.edu.ifpb.esperanca.eduflow.domain.entities.Disciplina;
import br.edu.ifpb.esperanca.eduflow.domain.exceptions.BusinessException;
import br.edu.ifpb.esperanca.eduflow.repository.MonitorDisciplinaRepository;

import java.util.Optional;

public class MonitorDisciplinaService {

    private final MonitorDisciplinaRepository repo = new MonitorDisciplinaRepository();

    /**
     * Vincula monitor a uma disciplina.
     * RN: monitor só pode estar vinculado a uma disciplina por vez.
     * Substituição automática do vínculo anterior.
     */
    public void vincular(Long monitorId, Long disciplinaId) {
        if (monitorId == null || disciplinaId == null)
            throw new BusinessException("Monitor e disciplina são obrigatórios.");
        repo.vincular(monitorId, disciplinaId);
    }

    /** Remove o vínculo do monitor com sua disciplina atual. */
    public void desvincular(Long monitorId) {
        if (!repo.estaVinculado(monitorId))
            throw new BusinessException("Este monitor não possui disciplina vinculada.");
        repo.desvincular(monitorId);
    }

    public Optional<Disciplina> buscarDisciplinaDoMonitor(Long monitorId) {
        return repo.buscarDisciplinaDoMonitor(monitorId);
    }

    public boolean estaVinculado(Long monitorId) {
        return repo.estaVinculado(monitorId);
    }
}
