package br.edu.ifpb.esperanca.eduflow.service;

import br.edu.ifpb.esperanca.eduflow.domain.entities.Disciplina;
import br.edu.ifpb.esperanca.eduflow.domain.exceptions.BusinessException;
import br.edu.ifpb.esperanca.eduflow.repository.ProfessorDisciplinaRepository;

import java.util.List;

public class ProfessorDisciplinaService {

    private final ProfessorDisciplinaRepository repo = new ProfessorDisciplinaRepository();

    public void vincular(Long professorId, Long disciplinaId) {
        if (professorId == null || disciplinaId == null)
            throw new BusinessException("Professor e disciplina são obrigatórios.");
        if (repo.estaVinculado(professorId, disciplinaId))
            throw new BusinessException("Professor já está vinculado a esta disciplina.");
        repo.vincular(professorId, disciplinaId);
    }

    public void desvincular(Long professorId, Long disciplinaId) {
        if (!repo.estaVinculado(professorId, disciplinaId))
            throw new BusinessException("Professor não está vinculado a esta disciplina.");
        repo.desvincular(professorId, disciplinaId);
    }

    public List<Disciplina> listarPorProfessor(Long professorId) {
        return repo.listarPorProfessor(professorId);
    }
}
