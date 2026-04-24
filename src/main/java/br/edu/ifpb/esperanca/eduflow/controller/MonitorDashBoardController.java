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
import java.util.List;

public class MonitorDashBoardController {

    @FXML private Label lblBemVindo;
    // Aba: Minhas Agendas
    @FXML private TableView<Agenda> tabelaMinhasAgendas;
    @FXML private TableColumn<Agenda, String> colAgHorario;
    @FXML private TableColumn<Agenda, String> colAgLocal;
    @FXML private TableColumn<Agenda, String> colAgVagas;
    @FXML private ComboBox<Disciplina> cbDisciplina;
    @FXML private TextField txtLocal;
    @FXML private TextField txtLink;
    @FXML private DatePicker dpData;
    @FXML private TextField txtHoraInicio;
    @FXML private TextField txtHoraFim;
    @FXML private Spinner<Integer> spVagas;
    // Aba: Agendamentos para registrar
    @FXML private TableView<Agendamento> tabelaAgendamentos;
    @FXML private TableColumn<Agendamento, String> colAgdAluno;
    @FXML private TableColumn<Agendamento, String> colAgdAssunto;
    @FXML private TableColumn<Agendamento, String> colAgdStatus;
    @FXML private CheckBox chkPresenca;
    @FXML private TextArea txtConteudo;
    @FXML private Text errorMessage;
    @FXML private Text successMessage;

    private final AgendaService agendaService = new AgendaService();
    private final AgendamentoService agendamentoService = new AgendamentoService();
    private final DisciplinaService disciplinaService = new DisciplinaService();
    private Monitor monitorLogado;

    @FXML
    public void initialize() {
        monitorLogado = (Monitor) SessionManager.getInstance().getUsuarioLogado();
        lblBemVindo.setText("Monitor: " + monitorLogado.getNome());

        carregarDisciplinas();
        configurarTabelaAgendas();
        configurarTabelaAgendamentos();
        carregarMinhasAgendas();
        carregarAgendamentosParaRegistro();
    }

    private void carregarDisciplinas() {
        List<Disciplina> disciplinas = disciplinaService.listarPorMonitor(monitorLogado.getId());
        monitorLogado.setDisciplinasVinculadas(disciplinas);
        if (cbDisciplina != null)
            cbDisciplina.setItems(FXCollections.observableArrayList(disciplinas));
    }

    private void configurarTabelaAgendas() {
        if (colAgHorario != null)
            colAgHorario.setCellValueFactory(cell -> new SimpleStringProperty(
                    cell.getValue().getDataHoraInicio() + " → " + cell.getValue().getDataHoraFim()));
        if (colAgLocal != null)
            colAgLocal.setCellValueFactory(cell -> {
                String l = cell.getValue().getLocal();
                return new SimpleStringProperty(l != null && !l.isBlank() ? l : cell.getValue().getLink());
            });
        if (colAgVagas != null)
            colAgVagas.setCellValueFactory(cell -> new SimpleStringProperty(
                    cell.getValue().getVagasDisponiveis() + "/" + cell.getValue().getVagasTotais()));
    }

    private void configurarTabelaAgendamentos() {
        if (colAgdAssunto != null)
            colAgdAssunto.setCellValueFactory(cell ->
                    new SimpleStringProperty(cell.getValue().getAssunto()));
        if (colAgdStatus != null)
            colAgdStatus.setCellValueFactory(cell ->
                    new SimpleStringProperty(cell.getValue().getStatus().name()));
    }

    private void carregarMinhasAgendas() {
        if (tabelaMinhasAgendas == null) return;
        List<Agenda> agendas = agendaService.listarAgendasDoMonitor(monitorLogado.getId());
        monitorLogado.setAgendas(agendas);
        tabelaMinhasAgendas.setItems(FXCollections.observableArrayList(agendas));
    }

    private void carregarAgendamentosParaRegistro() {
        if (tabelaAgendamentos == null) return;
        List<Agendamento> ags = agendamentoService.listarParaValidacao(monitorLogado.getId());
        tabelaAgendamentos.setItems(FXCollections.observableArrayList(ags));
    }

    @FXML
    public void handleCadastrarAgenda() {
        errorMessage.setVisible(false);
        Disciplina disciplina = cbDisciplina != null ? cbDisciplina.getValue() : null;
        if (disciplina == null) { showError("Selecione uma disciplina."); return; }
        if (dpData == null || dpData.getValue() == null) { showError("Selecione a data."); return; }

        try {
            Agenda nova = new Agenda();
            nova.setLocal(txtLocal != null ? txtLocal.getText().trim() : "");
            nova.setLink(txtLink != null ? txtLink.getText().trim() : "");
            nova.setVagasTotais(spVagas != null ? spVagas.getValue() : 1);

            String[] ini = (txtHoraInicio != null ? txtHoraInicio.getText() : "08:00").split(":");
            String[] fim = (txtHoraFim != null ? txtHoraFim.getText() : "09:00").split(":");
            LocalDateTime inicio = dpData.getValue().atTime(Integer.parseInt(ini[0]), Integer.parseInt(ini[1]));
            LocalDateTime fimDt  = dpData.getValue().atTime(Integer.parseInt(fim[0]), Integer.parseInt(fim[1]));
            nova.setDataHoraInicio(inicio);
            nova.setDataHoraFim(fimDt);

            agendaService.cadastrarAgenda(monitorLogado, disciplina, nova);
            showSuccess("Agenda cadastrada com sucesso!");
            carregarMinhasAgendas();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML
    public void handleRegistrarAtendimento() {
        Agendamento selecionado = tabelaAgendamentos != null
                ? tabelaAgendamentos.getSelectionModel().getSelectedItem() : null;
        if (selecionado == null) { showError("Selecione um agendamento."); return; }

        String conteudo = txtConteudo != null ? txtConteudo.getText().trim() : "";
        boolean presenca = chkPresenca != null && chkPresenca.isSelected();

        try {
            agendamentoService.registrarAtendimento(monitorLogado, selecionado, presenca, conteudo);
            showSuccess("Atendimento registrado!");
            carregarAgendamentosParaRegistro();
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
