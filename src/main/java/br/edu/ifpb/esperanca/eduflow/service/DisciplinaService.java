package br.edu.ifpb.esperanca.eduflow.service;

import br.edu.ifpb.esperanca.eduflow.domain.entities.Disciplina;
import br.edu.ifpb.esperanca.eduflow.domain.exceptions.BusinessException;
import br.edu.ifpb.esperanca.eduflow.repository.DisciplinaRepository;

import java.util.List;

public class DisciplinaService {

    private final DisciplinaRepository disciplinaRepository;

    public DisciplinaService() {
        this.disciplinaRepository = new DisciplinaRepository();
    }

    public Disciplina cadastrar(Disciplina disciplina) {
        if (disciplina.getNome() == null || disciplina.getNome().isBlank())
            throw new BusinessException("Nome da disciplina é obrigatório.");
        if (disciplina.getCodigo() == null || disciplina.getCodigo().isBlank())
            throw new BusinessException("Código da disciplina é obrigatório.");
        return disciplinaRepository.salvar(disciplina);
    }

    public List<Disciplina> listarTodas() {
        return disciplinaRepository.listarTodas();
    }

    public List<Disciplina> listarPorMonitor(Long monitorId) {
        return disciplinaRepository.listarPorMonitor(monitorId);
    }

    public List<Disciplina> listarPorProfessor(Long professorId) {
        return disciplinaRepository.listarPorProfessor(professorId);
    }

    public void excluir(Long id) {
        disciplinaRepository.excluir(id);
    }
}
