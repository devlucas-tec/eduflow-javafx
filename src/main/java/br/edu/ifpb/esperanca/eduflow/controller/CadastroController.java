package br.edu.ifpb.esperanca.eduflow.controller;

import br.edu.ifpb.esperanca.eduflow.MainApp;
import br.edu.ifpb.esperanca.eduflow.domain.entities.Aluno;
import br.edu.ifpb.esperanca.eduflow.service.UsuarioService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import java.io.IOException;

public class CadastroController {

    @FXML private TextField txtNome;
    @FXML private TextField txtEmail;
    @FXML private TextField txtMatricula;
    @FXML private PasswordField txtSenha;
    @FXML private PasswordField txtConfirmaSenha;
    @FXML private Text errorMessage;
    @FXML private Text successMessage;

    private final UsuarioService usuarioService = new UsuarioService();

    @FXML
    public void initialize() {
        errorMessage.setVisible(false);
        successMessage.setVisible(false);
    }

    @FXML
    public void handleCadastrar() {
        errorMessage.setVisible(false);
        successMessage.setVisible(false);

        String nome = txtNome.getText().trim();
        String email = txtEmail.getText().trim();
        String matricula = txtMatricula.getText().trim();
        String senha = txtSenha.getText();
        String confirma = txtConfirmaSenha.getText();

        if (nome.isBlank() || email.isBlank() || matricula.isBlank() || senha.isBlank()) {
            showError("Todos os campos são obrigatórios.");
            return;
        }
        if (!senha.equals(confirma)) {
            showError("As senhas não coincidem.");
            return;
        }

        try {
            Aluno aluno = new Aluno();
            aluno.setNome(nome);
            aluno.setEmail(email);
            aluno.setMatricula(matricula);
            aluno.setSenhaHash(senha); // será hashed no repository
            usuarioService.cadastrar(aluno);

            successMessage.setText("Cadastro realizado! Faça login.");
            successMessage.setVisible(true);
        } catch (Exception e) {
            showError("Erro ao cadastrar: " + e.getMessage());
        }
    }

    @FXML
    public void handleVoltarLogin() throws IOException {
        MainApp.setRoot("login");
    }

    private void showError(String msg) {
        errorMessage.setText(msg);
        errorMessage.setVisible(true);
    }
}
