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

public class AlunoDashBoardController {

    @FXML private Label lblBemVindo;

    // --- Aba Disciplinas ---
    @FXML private TableView<Disciplina> tabelaMatriculadas;
    @FXML private TableColumn<Disciplina, String> colMatNome;
    @FXML private TableColumn<Disciplina, String> colMatCodigo;
    @FXML private TableColumn<Disciplina, String> colMatSemestre;
    @FXML private TableView<Disciplina> tabelaDisponiveis;
    @FXML private TableColumn<Disciplina, String> colDispNome;
    @FXML private TableColumn<Disciplina, String> colDispCodigo;
    @FXML private TableColumn<Disciplina, String> colDispSemestre;
    @FXML private Text errorDisciplina;
    @FXML private Text successDisciplina;

    // --- Aba Agendar ---
    @FXML private TableView<Agenda> tabelaAgendas;
    @FXML private TableColumn<Agenda, String> colDisciplina;
    @FXML private TableColumn<Agenda, String> colHorario;
    @FXML private TableColumn<Agenda, String> colLocal;
    @FXML private TableColumn<Agenda, String> colVagas;
    @FXML private TextArea txtAssunto;
    @FXML private Text errorMessage;
    @FXML private Text successMessage;

    // --- Aba Meus Agendamentos ---
    @FXML private TableView<Agendamento> tabelaMeusAgendamentos;
    @FXML private TableColumn<Agendamento, String> colMeuStatus;
    @FXML private TableColumn<Agendamento, String> colMeuAssunto;
    @FXML private TableColumn<Agendamento, String> colMeuHorario;
    @FXML private TableColumn<Agendamento, String> colMeuJustificativa;

    private final AgendaService agendaService = new AgendaService();
    private final AgendamentoService agendamentoService = new AgendamentoService();
    private final AlunoDisciplinaService alunoDisciplinaService = new AlunoDisciplinaService();
    private Aluno alunoLogado;

    @FXML
    public void initialize() {
        alunoLogado = (Aluno) SessionManager.getInstance().getUsuarioLogado();
        lblBemVindo.setText("Olá, " + alunoLogado.getNome() + "!");

        tabelaMatriculadas.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabelaDisponiveis.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabelaAgendas.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabelaMeusAgendamentos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        configurarTabelaDisciplinas();
        carregarDisciplinas();

        configurarTabelaAgendas();
        configurarTabelaMeusAgendamentos();
        carregarAgendas();
        carregarMeusAgendamentos();
    }

    // ===================== DISCIPLINAS =====================

    private void configurarTabelaDisciplinas() {
        colMatNome.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNome()));
        colMatCodigo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCodigo()));
        colMatSemestre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSemestreLetivo()));

        colDispNome.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNome()));
        colDispCodigo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCodigo()));
        colDispSemestre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSemestreLetivo()));
    }

    private void carregarDisciplinas() {
        tabelaMatriculadas.setItems(FXCollections.observableArrayList(
                alunoDisciplinaService.listarMatriculadas(alunoLogado.getId())));
        tabelaDisponiveis.setItems(FXCollections.observableArrayList(
                alunoDisciplinaService.listarDisponiveis(alunoLogado.getId())));
    }

    @FXML
    public void handleMatricular() {
        Disciplina selecionada = tabelaDisponiveis.getSelectionModel().getSelectedItem();
        if (selecionada == null) { showErrorDisc("Selecione uma disciplina disponível."); return; }
        try {
            alunoDisciplinaService.matricular(alunoLogado.getId(), selecionada.getId());
            showSuccessDisc("Matriculado em \"" + selecionada.getNome() + "\" com sucesso!");
            carregarDisciplinas();
        } catch (Exception e) {
            showErrorDisc(e.getMessage());
        }
    }

    @FXML
    public void handleCancelarMatricula() {
        Disciplina selecionada = tabelaMatriculadas.getSelectionModel().getSelectedItem();
        if (selecionada == null) { showErrorDisc("Selecione uma disciplina para cancelar a matrícula."); return; }
        try {
            alunoDisciplinaService.cancelarMatricula(alunoLogado.getId(), selecionada.getId());
            showSuccessDisc("Matrícula em \"" + selecionada.getNome() + "\" cancelada.");
            carregarDisciplinas();
        } catch (Exception e) {
            showErrorDisc(e.getMessage());
        }
    }

    // ===================== AGENDAS =====================

    private void configurarTabelaAgendas() {
        colDisciplina.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getDisciplina() != null
                        ? cell.getValue().getDisciplina().getNome() : "—"));
        colHorario.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getDataHoraInicio() + " → " + cell.getValue().getDataHoraFim()));
        colLocal.setCellValueFactory(cell -> {
            String local = cell.getValue().getLocal();
            String link  = cell.getValue().getLink();
            return new SimpleStringProperty(local != null && !local.isBlank() ? local : link);
        });
        colVagas.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getVagasDisponiveis() + "/" + cell.getValue().getVagasTotais()));
    }

    private void configurarTabelaMeusAgendamentos() {
        colMeuStatus.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getStatus().name()));
        colMeuAssunto.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getAssunto()));
        colMeuHorario.setCellValueFactory(cell -> {
            Agenda ag = cell.getValue().getAgenda();
            return new SimpleStringProperty(ag != null ? ag.getDataHoraInicio().toString() : "—");
        });
        if (colMeuJustificativa != null)
            colMeuJustificativa.setCellValueFactory(cell ->
                    new SimpleStringProperty(cell.getValue().getJustificativa() != null
                            ? cell.getValue().getJustificativa() : ""));
    }

    private void carregarAgendas() {
        tabelaAgendas.setItems(FXCollections.observableArrayList(
                agendaService.listarAgendasDisponiveisPorAluno(alunoLogado.getId())));
    }

    private void carregarMeusAgendamentos() {
        tabelaMeusAgendamentos.setItems(FXCollections.observableArrayList(
                agendamentoService.listarTodosPorAluno(alunoLogado.getId())));
    }

    @FXML
    public void handleAgendar() {
        errorMessage.setVisible(false);
        successMessage.setVisible(false);
        Agenda selecionada = tabelaAgendas.getSelectionModel().getSelectedItem();
        if (selecionada == null) { showError("Selecione um horário."); return; }
        String assunto = txtAssunto.getText().trim();
        if (assunto.isBlank()) { showError("Descreva o assunto da dúvida."); return; }
        try {
            agendamentoService.agendarMonitoria(alunoLogado, selecionada, assunto);
            showSuccess("Agendamento realizado com sucesso!");
            carregarAgendas();
            carregarMeusAgendamentos();
            txtAssunto.clear();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML
    public void handleCancelarAgendamento() {
        Agendamento selecionado = tabelaMeusAgendamentos.getSelectionModel().getSelectedItem();
        if (selecionado == null) { showError("Selecione um agendamento para cancelar."); return; }
        try {
            agendamentoService.cancelarPeloAluno(selecionado, alunoLogado);
            showSuccess("Agendamento cancelado.");
            carregarMeusAgendamentos();
            carregarAgendas();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    // ===================== LOGOUT =====================

    @FXML
    public void handleLogout() throws IOException {
        SessionManager.getInstance().encerrarSessao();
        MainApp.setRoot("login");
    }

    // ===================== HELPERS =====================

    private void showErrorDisc(String msg) {
        if (errorDisciplina != null) { errorDisciplina.setText(msg); errorDisciplina.setVisible(true); }
        if (successDisciplina != null) successDisciplina.setVisible(false);
    }
    private void showSuccessDisc(String msg) {
        if (successDisciplina != null) { successDisciplina.setText(msg); successDisciplina.setVisible(true); }
        if (errorDisciplina != null) errorDisciplina.setVisible(false);
    }
    private void showError(String msg) { if (errorMessage != null) { errorMessage.setText(msg); errorMessage.setVisible(true); } }
    private void showSuccess(String msg) { if (successMessage != null) { successMessage.setText(msg); successMessage.setVisible(true); } }
}