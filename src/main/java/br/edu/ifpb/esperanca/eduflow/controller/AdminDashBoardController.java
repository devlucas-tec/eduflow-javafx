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

public class AdminDashBoardController {

    @FXML private Label lblBemVindo;
    // Gestão de Disciplinas
    @FXML private TableView<Disciplina> tabelaDisciplinas;
    @FXML private TableColumn<Disciplina, String> colNome;
    @FXML private TableColumn<Disciplina, String> colCodigo;
    @FXML private TableColumn<Disciplina, String> colSemestre;
    @FXML private TextField txtNomeDisciplina;
    @FXML private TextField txtCodigoDisciplina;
    @FXML private TextField txtSemestreDisciplina;
    @FXML private Text errorMessage;
    @FXML private Text successMessage;

    private final DisciplinaService disciplinaService = new DisciplinaService();

    @FXML
    public void initialize() {
        Usuario usuario = SessionManager.getInstance().getUsuarioLogado();
        lblBemVindo.setText("Admin: " + (usuario != null ? usuario.getNome() : ""));

        configurarTabela();
        carregarDisciplinas();
    }

    private void configurarTabela() {
        if (colNome != null)
            colNome.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNome()));
        if (colCodigo != null)
            colCodigo.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCodigo()));
        if (colSemestre != null)
            colSemestre.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getSemestreLetivo()));
    }

    private void carregarDisciplinas() {
        if (tabelaDisciplinas == null) return;
        List<Disciplina> lista = disciplinaService.listarTodas();
        tabelaDisciplinas.setItems(FXCollections.observableArrayList(lista));
    }

    @FXML
    public void handleCadastrarDisciplina() {
        if (errorMessage != null) errorMessage.setVisible(false);
        String nome = txtNomeDisciplina != null ? txtNomeDisciplina.getText().trim() : "";
        String codigo = txtCodigoDisciplina != null ? txtCodigoDisciplina.getText().trim() : "";
        String semestre = txtSemestreDisciplina != null ? txtSemestreDisciplina.getText().trim() : "";

        try {
            Disciplina d = new Disciplina(null, nome, codigo, semestre);
            disciplinaService.cadastrar(d);
            showSuccess("Disciplina cadastrada!");
            carregarDisciplinas();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML
    public void handleExcluirDisciplina() {
        if (tabelaDisciplinas == null) return;
        Disciplina selecionada = tabelaDisciplinas.getSelectionModel().getSelectedItem();
        if (selecionada == null) { showError("Selecione uma disciplina."); return; }
        disciplinaService.excluir(selecionada.getId());
        showSuccess("Disciplina removida.");
        carregarDisciplinas();
    }

    @FXML
    public void handleLogout() throws IOException {
        SessionManager.getInstance().encerrarSessao();
        MainApp.setRoot("login");
    }

    private void showError(String msg) { if (errorMessage != null) { errorMessage.setText(msg); errorMessage.setVisible(true); } }
    private void showSuccess(String msg) { if (successMessage != null) { successMessage.setText(msg); successMessage.setVisible(true); } }
}
