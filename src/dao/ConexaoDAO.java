package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe responsável por gerenciar a conexão JDBC com o MySQL (XAMPP).
 */
public class ConexaoDAO {

    private static final String URL = "jdbc:mysql://localhost:3306/gestao_nautica_db?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // Senha padrão do XAMPP (em branco)

    public static Connection obterConexao() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL JDBC não foi encontrado na biblioteca do projeto!", e);
        }
    }
}