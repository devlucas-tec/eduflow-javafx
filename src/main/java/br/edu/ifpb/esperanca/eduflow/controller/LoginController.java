package br.edu.ifpb.esperanca.eduflow.controller;

import br.edu.ifpb.esperanca.eduflow.MainApp;
import br.edu.ifpb.esperanca.eduflow.domain.entities.Usuario;
import br.edu.ifpb.esperanca.eduflow.service.SessionManager;
import br.edu.ifpb.esperanca.eduflow.service.UsuarioService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.Optional;

public class LoginController {

    @FXML private TextField txtEmail;
    @FXML private PasswordField txtSenha;
    @FXML private Button btnLogin;
    @FXML private Text errorMessage;

    private UsuarioService usuarioService;

    @FXML
    public void initialize() {
        this.usuarioService = new UsuarioService();
        errorMessage.setVisible(false);
    }

    @FXML
    public void handleLogin() {
        String email = txtEmail.getText().trim();
        String senha = txtSenha.getText();

        if (email.isEmpty() || senha.isEmpty()) {
            showError("E-mail e senha são obrigatórios.");
            return;
        }

        try {
            Optional<Usuario> resultado = usuarioService.autenticar(email, senha);
            if (resultado.isPresent()) {
                SessionManager.getInstance().iniciarSessao(resultado.get());
                navegarParaDashboard(resultado.get());
            } else {
                showError("E-mail ou senha inválidos.");
            }
        } catch (Exception e) {
            showError("Erro ao conectar. Verifique sua conexão.");
            e.printStackTrace();
        }
    }

    @FXML
    public void handleIrParaCadastro() throws IOException {
        MainApp.setRoot("cadastro");
    }

    private void navegarParaDashboard(Usuario usuario) throws IOException {
        switch (usuario.getRole()) {
            case ALUNO     -> MainApp.setRoot("aluno_dashboard");
            case MONITOR   -> MainApp.setRoot("monitor_dashboard");
            case PROFESSOR -> MainApp.setRoot("professor_dashboard");
            case ADMINISTRADOR -> MainApp.setRoot("admin_dashboard");
        }
    }

    private void showError(String message) {
        errorMessage.setText(message);
        errorMessage.setVisible(true);
    }
}
