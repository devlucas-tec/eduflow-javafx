package br.edu.ifpb.esperanca.eduflow.controller;

import br.edu.ifpb.esperanca.eduflow.MainApp;
import br.edu.ifpb.esperanca.eduflow.domain.entities.*;
import br.edu.ifpb.esperanca.eduflow.domain.enums.StatusAgendamento;
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
    @FXML private TabPane tabPane;

    // --- Aba Disciplinas (como aluno) ---
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

    // --- Aba Agendar (como aluno) ---
    @FXML private TableView<Agenda> tabelaAgendas;
    @FXML private TableColumn<Agenda, String> colDisciplina;
    @FXML private TableColumn<Agenda, String> colHorario;
    @FXML private TableColumn<Agenda, String> colLocal;
    @FXML private TableColumn<Agenda, String> colVagas;
    @FXML private TextArea txtAssunto;
    @FXML private Text errorAgendamento;
    @FXML private Text successAgendamento;

    // --- Aba Meus Agendamentos (como aluno) ---
    @FXML private TableView<Agendamento> tabelaMeusAgendamentos;
    @FXML private TableColumn<Agendamento, String> colMeuStatus;
    @FXML private TableColumn<Agendamento, String> colMeuAssunto;
    @FXML private TableColumn<Agendamento, String> colMeuHorario;

    // --- Aba Minha Agenda (monitor) ---
    @FXML private TableView<Agenda> tabelaMinhasAgendas;
    @FXML private TableColumn<Agenda, String> colAgHorario;
    @FXML private TableColumn<Agenda, String> colAgLocal;
    @FXML private TableColumn<Agenda, String> colAgVagas;
    @FXML private ComboBox<Disciplina> cbDisciplina;
    @FXML private Label lblSemVinculo;
    @FXML private TextField txtLocal;
    @FXML private TextField txtLink;
    @FXML private DatePicker dpData;
    @FXML private TextField txtHoraInicio;
    @FXML private TextField txtHoraFim;
    @FXML private Spinner<Integer> spVagas;
    @FXML private Text errorMessage;
    @FXML private Text successMessage;

    // --- Aba Atendimentos (monitor) ---
    @FXML private TableView<Agendamento> tabelaAgendamentos;
    @FXML private TableColumn<Agendamento, String> colAgdAluno;
    @FXML private TableColumn<Agendamento, String> colAgdAssunto;
    @FXML private TableColumn<Agendamento, String> colAgdStatus;
    @FXML private CheckBox chkPresenca;
    @FXML private TextArea txtConteudo;
    @FXML private Text errorAtendimento;
    @FXML private Text successAtendimento;

    // --- Aba Relatórios ---
    @FXML private Label lblTotalAtendimentos;
    @FXML private Label lblTotalFaltas;
    @FXML private Label lblHorasMonitoria;
    @FXML private TableView<Agendamento> tabelaRelatorio;
    @FXML private TableColumn<Agendamento, String> colRelAluno;
    @FXML private TableColumn<Agendamento, String> colRelAssunto;
    @FXML private TableColumn<Agendamento, String> colRelStatus;
    @FXML private TableColumn<Agendamento, String> colRelData;

    private final AgendaService agendaService = new AgendaService();
    private final AgendamentoService agendamentoService = new AgendamentoService();
    private final DisciplinaService disciplinaService = new DisciplinaService();
    private final AlunoDisciplinaService alunoDisciplinaService = new AlunoDisciplinaService();
    private final RelatorioService relatorioService = new RelatorioService();
    private final MonitorDisciplinaService monitorDisciplinaService = new MonitorDisciplinaService();
    private Monitor monitorLogado;

    @FXML
    public void initialize() {
        monitorLogado = (Monitor) SessionManager.getInstance().getUsuarioLogado();
        lblBemVindo.setText("Monitor: " + monitorLogado.getNome());

        configurarTabelaDisciplinas();
        configurarTabelaAgendar();
        configurarTabelaMeusAgendamentos();
        configurarTabelaAgendas();
        configurarTabelaAgendamentos();
        configurarTabelaRelatorio();

        carregarDisciplinas();
        carregarDisciplinasMonitor();

        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, abaAntiga, novaAba) -> {
            if (novaAba != null) {
                String titulo = novaAba.getText();

                switch (titulo) {
                    case "Minhas Disciplinas":
                        carregarDisciplinas();
                        break;
                    case "Agendar Monitoria":
                        carregarAgendas();
                        break;
                    case "Meus Agendamentos":
                        carregarMeusAgendamentos();
                        break;
                    case "Minha Agenda":
                        carregarMinhasAgendas();
                        break;
                    case "Atendimentos":
                        carregarAgendamentosParaRegistro();
                        break;
                }
                limparMensagens();
            }
        });
    }

    private void limparMensagens() {
        if (errorDisciplina != null) errorDisciplina.setVisible(false);
        if (successDisciplina != null) successDisciplina.setVisible(false);
        if (errorAgendamento != null) errorAgendamento.setVisible(false);
        if (successAgendamento != null) successAgendamento.setVisible(false);
        if (errorMessage != null) errorMessage.setVisible(false);
        if (successMessage != null) successMessage.setVisible(false);
        if (errorAtendimento != null) errorAtendimento.setVisible(false);
        if (successAtendimento != null) successAtendimento.setVisible(false);
    }

    // ===================== DISCIPLINAS (ALUNO) =====================

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
                alunoDisciplinaService.listarMatriculadas(monitorLogado.getId())));
        tabelaDisponiveis.setItems(FXCollections.observableArrayList(
                alunoDisciplinaService.listarDisponiveis(monitorLogado.getId())));
    }

    @FXML
    public void handleMatricular() {
        Disciplina selecionada = tabelaDisponiveis.getSelectionModel().getSelectedItem();
        if (selecionada == null) { showErrorDisc("Selecione uma disciplina disponível."); return; }
        try {
            alunoDisciplinaService.matricular(monitorLogado.getId(), selecionada.getId());
            showSuccessDisc("Matriculado em \"" + selecionada.getNome() + "\" com sucesso!");
            carregarDisciplinas();
        } catch (Exception e) { showErrorDisc(e.getMessage()); }
    }

    @FXML
    public void handleCancelarMatricula() {
        Disciplina selecionada = tabelaMatriculadas.getSelectionModel().getSelectedItem();
        if (selecionada == null) { showErrorDisc("Selecione uma disciplina para cancelar."); return; }
        try {
            alunoDisciplinaService.cancelarMatricula(monitorLogado.getId(), selecionada.getId());
            showSuccessDisc("Matrícula cancelada.");
            carregarDisciplinas();
        } catch (Exception e) { showErrorDisc(e.getMessage()); }
    }

    // ===================== AGENDAR (ALUNO) =====================

    private void configurarTabelaAgendar() {
        colDisciplina.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getDisciplina() != null ? c.getValue().getDisciplina().getNome() : "—"));
        colHorario.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getDataHoraInicio() + " → " + c.getValue().getDataHoraFim()));
        colLocal.setCellValueFactory(c -> {
            String l = c.getValue().getLocal();
            return new SimpleStringProperty(l != null && !l.isBlank() ? l : c.getValue().getLink());
        });
        colVagas.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getVagasDisponiveis() + "/" + c.getValue().getVagasTotais()));
    }

    private void carregarAgendas() {
        tabelaAgendas.setItems(FXCollections.observableArrayList(
                agendaService.listarAgendasDisponiveisPorAluno(monitorLogado.getId())));
    }

    @FXML
    public void handleAgendar() {
        Agenda selecionada = tabelaAgendas.getSelectionModel().getSelectedItem();
        if (selecionada == null) { showErrorAg("Selecione um horário."); return; }
        String assunto = txtAssunto != null ? txtAssunto.getText().trim() : "";
        if (assunto.isBlank()) { showErrorAg("Descreva o assunto."); return; }
        try {
            agendamentoService.agendarMonitoria(monitorLogado, selecionada, assunto);
            showSuccessAg("Agendamento realizado!");
            carregarAgendas();
            carregarMeusAgendamentos();
            if (txtAssunto != null) txtAssunto.clear();
        } catch (Exception e) { showErrorAg(e.getMessage()); }
    }

    // ===================== MEUS AGENDAMENTOS (ALUNO) =====================

    private void configurarTabelaMeusAgendamentos() {
        colMeuStatus.setCellValueFactory(c -> {
            Agendamento ag = c.getValue();
            if (ag == null || ag.getStatus() == null) return new SimpleStringProperty("—");

            String statusTexto = ag.getStatus().name();

            if (ag.getJustificativa() != null && !ag.getJustificativa().isBlank()) {
                statusTexto += " (" + ag.getJustificativa() + ")";
            }

            return new SimpleStringProperty(statusTexto);
        });

        colMeuAssunto.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue() != null ? c.getValue().getAssunto() : "—")
        );

        colMeuHorario.setCellValueFactory(c -> {
            if (c.getValue() != null &&
                    c.getValue().getAgenda() != null &&
                    c.getValue().getAgenda().getDataHoraInicio() != null) {

                LocalDateTime inicio = c.getValue().getAgenda().getDataHoraInicio();
                return new SimpleStringProperty(inicio.toString().replace("T", " "));
            }
            return new SimpleStringProperty("Horário não definido");
        });
    }

    private void carregarMeusAgendamentos() {
        tabelaMeusAgendamentos.setItems(FXCollections.observableArrayList(
                agendamentoService.listarTodosPorAluno(monitorLogado.getId())));
    }

    @FXML
    public void handleCancelarAgendamento() {
        Agendamento selecionado = tabelaMeusAgendamentos.getSelectionModel().getSelectedItem();
        if (selecionado == null) { return; }
        try {
            agendamentoService.cancelarPeloAluno(selecionado, monitorLogado);
            carregarMeusAgendamentos();
            carregarAgendas();
        } catch (Exception e) { showError(e.getMessage()); }
    }

    // ===================== MINHA AGENDA (MONITOR) =====================

    private void carregarDisciplinasMonitor() {
        monitorDisciplinaService.buscarDisciplinaDoMonitor(monitorLogado.getId())
                .ifPresentOrElse(
                        disciplina -> {
                            monitorLogado.setDisciplinasVinculadas(java.util.List.of(disciplina));
                            if (cbDisciplina != null)
                                cbDisciplina.setItems(FXCollections.observableArrayList(disciplina));
                            if (lblSemVinculo != null) {
                                lblSemVinculo.setVisible(false);
                                lblSemVinculo.setManaged(false);
                            }
                        },
                        () -> {
                            monitorLogado.setDisciplinasVinculadas(java.util.List.of());
                            if (cbDisciplina != null) cbDisciplina.getItems().clear();
                            if (lblSemVinculo != null) {
                                lblSemVinculo.setVisible(true);
                                lblSemVinculo.setManaged(true);
                            }
                        }
                );
    }

    private void configurarTabelaAgendas() {
        if (colAgHorario != null)
            colAgHorario.setCellValueFactory(c -> new SimpleStringProperty(
                    c.getValue().getDataHoraInicio() + " → " + c.getValue().getDataHoraFim()));
        if (colAgLocal != null)
            colAgLocal.setCellValueFactory(c -> {
                String l = c.getValue().getLocal();
                return new SimpleStringProperty(l != null && !l.isBlank() ? l : c.getValue().getLink());
            });
        if (colAgVagas != null)
            colAgVagas.setCellValueFactory(c -> new SimpleStringProperty(
                    c.getValue().getVagasDisponiveis() + "/" + c.getValue().getVagasTotais()));
    }

    private void carregarMinhasAgendas() {
        if (tabelaMinhasAgendas == null) return;
        List<Agenda> agendas = agendaService.listarAgendasDoMonitor(monitorLogado.getId());
        tabelaMinhasAgendas.setItems(FXCollections.observableArrayList(agendas));
    }

    @FXML
    public void handleCadastrarAgenda() {
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
        } catch (Exception e) { showError(e.getMessage()); }
    }

    // ===================== ATENDIMENTOS (MONITOR) =====================

    private void configurarTabelaAgendamentos() {
        colAgdAluno.setCellValueFactory(c -> {
            if (c.getValue().getAluno() != null) {
                return new SimpleStringProperty(c.getValue().getAluno().getNome());
            }
            return new SimpleStringProperty("Desconhecido");
        });

        colAgdAssunto.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAssunto()));

        colAgdStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus().name()));
    }

    private void carregarAgendamentosParaRegistro() {
        if (tabelaAgendamentos == null) return;
        tabelaAgendamentos.setItems(FXCollections.observableArrayList(
                agendamentoService.listarParaValidacao(monitorLogado.getId())));
    }

    @FXML
    public void handleRegistrarAtendimento() {
        Agendamento selecionado = tabelaAgendamentos.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            showErrorAtendimento("Selecione um agendamento.");
            return;
        }
        String conteudo = txtConteudo != null ? txtConteudo.getText().trim() : "";
        boolean presenca = chkPresenca != null && chkPresenca.isSelected();
        try {
            agendamentoService.registrarAtendimento(monitorLogado, selecionado, presenca, conteudo);
            showSuccessAtendimento("Atendimento registrado com sucesso!");
            carregarAgendamentosParaRegistro();
            // Limpa os campos após registro bem-sucedido
            if (txtConteudo != null) txtConteudo.clear();
            if (chkPresenca != null) chkPresenca.setSelected(false);
        } catch (Exception e) {
            showErrorAtendimento(e.getMessage());
        }
    }

    @FXML
    public void handleCancelarSessaoMonitoria() {
        Agenda agendaSelecionada = tabelaMinhasAgendas.getSelectionModel().getSelectedItem();

        if (agendaSelecionada == null) {
            showError("Selecione um horário na tabela para cancelar.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Cancelar Sessão de Monitoria");
        dialog.setHeaderText("Você está cancelando este horário para TODOS os alunos.");
        dialog.setContentText("Informe a justificativa:");

        dialog.showAndWait().ifPresent(justificativa -> {
            if (justificativa.trim().isEmpty()) {
                showError("A justificativa é obrigatória.");
                return;
            }

            try {
                agendaService.cancelarSessao(agendaSelecionada.getId(), justificativa.trim());

                List<Agendamento> agendamentosDaSessao = agendamentoService.listarPorAgenda(agendaSelecionada.getId());
                for (Agendamento ag : agendamentosDaSessao) {
                    agendamentoService.cancelarPeloMonitor(ag, justificativa.trim());
                }

                if (agendamentosDaSessao.isEmpty()) {
                    showSuccess("Sessão cancelada (não havia alunos agendados).");
                } else {
                    showSuccess("Sessão cancelada. Justificativa registrada para " + agendamentosDaSessao.size() + " aluno(s).");
                }

                carregarMinhasAgendas();

            } catch (Exception e) {
                showError("Erro ao cancelar sessão: " + e.getMessage());
            }
        });
    }

    // ===================== RELATÓRIOS =====================

    private void configurarTabelaRelatorio() {
        colRelAluno.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getAluno() != null ? c.getValue().getAluno().getNome() : "—"));
        colRelAssunto.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAssunto()));
        colRelStatus.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getStatus() == StatusAgendamento.REALIZADO ? "✅ Presente" : "❌ Faltou"));
        colRelData.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getDataHoraSolicitacao() != null
                        ? c.getValue().getDataHoraSolicitacao().toLocalDate().toString() : "—"));
    }

    @FXML
    public void handleGerarRelatorio() {
        long atendimentos = relatorioService.totalAtendimentos(monitorLogado.getId());
        long faltas = relatorioService.totalFaltas(monitorLogado.getId());
        long horas = relatorioService.horasDeMonitoria(monitorLogado.getId());

        lblTotalAtendimentos.setText(String.valueOf(atendimentos));
        lblTotalFaltas.setText(String.valueOf(faltas));
        lblHorasMonitoria.setText(horas + "h");

        List<Agendamento> detalhes = relatorioService.listarAtendimentosFinalizados(monitorLogado.getId());
        tabelaRelatorio.setItems(FXCollections.observableArrayList(detalhes));
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
    private void showErrorAg(String msg) {
        if (errorAgendamento != null) { errorAgendamento.setText(msg); errorAgendamento.setVisible(true); }
        if (successAgendamento != null) successAgendamento.setVisible(false);
    }
    private void showSuccessAg(String msg) {
        if (successAgendamento != null) { successAgendamento.setText(msg); successAgendamento.setVisible(true); }
        if (errorAgendamento != null) errorAgendamento.setVisible(false);
    }
    private void showError(String msg) {
        if (errorMessage != null) { errorMessage.setText(msg); errorMessage.setVisible(true); }
        if (successMessage != null) successMessage.setVisible(false);
    }
    private void showSuccess(String msg) {
        if (successMessage != null) { successMessage.setText(msg); successMessage.setVisible(true); }
        if (errorMessage != null) errorMessage.setVisible(false);
    }


    private void showErrorAtendimento(String msg) {
        if (errorAtendimento != null) {
            errorAtendimento.setText(msg);
            errorAtendimento.setVisible(true);
            if (successAtendimento != null) successAtendimento.setVisible(false);
        } else {
            new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait();
        }
    }
    private void showSuccessAtendimento(String msg) {
        if (successAtendimento != null) {
            successAtendimento.setText(msg);
            successAtendimento.setVisible(true);
            if (errorAtendimento != null) errorAtendimento.setVisible(false);
        } else {
            new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
        }
    }
}