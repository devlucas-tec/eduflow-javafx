package br.edu.ifpb.esperanca.eduflow.controller;

import br.edu.ifpb.esperanca.eduflow.MainApp;
import br.edu.ifpb.esperanca.eduflow.domain.entities.*;
import br.edu.ifpb.esperanca.eduflow.service.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.List;

public class ProfessorDashboardController {

    @FXML private Label lblBemVindo;

    // --- Aba Disciplinas ---
    @FXML private TableView<Disciplina> tabelaDisciplinas;
    @FXML private TableColumn<Disciplina, String> colDisciplinaNome;
    @FXML private TableColumn<Disciplina, String> colDisciplinaCodigo;
    @FXML private TableColumn<Disciplina, String> colDisciplinaSemestre;
    @FXML private Text msgDisciplinas;

    // --- Aba Validação ---
    @FXML private TableView<Agendamento> tabelaParaValidar;
    @FXML private TableColumn<Agendamento, String> colStatus;
    @FXML private TableColumn<Agendamento, String> colAssunto;
    @FXML private TableColumn<Agendamento, String> colAluno;
    @FXML private Text errorMessage;
    @FXML private Text successMessage;

    private final AgendamentoService agendamentoService = new AgendamentoService();
    private final ProfessorDisciplinaService professorDisciplinaService = new ProfessorDisciplinaService();
    private Professor professorLogado;

    @FXML
    public void initialize() {
        professorLogado = (Professor) SessionManager.getInstance().getUsuarioLogado();
        lblBemVindo.setText("Prof. " + professorLogado.getNome());

        configurarTabelaDisciplinas();
        carregarDisciplinas();

        configurarTabelaValidacao();
        carregarAgendamentosParaValidar();
    }

    // ===================== DISCIPLINAS =====================

    private void configurarTabelaDisciplinas() {
        colDisciplinaNome.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNome()));
        colDisciplinaCodigo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCodigo()));
        colDisciplinaSemestre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSemestreLetivo()));
    }

    private void carregarDisciplinas() {
        List<Disciplina> disciplinas = professorDisciplinaService.listarPorProfessor(professorLogado.getId());
        professorLogado.setDisciplinas(disciplinas);

        if (disciplinas.isEmpty()) {
            if (msgDisciplinas != null) {
                msgDisciplinas.setText("Nenhuma disciplina vinculada. Solicite ao administrador.");
                msgDisciplinas.setVisible(true);
            }
        }
        tabelaDisciplinas.setItems(FXCollections.observableArrayList(disciplinas));
    }

    // ===================== VALIDAÇÃO =====================

    private void configurarTabelaValidacao() {
        if (colStatus != null)
            colStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus().name()));
        if (colAssunto != null)
            colAssunto.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAssunto()));
        if (colAluno != null)
            colAluno.setCellValueFactory(c -> {
                Aluno aluno = c.getValue().getAluno();
                return new SimpleStringProperty(aluno != null ? aluno.getNome() : "—");
            });
    }

    private void carregarAgendamentosParaValidar() {
        if (tabelaParaValidar == null) return;
        tabelaParaValidar.setItems(FXCollections.observableArrayList(List.of()));
    }

    @FXML
    public void handleValidar() {
        if (tabelaParaValidar == null) return;
        Agendamento selecionado = tabelaParaValidar.getSelectionModel().getSelectedItem();
        if (selecionado == null) { showError("Selecione um agendamento para validar."); return; }
        try {
            Agenda agenda = selecionado.getAgenda();
            Disciplina disciplina = agenda != null ? agenda.getDisciplina() : null;
            if (disciplina == null) { showError("Disciplina do agendamento não encontrada."); return; }
            professorLogado.validarAtendimento(selecionado, disciplina);
            showSuccess("Atendimento validado com sucesso! (RN02/RN09)");
            carregarAgendamentosParaValidar();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML
    public void handleLogout() throws IOException {
        SessionManager.getInstance().encerrarSessao();
        MainApp.setRoot("login");
    }

    private void showError(String msg) { if (errorMessage != null) { errorMessage.setText(msg); errorMessage.setVisible(true); } }
    private void showSuccess(String msg) { if (successMessage != null) { successMessage.setText(msg); successMessage.setVisible(true); } }
}
