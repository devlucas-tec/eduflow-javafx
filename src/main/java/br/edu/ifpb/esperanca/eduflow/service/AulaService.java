package br.edu.ifpb.esperanca.eduflow.service;

import br.edu.ifpb.esperanca.eduflow.domain.entities.Aula;
import br.edu.ifpb.esperanca.eduflow.domain.exceptions.BusinessException;
import br.edu.ifpb.esperanca.eduflow.repository.AulaRepository;

import java.util.List;

public class AulaService {

    private final AulaRepository aulaRepository = new AulaRepository();

    public Aula cadastrar(Aula aula) {
        validar(aula);
        return aulaRepository.salvar(aula);
    }

    public void editar(Aula aula) {
        if (aula.getId() == null) throw new BusinessException("Aula sem ID para edição.");
        validar(aula);
        aulaRepository.atualizar(aula);
    }

    public void excluir(Long id) {
        aulaRepository.excluir(id);
    }

    public List<Aula> listarPorProfessor(Long professorId) {
        return aulaRepository.listarPorProfessor(professorId);
    }

    public List<Aula> listarTodas() {
        return aulaRepository.listarTodas();
    }

    private void validar(Aula aula) {
        if (aula.getDataHora() == null)
            throw new BusinessException("Data e hora da aula são obrigatórios.");
        if (aula.getTipo() == null || aula.getTipo().isBlank())
            throw new BusinessException("Tipo da aula é obrigatório.");
        if (aula.getProfessorId() == null)
            throw new BusinessException("Professor é obrigatório.");
        if (aula.getDisciplinaId() == null)
            throw new BusinessException("Disciplina é obrigatória.");
    }
}
