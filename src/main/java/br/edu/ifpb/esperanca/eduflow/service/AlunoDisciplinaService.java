package br.edu.ifpb.esperanca.eduflow.service;

import br.edu.ifpb.esperanca.eduflow.domain.entities.Disciplina;
import br.edu.ifpb.esperanca.eduflow.domain.exceptions.BusinessException;
import br.edu.ifpb.esperanca.eduflow.repository.AlunoDisciplinaRepository;

import java.util.List;

public class AlunoDisciplinaService {

    private final AlunoDisciplinaRepository repo = new AlunoDisciplinaRepository();

    public void matricular(Long alunoId, Long disciplinaId) {
        if (repo.estaMatriculado(alunoId, disciplinaId))
            throw new BusinessException("Você já está matriculado nesta disciplina.");
        repo.matricular(alunoId, disciplinaId);
    }

    public void cancelarMatricula(Long alunoId, Long disciplinaId) {
        if (!repo.estaMatriculado(alunoId, disciplinaId))
            throw new BusinessException("Você não está matriculado nesta disciplina.");
        repo.cancelarMatricula(alunoId, disciplinaId);
    }

    public List<Disciplina> listarMatriculadas(Long alunoId) {
        return repo.listarMatriculadas(alunoId);
    }

    public List<Disciplina> listarDisponiveis(Long alunoId) {
        return repo.listarDisponiveis(alunoId);
    }
}
