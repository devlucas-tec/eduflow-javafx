package br.edu.ifpb.esperanca.eduflow.repository;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConectionFactory {

    private static Connection connection;

    private ConectionFactory() {
        // Construtor privado para evitar instanciação
    }

    public static Connection getConnection() {
        if (connection == null) {
            try {
                // O System.getenv busca os valores que o plugin EnvFile carregou do seu .env
                String url = System.getenv("DB_URL");
                String user = System.getenv("DB_USER");
                String password = System.getenv("DB_PASSWORD");

                if (url == null || user == null) {
                    throw new RuntimeException("Variáveis de ambiente de banco de dados não encontradas!");
                }
                connection = DriverManager.getConnection(url, user, password);
            } catch (SQLException e) {
                throw new RuntimeException("Erro ao conectar ao banco de dados", e);
            }
        }
        return connection;
    }

    private static Properties loadProperties() throws IOException {
        Properties props = new Properties();
        // O class loader vai procurar o arquivo no classpath (dentro de src/main/resources)
        try (InputStream input = ConectionFactory.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                throw new IOException("Arquivo database.properties não encontrado no classpath");
            }
            props.load(input);
        }
        return props;
    }
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                throw new RuntimeException("Erro ao fechar a conexão com o banco de dados", e);
            }
        }
    }
}

