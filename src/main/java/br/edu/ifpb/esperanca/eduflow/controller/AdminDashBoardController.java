package br.edu.ifpb.esperanca.eduflow.controller;

import br.edu.ifpb.esperanca.eduflow.MainApp;
import br.edu.ifpb.esperanca.eduflow.domain.entities.*;
import br.edu.ifpb.esperanca.eduflow.domain.enums.Role;
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

    // --- Aba Disciplinas ---
    @FXML private TableView<Disciplina> tabelaDisciplinas;
    @FXML private TableColumn<Disciplina, String> colNome;
    @FXML private TableColumn<Disciplina, String> colCodigo;
    @FXML private TableColumn<Disciplina, String> colSemestre;
    @FXML private TextField txtNomeDisciplina;
    @FXML private TextField txtCodigoDisciplina;
    @FXML private TextField txtSemestreDisciplina;
    @FXML private Text errorMessage;
    @FXML private Text successMessage;

    // --- Aba Usuários ---
    @FXML private TableView<Usuario> tabelaUsuarios;
    @FXML private TableColumn<Usuario, String> colUsuarioNome;
    @FXML private TableColumn<Usuario, String> colUsuarioEmail;
    @FXML private TableColumn<Usuario, String> colUsuarioMatricula;
    @FXML private TableColumn<Usuario, String> colUsuarioRole;
    @FXML private TableColumn<Usuario, String> colUsuarioStatus;
    @FXML private ComboBox<String> filtroRole;
    @FXML private ComboBox<String> filtroStatus;
    @FXML private ComboBox<String> novoRole;
    @FXML private Button btnToggleStatus;
    @FXML private Text errorUsuario;
    @FXML private Text successUsuario;

    private final DisciplinaService disciplinaService = new DisciplinaService();
    private final UsuarioService usuarioService = new UsuarioService();

    @FXML
    public void initialize() {
        Usuario usuario = SessionManager.getInstance().getUsuarioLogado();
        lblBemVindo.setText("Admin: " + (usuario != null ? usuario.getNome() : ""));

        configurarTabelaDisciplinas();
        carregarDisciplinas();

        configurarTabelaUsuarios();
        configurarFiltros();
        carregarUsuarios();

        // Atualiza o botão quando selecionar um usuário
        tabelaUsuarios.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                btnToggleStatus.setText(selected.isAtivo() ? "Desabilitar Conta" : "Habilitar Conta");
                btnToggleStatus.setStyle(selected.isAtivo()
                        ? "-fx-background-color: #E67E22; -fx-text-fill: white; -fx-background-radius: 5;"
                        : "-fx-background-color: #27AE60; -fx-text-fill: white; -fx-background-radius: 5;");
            }
        });
    }

    // ===================== DISCIPLINAS =====================

    private void configurarTabelaDisciplinas() {
        if (colNome != null)
            colNome.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNome()));
        if (colCodigo != null)
            colCodigo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCodigo()));
        if (colSemestre != null)
            colSemestre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSemestreLetivo()));
    }

    private void carregarDisciplinas() {
        if (tabelaDisciplinas == null) return;
        tabelaDisciplinas.setItems(FXCollections.observableArrayList(disciplinaService.listarTodas()));
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

    // ===================== USUÁRIOS =====================

    private void configurarTabelaUsuarios() {
        colUsuarioNome.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNome()));
        colUsuarioEmail.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail()));
        colUsuarioMatricula.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMatricula()));
        colUsuarioRole.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRole().name()));
        colUsuarioStatus.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().isAtivo() ? "✅ Ativo" : "🚫 Inativo"));
    }

    private void configurarFiltros() {
        filtroRole.setItems(FXCollections.observableArrayList(
                "TODOS", "ALUNO", "MONITOR", "PROFESSOR", "ADMINISTRADOR"));
        filtroRole.setValue("TODOS");

        filtroStatus.setItems(FXCollections.observableArrayList("TODOS", "ATIVO", "INATIVO"));
        filtroStatus.setValue("TODOS");

        novoRole.setItems(FXCollections.observableArrayList(
                "ALUNO", "MONITOR", "PROFESSOR", "ADMINISTRADOR"));
    }

    private void carregarUsuarios() {
        List<Usuario> lista = usuarioService.listarTodos();
        tabelaUsuarios.setItems(FXCollections.observableArrayList(lista));
    }

    @FXML
    public void handleFiltrarUsuarios() {
        String role = filtroRole.getValue();
        String status = filtroStatus.getValue();
        List<Usuario> lista = usuarioService.listarPorFiltro(role, status);
        tabelaUsuarios.setItems(FXCollections.observableArrayList(lista));
    }

    @FXML
    public void handleLimparFiltros() {
        filtroRole.setValue("TODOS");
        filtroStatus.setValue("TODOS");
        carregarUsuarios();
    }

    @FXML
    public void handleAlterarRole() {
        Usuario selecionado = tabelaUsuarios.getSelectionModel().getSelectedItem();
        if (selecionado == null) { showErrorUsuario("Selecione um usuário."); return; }
        String roleStr = novoRole.getValue();
        if (roleStr == null) { showErrorUsuario("Selecione o novo role."); return; }
        try {
            usuarioService.alterarRole(selecionado, Role.valueOf(roleStr));
            showSuccessUsuario("Role alterado para " + roleStr + ".");
            handleFiltrarUsuarios();
        } catch (Exception e) {
            showErrorUsuario(e.getMessage());
        }
    }

    @FXML
    public void handleToggleStatus() {
        Usuario selecionado = tabelaUsuarios.getSelectionModel().getSelectedItem();
        if (selecionado == null) { showErrorUsuario("Selecione um usuário."); return; }

        Usuario logado = SessionManager.getInstance().getUsuarioLogado();
        if (logado != null && logado.getId().equals(selecionado.getId())) {
            showErrorUsuario("Você não pode desabilitar sua própria conta.");
            return;
        }

        boolean novoStatus = !selecionado.isAtivo();
        usuarioService.alterarStatus(selecionado, novoStatus);
        showSuccessUsuario("Conta " + (novoStatus ? "habilitada" : "desabilitada") + " com sucesso.");
        handleFiltrarUsuarios();
    }

    // ===================== LOGOUT =====================

    @FXML
    public void handleLogout() throws IOException {
        SessionManager.getInstance().encerrarSessao();
        MainApp.setRoot("login");
    }

    // ===================== HELPERS =====================

    private void showError(String msg) {
        if (errorMessage != null) { errorMessage.setText(msg); errorMessage.setVisible(true); }
    }
    private void showSuccess(String msg) {
        if (successMessage != null) { successMessage.setText(msg); successMessage.setVisible(true); }
    }
    private void showErrorUsuario(String msg) {
        if (errorUsuario != null) { errorUsuario.setText(msg); errorUsuario.setVisible(true); }
        if (successUsuario != null) successUsuario.setVisible(false);
    }
    private void showSuccessUsuario(String msg) {
        if (successUsuario != null) { successUsuario.setText(msg); successUsuario.setVisible(true); }
        if (errorUsuario != null) errorUsuario.setVisible(false);
    }
}
