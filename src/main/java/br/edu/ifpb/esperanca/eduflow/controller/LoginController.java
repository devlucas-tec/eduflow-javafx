package br.edu.ifpb.esperanca.eduflow.controller;

import br.edu.ifpb.esperanca.eduflow.MainApp;
import br.edu.ifpb.esperanca.eduflow.domain.entities.Usuario;
import br.edu.ifpb.esperanca.eduflow.service.UsuarioService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.Optional;

public class LoginController {

    @FXML
    private TextField txtEmail;

    @FXML
    private PasswordField txtSenha;

    @FXML
    private Button btnLogin;

    @FXML
    private Text errorMessage;

    private UsuarioService usuarioService;

    @FXML
    public void initialize() {
        this.usuarioService = new UsuarioService();
        errorMessage.setVisible(false);
    }

    @FXML
    public void handleLogin() {
        String email = txtEmail.getText();
        String senha = txtSenha.getText();

        if (email.isEmpty() || senha.isEmpty()) {
            showError("E-mail e senha são obrigatórios.");
            return;
        }

        try {
            Optional<Usuario> usuarioAutenticado = usuarioService.autenticar(email, senha);

            if (usuarioAutenticado.isPresent()) {
                navegarParaDashboard(usuarioAutenticado.get());
            } else {
                showError("E-mail ou senha inválidos.");
            }
        } catch (Exception e) {
            showError("Erro ao tentar fazer login. Tente novamente.");
            e.printStackTrace(); // Logar a exceção para depuração
        }
    }

    private void navegarParaDashboard(Usuario usuario) throws IOException {
        switch (usuario.getRole()) {
            case ALUNO:
                MainApp.setRoot("aluno_dashboard");
                break;
            case PROFESSOR:
            case MONITOR: // Ambos usam o mesmo dashboard por enquanto
                MainApp.setRoot("professor_dashboard");
                break;
            case ADMINISTRADOR:
                // Criar e redirecionar para o dashboard do admin
                break;
        }
    }

    private void showError(String message) {
        errorMessage.setText(message);
        errorMessage.setVisible(true);
    }
}
