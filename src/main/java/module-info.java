module br.edu.ifpb.esperanca.eduflow {
    // Módulos do JavaFX que o projeto precisa
    requires javafx.controls;
    requires javafx.fxml;

    // Módulo do Java para acesso a banco de dados (JDBC)
    requires java.sql;

    // Abre os pacotes para que o JavaFX possa acessá-los via reflexão
    opens br.edu.ifpb.esperanca.eduflow to javafx.fxml;
    opens br.edu.ifpb.esperanca.eduflow.controller to javafx.fxml;

    // Exporta o pacote principal para que o JavaFX possa iniciar a aplicação
    exports br.edu.ifpb.esperanca.eduflow;
}
