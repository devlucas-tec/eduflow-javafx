package br.edu.ifpb.esperanca.eduflow.repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ConectionFactory {

    private static Connection connection;

    // Armazena as variáveis lidas do .env
    private static final Map<String, String> envVars = new HashMap<>();

    static {
        carregarEnv();
    }

    private ConectionFactory() {}

    /**
     * Lê o arquivo .env da raiz do classpath.
     * Ignora linhas em branco e comentários (#).
     */
    private static void carregarEnv() {
        // Tenta ler o .env do classpath (coloque o .env em src/main/resources)
        try (InputStream is = ConectionFactory.class.getClassLoader().getResourceAsStream(".env")) {
            if (is != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String linha;
                while ((linha = reader.readLine()) != null) {
                    linha = linha.trim();
                    if (linha.isEmpty() || linha.startsWith("#")) continue;
                    int idx = linha.indexOf('=');
                    if (idx > 0) {
                        String chave = linha.substring(0, idx).trim();
                        String valor = linha.substring(idx + 1).trim();
                        // Remove aspas se houver
                        if (valor.startsWith("\"") && valor.endsWith("\"")) {
                            valor = valor.substring(1, valor.length() - 1);
                        }
                        envVars.put(chave, valor);
                    }
                }
                System.out.println("[ConectionFactory] .env carregado do classpath com sucesso.");
            } else {
                System.out.println("[ConectionFactory] .env não encontrado no classpath, usando variáveis de ambiente do sistema.");
            }
        } catch (IOException e) {
            System.err.println("[ConectionFactory] Erro ao ler .env: " + e.getMessage());
        }
    }

    /**
     * Retorna o valor da variável: primeiro tenta o .env,
     * depois tenta variável de ambiente do sistema (compatível com EnvFile do IntelliJ).
     */
    private static String getVar(String chave) {
        String valor = envVars.get(chave);
        if (valor != null && !valor.isEmpty()) return valor;
        return System.getenv(chave); // fallback para EnvFile / CI
    }

    /**
     * Retorna uma conexão válida, reconectando se necessário
     * (Neon fecha conexões ociosas após um tempo).
     */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed() || !connection.isValid(3)) {
                connection = criarConexao();
            }
        } catch (SQLException e) {
            // isClosed() / isValid() lançou exceção — tenta reconectar
            connection = criarConexao();
        }
        return connection;
    }

    private static Connection criarConexao() {
        String url      = getVar("DB_URL");
        String user     = getVar("DB_USER");
        String password = getVar("DB_PASSWORD");

        if (url == null || url.isBlank()) {
            throw new RuntimeException(
                    "DB_URL não encontrada! Verifique se o arquivo .env está em src/main/resources/ " +
                            "ou se as variáveis de ambiente estão configuradas corretamente."
            );
        }
        if (user == null || user.isBlank()) {
            throw new RuntimeException(
                    "DB_USER não encontrada! Verifique o arquivo .env."
            );
        }

        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("[ConectionFactory] Conexão estabelecida com sucesso.");
            return conn;
        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erro ao conectar ao banco de dados. Verifique as credenciais no .env.\nDetalhe: " + e.getMessage(), e
            );
        }
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                throw new RuntimeException("Erro ao fechar a conexão.", e);
            }
        }
    }
}