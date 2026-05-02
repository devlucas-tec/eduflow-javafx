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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ProfessorDashboardController {

    @FXML private Label lblBemVindo;

    // --- Aba Disciplinas ---
    @FXML private TableView<Disciplina> tabelaDisciplinas;
    @FXML private TableColumn<Disciplina, String> colDisciplinaNome;
    @FXML private TableColumn<Disciplina, String> colDisciplinaCodigo;
    @FXML private TableColumn<Disciplina, String> colDisciplinaSemestre;
    @FXML private Text msgDisciplinas;

    // --- Aba Calendário ---
    @FXML private ComboBox<Disciplina> comboDisciplinaCalendario;
    @FXML private Label lblCargaHoraria;
    @FXML private TableView<Aula> tabelaMinhasAulas;
    @FXML private TableColumn<Aula, String> colCalData;
    @FXML private TableColumn<Aula, String> colCalDisciplina;
    @FXML private TableColumn<Aula, String> colCalTipo;
    @FXML private TableColumn<Aula, String> colCalJustificativa;
    @FXML private TextField txtCalData;
    @FXML private ComboBox<String> comboCalTipo;
    @FXML private ComboBox<Disciplina> comboCalDisciplina;
    @FXML private TextField txtCalJustificativa;
    @FXML private Text errorCalendario;
    @FXML private Text successCalendario;

    // --- Aba Validação ---
    @FXML private TableView<Agendamento> tabelaParaValidar;
    @FXML private TableColumn<Agendamento, String> colStatus;
    @FXML private TableColumn<Agendamento, String> colAssunto;
    @FXML private TableColumn<Agendamento, String> colAluno;
    @FXML private TableColumn<Agendamento, String> colJustificativa;
    @FXML private Text errorMessage;
    @FXML private Text successMessage;

    // --- Aba Monitores ---
    @FXML private ComboBox<Disciplina> comboDisciplinaMonitor;
    @FXML private TableView<Agenda> tabelaAgendasMonitor;
    @FXML private TableColumn<Agenda, String> colMonNome;
    @FXML private TableColumn<Agenda, String> colMonHorario;
    @FXML private TableColumn<Agenda, String> colMonLocal;
    @FXML private TableColumn<Agenda, String> colMonStatus;
    @FXML private TableColumn<Agenda, String> colMonJustificativa;
    @FXML private TableView<Agendamento> tabelaAgendamentosMonitor;
    @FXML private TableColumn<Agendamento, String> colAgmAluno;
    @FXML private TableColumn<Agendamento, String> colAgmAssunto;
    @FXML private TableColumn<Agendamento, String> colAgmStatus;
    @FXML private TableColumn<Agendamento, String> colAgmJustificativa;
    @FXML private Text errorMonitor;

    private final AgendaService agendaService = new AgendaService();
    private final AgendamentoService agendamentoService = new AgendamentoService();
    private final ProfessorDisciplinaService professorDisciplinaService = new ProfessorDisciplinaService();
    private final AulaService aulaService = new AulaService();
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private Professor professorLogado;

    @FXML
    public void initialize() {
        professorLogado = (Professor) SessionManager.getInstance().getUsuarioLogado();
        lblBemVindo.setText("Prof. " + professorLogado.getNome());

        configurarTabelaDisciplinas();
        carregarDisciplinas();

        configurarTabelaCalendario();
        configurarFormularioCalendario();
        handleVerTodasMinhasAulas();

        configurarTabelaValidacao();
        carregarAgendamentosParaValidar();

        configurarTabelaMonitores();
        carregarDisciplinasComboMonitor();
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
        if (disciplinas.isEmpty() && msgDisciplinas != null) {
            msgDisciplinas.setText("Nenhuma disciplina vinculada. Solicite ao administrador.");
            msgDisciplinas.setVisible(true);
        }
        tabelaDisciplinas.setItems(FXCollections.observableArrayList(disciplinas));
    }

    // ===================== CALENDÁRIO =====================

    private void configurarTabelaCalendario() {
        colCalData.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getDataHora() != null
                        ? c.getValue().getDataHora().format(FMT) : "—"));
        colCalDisciplina.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getDisciplinaNome() != null
                        ? c.getValue().getDisciplinaNome() : "—"));
        colCalTipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTipo()));
        colCalJustificativa.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getJustificativa() != null
                        ? c.getValue().getJustificativa() : ""));

        // Ao clicar numa aula, preenche o formulário para edição
        tabelaMinhasAulas.getSelectionModel().selectedItemProperty().addListener((obs, old, aula) -> {
            if (aula != null) {
                txtCalData.setText(aula.getDataHora().format(FMT));
                comboCalTipo.setValue(aula.getTipo());
                txtCalJustificativa.setText(aula.getJustificativa() != null ? aula.getJustificativa() : "");
                comboCalDisciplina.getItems().stream()
                        .filter(d -> d.getId().equals(aula.getDisciplinaId()))
                        .findFirst().ifPresent(comboCalDisciplina::setValue);
            }
        });
    }

    private void configurarFormularioCalendario() {
        comboCalTipo.setItems(FXCollections.observableArrayList("REGULAR", "REPOSICAO", "EXTRA"));
        comboCalTipo.setValue("REGULAR");

        List<Disciplina> disciplinas = professorDisciplinaService.listarPorProfessor(professorLogado.getId());

        // Combo do filtro
        comboDisciplinaCalendario.setItems(FXCollections.observableArrayList(disciplinas));
        comboDisciplinaCalendario.setConverter(new javafx.util.StringConverter<>() {
            public String toString(Disciplina d) { return d != null ? d.getNome() : ""; }
            public Disciplina fromString(String s) { return null; }
        });

        // Combo do formulário
        comboCalDisciplina.setItems(FXCollections.observableArrayList(disciplinas));
        comboCalDisciplina.setConverter(new javafx.util.StringConverter<>() {
            public String toString(Disciplina d) { return d != null ? d.getNome() : ""; }
            public Disciplina fromString(String s) { return null; }
        });
    }

    @FXML
    public void handleFiltrarAulas() {
        Disciplina disciplina = comboDisciplinaCalendario.getValue();
        if (disciplina == null) { showErrorCal("Selecione uma disciplina para filtrar."); return; }

        List<Aula> aulas = aulaService.listarPorProfessorEDisciplina(professorLogado.getId(), disciplina.getId());
        tabelaMinhasAulas.setItems(FXCollections.observableArrayList(aulas));

        int[] carga = aulaService.calcularCargaHoraria(professorLogado.getId(), disciplina.getId());
        lblCargaHoraria.setText("Carga horária — Total: " + carga[0] + " aula(s) | Cumpridas: " + carga[1]);
    }

    @FXML
    public void handleVerTodasMinhasAulas() {
        List<Aula> aulas = aulaService.listarPorProfessor(professorLogado.getId());
        tabelaMinhasAulas.setItems(FXCollections.observableArrayList(aulas));
        if (lblCargaHoraria != null) lblCargaHoraria.setText("Exibindo todas as disciplinas");
    }

    @FXML
    public void handleAdicionarMinhaAula() {
        try {
            Aula aula = coletarFormulario(null);
            aulaService.cadastrar(aula);
            showSuccessCal("Aula adicionada com sucesso.");
            handleVerTodasMinhasAulas();
            limparFormulario();
        } catch (Exception e) {
            showErrorCal(e.getMessage());
        }
    }

    @FXML
    public void handleEditarMinhaAula() {
        Aula selecionada = tabelaMinhasAulas.getSelectionModel().getSelectedItem();
        if (selecionada == null) { showErrorCal("Selecione uma aula para editar."); return; }
        try {
            Aula aula = coletarFormulario(selecionada.getId());
            aulaService.editar(aula);
            showSuccessCal("Aula atualizada com sucesso.");
            handleVerTodasMinhasAulas();
            limparFormulario();
        } catch (Exception e) {
            showErrorCal(e.getMessage());
        }
    }

    @FXML
    public void handleRemoverMinhaAula() {
        Aula selecionada = tabelaMinhasAulas.getSelectionModel().getSelectedItem();
        if (selecionada == null) { showErrorCal("Selecione uma aula para remover."); return; }
        aulaService.excluir(selecionada.getId());
        showSuccessCal("Aula removida.");
        handleVerTodasMinhasAulas();
        limparFormulario();
    }

    @FXML
    public void handleLimparCalendario() {
        tabelaMinhasAulas.getSelectionModel().clearSelection();
        limparFormulario();
    }

    private Aula coletarFormulario(Long id) {
        String dataStr = txtCalData.getText().trim();
        String tipo = comboCalTipo.getValue();
        Disciplina disciplina = comboCalDisciplina.getValue();
        String justificativa = txtCalJustificativa.getText().trim();

        if (dataStr.isEmpty()) throw new RuntimeException("Informe a data e hora.");
        if (disciplina == null) throw new RuntimeException("Selecione a disciplina.");

        LocalDateTime dataHora;
        try {
            dataHora = LocalDateTime.parse(dataStr, FMT);
        } catch (DateTimeParseException e) {
            throw new RuntimeException("Data inválida. Use o formato dd/MM/yyyy HH:mm.");
        }

        return new Aula(id, dataHora, tipo,
                justificativa.isEmpty() ? null : justificativa,
                professorLogado.getId(), disciplina.getId());
    }

    private void limparFormulario() {
        txtCalData.clear();
        txtCalJustificativa.clear();
        comboCalTipo.setValue("REGULAR");
        comboCalDisciplina.setValue(null);
    }

    // ===================== MONITORES =====================

    private void configurarTabelaMonitores() {
        colMonNome.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getMonitorNome() != null ? c.getValue().getMonitorNome() : "—"));
        colMonHorario.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getDataHoraInicio() != null
                        ? c.getValue().getDataHoraInicio().format(FMT) : "—"));
        colMonLocal.setCellValueFactory(c -> {
            String local = c.getValue().getLocal();
            String link = c.getValue().getLink();
            return new SimpleStringProperty(local != null && !local.isBlank() ? local : (link != null ? link : "—"));
        });
        colMonStatus.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().isCancelada() ? "❌ Cancelada" : "✅ Ativa"));
        colMonJustificativa.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getJustificativa() != null
                        ? c.getValue().getJustificativa() : ""));

        // Ao selecionar uma sessão, carrega os agendamentos dela
        tabelaAgendasMonitor.getSelectionModel().selectedItemProperty().addListener((obs, old, agenda) -> {
            if (agenda != null) carregarAgendamentosDaSessao(agenda.getId());
            else tabelaAgendamentosMonitor.getItems().clear();
        });

        colAgmAluno.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getAluno() != null ? c.getValue().getAluno().getNome() : "—"));
        colAgmAssunto.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAssunto()));
        colAgmStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus().name()));
        colAgmJustificativa.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getJustificativa() != null
                        ? c.getValue().getJustificativa() : ""));
    }

    private void carregarDisciplinasComboMonitor() {
        List<Disciplina> disciplinas = professorDisciplinaService.listarPorProfessor(professorLogado.getId());
        comboDisciplinaMonitor.setItems(FXCollections.observableArrayList(disciplinas));
        comboDisciplinaMonitor.setConverter(new javafx.util.StringConverter<>() {
            public String toString(Disciplina d) { return d != null ? d.getNome() : ""; }
            public Disciplina fromString(String s) { return null; }
        });
    }

    @FXML
    public void handleCarregarAgendaMonitor() {
        Disciplina disciplina = comboDisciplinaMonitor.getValue();
        if (disciplina == null) {
            if (errorMonitor != null) { errorMonitor.setText("Selecione uma disciplina."); errorMonitor.setVisible(true); }
            return;
        }
        if (errorMonitor != null) errorMonitor.setVisible(false);
        List<Agenda> agendas = agendaService.listarPorDisciplina(disciplina.getId());
        tabelaAgendasMonitor.setItems(FXCollections.observableArrayList(agendas));
        tabelaAgendamentosMonitor.getItems().clear();
    }

    private void carregarAgendamentosDaSessao(Long agendaId) {
        List<Agendamento> agendamentos = agendamentoService.listarPorAgenda(agendaId);
        tabelaAgendamentosMonitor.setItems(FXCollections.observableArrayList(agendamentos));
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
        if (colJustificativa != null)
            colJustificativa.setCellValueFactory(c ->
                    new SimpleStringProperty(c.getValue().getJustificativa() != null
                            ? c.getValue().getJustificativa() : ""));
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

    // ===================== LOGOUT =====================

    @FXML
    public void handleLogout() throws IOException {
        SessionManager.getInstance().encerrarSessao();
        MainApp.setRoot("login");
    }

    // ===================== HELPERS =====================

    private void showErrorCal(String msg) {
        if (errorCalendario != null) { errorCalendario.setText(msg); errorCalendario.setVisible(true); }
        if (successCalendario != null) successCalendario.setVisible(false);
    }
    private void showSuccessCal(String msg) {
        if (successCalendario != null) { successCalendario.setText(msg); successCalendario.setVisible(true); }
        if (errorCalendario != null) errorCalendario.setVisible(false);
    }
    private void showError(String msg) { if (errorMessage != null) { errorMessage.setText(msg); errorMessage.setVisible(true); } }
    private void showSuccess(String msg) { if (successMessage != null) { successMessage.setText(msg); successMessage.setVisible(true); } }
}
