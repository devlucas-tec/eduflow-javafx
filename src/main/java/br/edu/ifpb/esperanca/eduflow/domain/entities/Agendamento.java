package br.edu.ifpb.esperanca.eduflow.domain.entities;

import br.edu.ifpb.esperanca.eduflow.domain.enums.StatusAgendamento;

import java.time.LocalDateTime;

public class Agendamento {

    private Long id;
    private String assunto;
    private LocalDateTime dataHoraSolicitacao;
    private StatusAgendamento status;

    public boolean podeCancelar() {

        return true;
    }

    public void cancelar () {

    }
}
