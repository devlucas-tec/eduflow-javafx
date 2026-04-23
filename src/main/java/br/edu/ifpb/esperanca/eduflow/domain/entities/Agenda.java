package br.edu.ifpb.esperanca.eduflow.domain.entities;

import java.time.LocalDateTime;

public class Agenda {

    private Long id;
    private LocalDateTime dataHoraInicio;
    private LocalDateTime dataHoraFim;
    private String local;
    private String link;
    private int vagasTotais;
}
