package br.edu.ifpb.esperanca.eduflow.controller;

import br.edu.ifpb.esperanca.eduflow.MainApp;
import br.edu.ifpb.esperanca.eduflow.domain.entities.*;
import br.edu.ifpb.esperanca.eduflow.service.AgendaService;
import br.edu.ifpb.esperanca.eduflow.service.AgendamentoService;
import br.edu.ifpb.esperanca.eduflow.service.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.List;

public class AlunoDashBoardController {

    @FXML private Label lblBemVindo;
    @FXML private TableView<Agenda> tabelaAgendas;
    @FXML private TableColumn<Agenda, String> colDisciplina;
    @FXML private TableColumn<Agenda, String> colHorario;
    @FXML private TableColumn<Agenda, String> colLocal;
    @FXML private TableColumn<Agenda, String> colVagas;
    @FXML private TextArea txtAssunto;
    @FXML private Text errorMessage;
    @FXML private Text successMessage;
    @FXML private TableView<Agendamento> tabelaMeusAgendamentos;
    @FXML private TableColumn<Agendamento, String> colMeuStatus;
    @FXML private TableColumn<Agendamento, String> colMeuAssunto;
    @FXML private TableColumn<Agendamento, String> colMeuHorario;

    private final AgendaService agendaService = new AgendaService();
    private final AgendamentoService agendamentoService = new AgendamentoService();
    private Aluno alunoLogado;

    @FXML
    public void initialize() {
        Usuario usuario = SessionManager.getInstance().getUsuarioLogado();
        this.alunoLogado = (Aluno) usuario;
        lblBemVindo.setText("Olá, " + alunoLogado.getNome() + "!");

        configurarTabelaAgendas();
        configurarTabelaMeusAgendamentos();
        carregarAgendas();
        carregarMeusAgendamentos();
    }

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
    }

    private void carregarAgendas() {
        List<Agenda> agendas = agendaService.listarAgendasDisponiveis();
        tabelaAgendas.setItems(FXCollections.observableArrayList(agendas));
    }

    private void carregarMeusAgendamentos() {
        List<Agendamento> meus = agendamentoService.listarTodosPorAluno(alunoLogado.getId());
        tabelaMeusAgendamentos.setItems(FXCollections.observableArrayList(meus));
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
            agendamentoService.cancelarPeloAluno(selecionado, alunoLogado, "Cancelado pelo aluno.");
            showSuccess("Agendamento cancelado.");
            carregarMeusAgendamentos();
            carregarAgendas();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML
    public void handleLogout() throws IOException {
        SessionManager.getInstance().encerrarSessao();
        MainApp.setRoot("login");
    }

    private void showError(String msg) { errorMessage.setText(msg); errorMessage.setVisible(true); }
    private void showSuccess(String msg) { successMessage.setText(msg); successMessage.setVisible(true); }
}
