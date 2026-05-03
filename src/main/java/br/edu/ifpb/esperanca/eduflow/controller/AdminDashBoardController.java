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
import br.edu.ifpb.esperanca.eduflow.service.MonitorDisciplinaService;
import br.edu.ifpb.esperanca.eduflow.service.ProfessorDisciplinaService;
import br.edu.ifpb.esperanca.eduflow.domain.entities.Aula;
import br.edu.ifpb.esperanca.eduflow.service.AulaService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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

    // --- Aba Professores ---
    @FXML private TableView<Usuario> tabelaProfessores;
    @FXML private TableColumn<Usuario, String> colProfNome;
    @FXML private TableColumn<Usuario, String> colProfEmail;
    @FXML private TableColumn<Usuario, String> colProfMatricula;
    @FXML private TableColumn<Usuario, String> colProfStatus;
    @FXML private ComboBox<Disciplina> comboDisciplinaVinculo;
    @FXML private TableView<Disciplina> tabelaDisciplinasProfessor;
    @FXML private TableColumn<Disciplina, String> colDPNome;
    @FXML private TableColumn<Disciplina, String> colDPCodigo;
    @FXML private TableColumn<Disciplina, String> colDPSemestre;
    @FXML private Text errorProfessor;
    @FXML private Text successProfessor;

    // --- Aba Monitores ---
    @FXML private TableView<Usuario> tabelaMonitores;
    @FXML private TableColumn<Usuario, String> colMonitorNome;
    @FXML private TableColumn<Usuario, String> colMonitorEmail;
    @FXML private TableColumn<Usuario, String> colMonitorMatricula;
    @FXML private TableColumn<Usuario, String> colMonitorStatus;
    @FXML private ComboBox<Disciplina> comboDisciplinaMonitor;
    @FXML private TableView<Disciplina> tabelaDisciplinasMonitor;
    @FXML private TableColumn<Disciplina, String> colDMNome;
    @FXML private TableColumn<Disciplina, String> colDMCodigo;
    @FXML private TableColumn<Disciplina, String> colDMSemestre;
    @FXML private Text errorMonitor;
    @FXML private Text successMonitor;

    // --- Aba Calendários ---
    @FXML private ComboBox<Usuario> comboProfessor;
    @FXML private TableView<Aula> tabelaAulas;
    @FXML private TableColumn<Aula, String> colAulaProfessor;
    @FXML private TableColumn<Aula, String> colAulaData;
    @FXML private TableColumn<Aula, String> colAulaDisciplina;
    @FXML private TableColumn<Aula, String> colAulaTipo;
    @FXML private TableColumn<Aula, String> colAulaJustificativa;
    @FXML private TextField txtAulaData;
    @FXML private ComboBox<String> comboTipoAula;
    @FXML private ComboBox<Disciplina> comboDisciplinaAula;
    @FXML private TextField txtJustificativa;
    @FXML private Text errorCalendario;
    @FXML private Text successCalendario;

    @FXML private TabPane tabPane;

    private final MonitorDisciplinaService monitorDisciplinaService = new MonitorDisciplinaService();
    private final ProfessorDisciplinaService professorDisciplinaService = new ProfessorDisciplinaService();
    private final AulaService aulaService = new AulaService();
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final DisciplinaService disciplinaService = new DisciplinaService();
    private final UsuarioService usuarioService = new UsuarioService();

    @FXML
    public void initialize() {
        Usuario usuario = SessionManager.getInstance().getUsuarioLogado();
        lblBemVindo.setText("Admin: " + (usuario != null ? usuario.getNome() : ""));

        // Colunas se ajustam dinamicamente à largura da tabela
        tabelaDisciplinas.widthProperty().addListener((obs, oldW, newW) -> {
            double w = newW.doubleValue();
            colNome.setPrefWidth(w * 0.50);
            colCodigo.setPrefWidth(w * 0.25);
            colSemestre.setPrefWidth(w * 0.25);
        });

        tabelaUsuarios.widthProperty().addListener((obs, oldW, newW) -> {
            double w = newW.doubleValue();
            colUsuarioNome.setPrefWidth(w * 0.30);
            colUsuarioEmail.setPrefWidth(w * 0.30);
            colUsuarioMatricula.setPrefWidth(w * 0.15);
            colUsuarioRole.setPrefWidth(w * 0.15);
            colUsuarioStatus.setPrefWidth(w * 0.10);
        });

        tabelaProfessores.widthProperty().addListener((obs, oldW, newW) -> {
            double w = newW.doubleValue();
            colProfNome.setPrefWidth(w * 0.35);
            colProfEmail.setPrefWidth(w * 0.35);
            colProfMatricula.setPrefWidth(w * 0.20);
            colProfStatus.setPrefWidth(w * 0.10);
        });

        tabelaDisciplinasProfessor.widthProperty().addListener((obs, oldW, newW) -> {
            double w = newW.doubleValue();
            colDPNome.setPrefWidth(w * 0.50);
            colDPCodigo.setPrefWidth(w * 0.25);
            colDPSemestre.setPrefWidth(w * 0.25);
        });

        tabelaMonitores.widthProperty().addListener((obs, oldW, newW) -> {
            double w = newW.doubleValue();
            colMonitorNome.setPrefWidth(w * 0.35);
            colMonitorEmail.setPrefWidth(w * 0.35);
            colMonitorMatricula.setPrefWidth(w * 0.20);
            colMonitorStatus.setPrefWidth(w * 0.10);
        });

        tabelaDisciplinasMonitor.widthProperty().addListener((obs, oldW, newW) -> {
            double w = newW.doubleValue();
            colDMNome.setPrefWidth(w * 0.50);
            colDMCodigo.setPrefWidth(w * 0.25);
            colDMSemestre.setPrefWidth(w * 0.25);
        });

        tabelaAulas.widthProperty().addListener((obs, oldW, newW) -> {
            double w = newW.doubleValue();
            colAulaProfessor.setPrefWidth(w * 0.20);
            colAulaData.setPrefWidth(w * 0.18);
            colAulaDisciplina.setPrefWidth(w * 0.25);
            colAulaTipo.setPrefWidth(w * 0.10);
            colAulaJustificativa.setPrefWidth(w * 0.27);
        });

        configurarTabelaDisciplinas();
        carregarDisciplinas();

        configurarTabelaUsuarios();
        configurarFiltros();
        carregarUsuarios();

        configurarTabelaProfessores();
        configurarTabelaMonitores();

        configurarTabelaAulas();
        configurarFormularioAula();
        handleVerTodasAulas();

        tabelaUsuarios.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                btnToggleStatus.setText(selected.isAtivo() ? "Desabilitar Conta" : "Habilitar Conta");
                btnToggleStatus.setStyle(selected.isAtivo()
                        ? "-fx-background-color: #E67E22; -fx-text-fill: white; -fx-background-radius: 5;"
                        : "-fx-background-color: #27AE60; -fx-text-fill: white; -fx-background-radius: 5;");
            }
        });

        tabPane.getSelectionModel().selectedIndexProperty().addListener((obs, oldIndex, newIndex) -> {
            int idx = newIndex.intValue();
            List<Disciplina> disciplinas = disciplinaService.listarTodas();
            if (idx == 2) {
                comboDisciplinaVinculo.setItems(FXCollections.observableArrayList(disciplinas));
                carregarProfessores();
            } else if (idx == 3) {
                comboDisciplinaMonitor.setItems(FXCollections.observableArrayList(disciplinas));
                carregarMonitores();
            } else if (idx == 4) {
                comboDisciplinaAula.setItems(FXCollections.observableArrayList(disciplinas));
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
        try {
            disciplinaService.excluir(selecionada.getId());
            showSuccess("Disciplina removida.");
            carregarDisciplinas();
        } catch (Exception e) {
            showError("Erro ao excluir disciplina: " + e.getMessage());
        }
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

    // ===================== PROFESSORES =====================

    private void configurarTabelaProfessores() {
        colProfNome.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNome()));
        colProfEmail.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail()));
        colProfMatricula.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMatricula()));
        colProfStatus.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().isAtivo() ? "✅ Ativo" : "🚫 Inativo"));

        colDPNome.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNome()));
        colDPCodigo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCodigo()));
        colDPSemestre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSemestreLetivo()));

        comboDisciplinaVinculo.setConverter(new javafx.util.StringConverter<>() {
            public String toString(Disciplina d) { return d != null ? d.getNome() + " (" + d.getCodigo() + ")" : ""; }
            public Disciplina fromString(String s) { return null; }
        });

        tabelaProfessores.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected == null) {
                tabelaDisciplinasProfessor.getItems().clear();
                return;
            }
            atualizarTabelaDisciplinasProfessor(selected.getId());
        });
    }

    private void carregarProfessores() {
        List<Usuario> professores = usuarioService.listarProfessores();
        tabelaProfessores.setItems(FXCollections.observableArrayList(professores));
        tabelaDisciplinasProfessor.getItems().clear();
    }

    private void atualizarTabelaDisciplinasProfessor(Long professorId) {
        List<Disciplina> disciplinas = professorDisciplinaService.listarPorProfessor(professorId);
        tabelaDisciplinasProfessor.setItems(FXCollections.observableArrayList(disciplinas));
    }

    @FXML
    public void handleVincularDisciplina() {
        Usuario selecionado = tabelaProfessores.getSelectionModel().getSelectedItem();
        if (selecionado == null) { showErrorProfessor("Selecione um professor na tabela."); return; }
        Disciplina disciplina = comboDisciplinaVinculo.getValue();
        if (disciplina == null) { showErrorProfessor("Selecione uma disciplina."); return; }
        try {
            professorDisciplinaService.vincular(selecionado.getId(), disciplina.getId());
            showSuccessProfessor("Disciplina vinculada ao professor com sucesso.");
            atualizarTabelaDisciplinasProfessor(selecionado.getId());
        } catch (Exception e) {
            showErrorProfessor(e.getMessage());
        }
    }

    @FXML
    public void handleDesvincularDisciplina() {
        Usuario selecionado = tabelaProfessores.getSelectionModel().getSelectedItem();
        if (selecionado == null) { showErrorProfessor("Selecione um professor na tabela."); return; }
        Disciplina disciplina = comboDisciplinaVinculo.getValue();
        if (disciplina == null) { showErrorProfessor("Selecione uma disciplina para desvincular."); return; }
        try {
            professorDisciplinaService.desvincular(selecionado.getId(), disciplina.getId());
            showSuccessProfessor("Disciplina desvinculada do professor.");
            atualizarTabelaDisciplinasProfessor(selecionado.getId());
        } catch (Exception e) {
            showErrorProfessor(e.getMessage());
        }
    }

    // ===================== MONITORES =====================

    private void configurarTabelaMonitores() {
        colMonitorNome.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNome()));
        colMonitorEmail.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail()));
        colMonitorMatricula.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMatricula()));
        colMonitorStatus.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().isAtivo() ? "✅ Ativo" : "🚫 Inativo"));

        colDMNome.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNome()));
        colDMCodigo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCodigo()));
        colDMSemestre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSemestreLetivo()));

        comboDisciplinaMonitor.setConverter(new javafx.util.StringConverter<>() {
            public String toString(Disciplina d) { return d != null ? d.getNome() + " (" + d.getCodigo() + ")" : ""; }
            public Disciplina fromString(String s) { return null; }
        });

        tabelaMonitores.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected == null) {
                tabelaDisciplinasMonitor.getItems().clear();
                return;
            }
            atualizarTabelaDisciplinasMonitor(selected.getId());
        });
    }

    private void carregarMonitores() {
        List<Usuario> monitores = usuarioService.listarMonitores();
        tabelaMonitores.setItems(FXCollections.observableArrayList(monitores));
        tabelaDisciplinasMonitor.getItems().clear();
    }

    private void atualizarTabelaDisciplinasMonitor(Long monitorId) {
        monitorDisciplinaService.buscarDisciplinaDoMonitor(monitorId).ifPresentOrElse(
                d -> tabelaDisciplinasMonitor.setItems(FXCollections.observableArrayList(d)),
                () -> tabelaDisciplinasMonitor.getItems().clear()
        );
    }

    @FXML
    public void handleVincularMonitor() {
        Usuario selecionado = tabelaMonitores.getSelectionModel().getSelectedItem();
        if (selecionado == null) { showErrorMonitor("Selecione um monitor na tabela."); return; }
        Disciplina disciplina = comboDisciplinaMonitor.getValue();
        if (disciplina == null) { showErrorMonitor("Selecione uma disciplina."); return; }
        try {
            monitorDisciplinaService.vincular(selecionado.getId(), disciplina.getId());
            showSuccessMonitor("Monitor vinculado a \"" + disciplina.getNome() + "\".");
            atualizarTabelaDisciplinasMonitor(selecionado.getId());
        } catch (Exception e) { showErrorMonitor(e.getMessage()); }
    }

    @FXML
    public void handleDesvincularMonitor() {
        Usuario selecionado = tabelaMonitores.getSelectionModel().getSelectedItem();
        if (selecionado == null) { showErrorMonitor("Selecione um monitor na tabela."); return; }
        try {
            monitorDisciplinaService.desvincular(selecionado.getId());
            showSuccessMonitor("Monitor desvinculado da disciplina.");
            atualizarTabelaDisciplinasMonitor(selecionado.getId());
        } catch (Exception e) { showErrorMonitor(e.getMessage()); }
    }

    // ===================== CALENDÁRIOS =====================

    private void configurarTabelaAulas() {
        colAulaProfessor.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getProfessorNome() != null
                        ? c.getValue().getProfessorNome() : "—"));
        colAulaData.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getDataHora() != null
                        ? c.getValue().getDataHora().format(FMT) : "—"));
        colAulaDisciplina.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getDisciplinaNome() != null
                        ? c.getValue().getDisciplinaNome() : "—"));
        colAulaTipo.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getTipo()));
        colAulaJustificativa.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getJustificativa() != null
                        ? c.getValue().getJustificativa() : ""));

        tabelaAulas.getSelectionModel().selectedItemProperty().addListener((obs, old, aula) -> {
            if (aula != null) {
                txtAulaData.setText(aula.getDataHora().format(FMT));
                comboTipoAula.setValue(aula.getTipo());
                txtJustificativa.setText(aula.getJustificativa() != null ? aula.getJustificativa() : "");
                comboDisciplinaAula.getItems().stream()
                        .filter(d -> d.getId().equals(aula.getDisciplinaId()))
                        .findFirst()
                        .ifPresent(comboDisciplinaAula::setValue);
            }
        });
    }

    private void configurarFormularioAula() {
        comboTipoAula.setItems(FXCollections.observableArrayList("REGULAR", "REPOSICAO", "EXTRA"));
        comboTipoAula.setValue("REGULAR");

        List<Usuario> professores = usuarioService.listarProfessores();
        comboProfessor.setItems(FXCollections.observableArrayList(professores));
        comboProfessor.setConverter(new javafx.util.StringConverter<>() {
            public String toString(Usuario u) { return u != null ? u.getNome() : ""; }
            public Usuario fromString(String s) { return null; }
        });

        List<Disciplina> disciplinas = disciplinaService.listarTodas();
        comboDisciplinaAula.setItems(FXCollections.observableArrayList(disciplinas));
        comboDisciplinaAula.setConverter(new javafx.util.StringConverter<>() {
            public String toString(Disciplina d) { return d != null ? d.getNome() : ""; }
            public Disciplina fromString(String s) { return null; }
        });
    }

    @FXML
    public void handleCarregarCalendario() {
        Usuario professor = comboProfessor.getValue();
        if (professor == null) { showErrorCalendario("Selecione um professor."); return; }
        List<Aula> aulas = aulaService.listarPorProfessor(professor.getId());
        aulas.forEach(a -> a.setProfessorNome(professor.getNome()));
        tabelaAulas.setItems(FXCollections.observableArrayList(aulas));
        showSuccessCalendario(aulas.size() + " aula(s) encontrada(s).");
    }

    @FXML
    public void handleVerTodasAulas() {
        List<Aula> aulas = aulaService.listarTodas();
        tabelaAulas.setItems(FXCollections.observableArrayList(aulas));
    }

    @FXML
    public void handleAdicionarAula() {
        try {
            Aula aula = coletarFormulario(null);
            aulaService.cadastrar(aula);
            showSuccessCalendario("Aula adicionada com sucesso.");
            handleVerTodasAulas();
            limparFormularioAula();
        } catch (Exception e) {
            showErrorCalendario(e.getMessage());
        }
    }

    @FXML
    public void handleEditarAula() {
        Aula selecionada = tabelaAulas.getSelectionModel().getSelectedItem();
        if (selecionada == null) { showErrorCalendario("Selecione uma aula para editar."); return; }
        try {
            Aula aula = coletarFormulario(selecionada.getId());
            aulaService.editar(aula);
            showSuccessCalendario("Aula atualizada com sucesso.");
            handleVerTodasAulas();
            limparFormularioAula();
        } catch (Exception e) {
            showErrorCalendario(e.getMessage());
        }
    }

    @FXML
    public void handleRemoverAula() {
        Aula selecionada = tabelaAulas.getSelectionModel().getSelectedItem();
        if (selecionada == null) { showErrorCalendario("Selecione uma aula para remover."); return; }
        aulaService.excluir(selecionada.getId());
        showSuccessCalendario("Aula removida.");
        handleVerTodasAulas();
        limparFormularioAula();
    }

    @FXML
    public void handleLimparSelecaoAula() {
        tabelaAulas.getSelectionModel().clearSelection();
        limparFormularioAula();
    }

    private Aula coletarFormulario(Long id) {
        String dataStr = txtAulaData.getText().trim();
        String tipo = comboTipoAula.getValue();
        Disciplina disciplina = comboDisciplinaAula.getValue();
        Usuario professor = comboProfessor.getValue();
        String justificativa = txtJustificativa.getText().trim();

        if (dataStr.isEmpty()) throw new RuntimeException("Informe a data e hora da aula.");
        if (professor == null) throw new RuntimeException("Selecione o professor.");
        if (disciplina == null) throw new RuntimeException("Selecione a disciplina.");

        LocalDateTime dataHora;
        try {
            dataHora = LocalDateTime.parse(dataStr, FMT);
        } catch (DateTimeParseException e) {
            throw new RuntimeException("Data inválida. Use o formato dd/MM/yyyy HH:mm.");
        }

        return new Aula(id, dataHora, tipo, justificativa.isEmpty() ? null : justificativa,
                professor.getId(), disciplina.getId());
    }

    private void limparFormularioAula() {
        txtAulaData.clear();
        txtJustificativa.clear();
        comboTipoAula.setValue("REGULAR");
        comboDisciplinaAula.setValue(null);
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
        if (successMessage != null) successMessage.setVisible(false);
    }
    private void showSuccess(String msg) {
        if (successMessage != null) { successMessage.setText(msg); successMessage.setVisible(true); }
        if (errorMessage != null) errorMessage.setVisible(false);
    }
    private void showErrorUsuario(String msg) {
        if (errorUsuario != null) { errorUsuario.setText(msg); errorUsuario.setVisible(true); }
        if (successUsuario != null) successUsuario.setVisible(false);
    }
    private void showSuccessUsuario(String msg) {
        if (successUsuario != null) { successUsuario.setText(msg); successUsuario.setVisible(true); }
        if (errorUsuario != null) errorUsuario.setVisible(false);
    }
    private void showErrorProfessor(String msg) {
        if (errorProfessor != null) { errorProfessor.setText(msg); errorProfessor.setVisible(true); }
        if (successProfessor != null) successProfessor.setVisible(false);
    }
    private void showSuccessProfessor(String msg) {
        if (successProfessor != null) { successProfessor.setText(msg); successProfessor.setVisible(true); }
        if (errorProfessor != null) errorProfessor.setVisible(false);
    }
    private void showErrorMonitor(String msg) {
        if (errorMonitor != null) { errorMonitor.setText(msg); errorMonitor.setVisible(true); }
        if (successMonitor != null) successMonitor.setVisible(false);
    }
    private void showSuccessMonitor(String msg) {
        if (successMonitor != null) { successMonitor.setText(msg); successMonitor.setVisible(true); }
        if (errorMonitor != null) errorMonitor.setVisible(false);
    }
    private void showErrorCalendario(String msg) {
        if (errorCalendario != null) { errorCalendario.setText(msg); errorCalendario.setVisible(true); }
        if (successCalendario != null) successCalendario.setVisible(false);
    }
    private void showSuccessCalendario(String msg) {
        if (successCalendario != null) { successCalendario.setText(msg); successCalendario.setVisible(true); }
        if (errorCalendario != null) errorCalendario.setVisible(false);
    }
}