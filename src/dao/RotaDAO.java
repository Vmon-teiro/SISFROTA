package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RotaDAO {

    public List<String> listarTodas() {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT nome FROM rotas ORDER BY nome ASC";

        try (Connection conn = ConexaoDAO.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(rs.getString("nome"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}