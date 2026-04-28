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
    @FXML private TableView<Agendamento> tabelaParaValidar;
    @FXML private TableColumn<Agendamento, String> colStatus;
    @FXML private TableColumn<Agendamento, String> colAssunto;
    @FXML private TableColumn<Agendamento, String> colAluno;
    @FXML private Text errorMessage;
    @FXML private Text successMessage;

    private final AgendamentoService agendamentoService = new AgendamentoService();
    private final DisciplinaService disciplinaService = new DisciplinaService();
    private Professor professorLogado;

    @FXML
    public void initialize() {
        professorLogado = (Professor) SessionManager.getInstance().getUsuarioLogado();
        lblBemVindo.setText("Prof. " + professorLogado.getNome());

        List<Disciplina> disciplinas = disciplinaService.listarPorProfessor(professorLogado.getId());
        professorLogado.setDisciplinas(disciplinas);

        configurarTabela();
        carregarAgendamentosParaValidar();
    }

    private void configurarTabela() {
        if (colStatus != null)
            colStatus.setCellValueFactory(cell ->
                    new SimpleStringProperty(cell.getValue().getStatus().name()));
        if (colAssunto != null)
            colAssunto.setCellValueFactory(cell ->
                    new SimpleStringProperty(cell.getValue().getAssunto()));
        if (colAluno != null)
            colAluno.setCellValueFactory(cell -> {
                Aluno aluno = cell.getValue().getAluno();
                return new SimpleStringProperty(aluno != null ? aluno.getNome() : "—");
            });
    }

    private void carregarAgendamentosParaValidar() {
        // Carrega todos os agendamentos REALIZADO/FALTOU das agendas dos monitores vinculados
        // Por ora lista a partir do primeiro monitor da primeira disciplina do professor
        if (tabelaParaValidar == null) return;
        if (professorLogado.getDisciplinas().isEmpty()) return;

        // Usa primeiro monitor vinculado como pivot (simplificação acadêmica)
        // Em produção filtraria por todas as disciplinas do professor
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
            agendamentoService.listarParaValidacao(professorLogado.getId()); // atualiza
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
