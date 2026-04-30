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

    /**
     * Retorna um resumo de carga horária para uma disciplina do professor.
     * Cada aula = 1h (simplificação acadêmica).
     */
    public String resumoCargaHoraria(Long professorId, Long disciplinaId) {
        int regular  = aulaRepository.contarPorProfessorETipo(professorId, disciplinaId, "REGULAR");
        int reposicao = aulaRepository.contarPorProfessorETipo(professorId, disciplinaId, "REPOSICAO");
        int extra    = aulaRepository.contarPorProfessorETipo(professorId, disciplinaId, "EXTRA");
        int total    = regular + reposicao + extra;
        return String.format("Total: %dh  |  Regulares: %dh  |  Reposições: %dh  |  Extras: %dh",
                total, regular, reposicao, extra);
    }

    public List<Aula> listarPorProfessorEDisciplina(Long professorId, Long disciplinaId) {
        return aulaRepository.listarPorProfessorEDisciplina(professorId, disciplinaId);
    }

    /** Retorna int[]{total, cumpridas} para exibição de carga horária. */
    public int[] calcularCargaHoraria(Long professorId, Long disciplinaId) {
        return aulaRepository.contarCargaHoraria(professorId, disciplinaId);
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
