package dao;

import model.Tripulante;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TripulanteDAO {

    public List<Tripulante> listarTodos() {
        List<Tripulante> lista = new ArrayList<>();
        String sql = "SELECT * FROM tripulantes ORDER BY nome ASC";

        try (Connection conn = ConexaoDAO.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Tripulante t = new Tripulante(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("cpf"),
                    rs.getString("categoria_habilitacao"),
                    rs.getString("numero_registro_cir"),
                    rs.getDate("data_vencimento_cir"),
                    rs.getString("status")
                );
                lista.add(t);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar tripulantes: " + e.getMessage());
        }
        return lista;
    }

    public Tripulante buscarPorId(int id) {
        String sql = "SELECT * FROM tripulantes WHERE id = ?";

        try (Connection conn = ConexaoDAO.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Tripulante(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("cpf"),
                        rs.getString("categoria_habilitacao"),
                        rs.getString("numero_registro_cir"),
                        rs.getDate("data_vencimento_cir"),
                        rs.getString("status")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar tripulante por ID: " + e.getMessage());
        }
        return null;
    }

    public boolean cadastrar(Tripulante t) {
        String sql = "INSERT INTO tripulantes (nome, cpf, categoria_habilitacao, numero_registro_cir, data_vencimento_cir, status) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexaoDAO.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, t.getNome());
            stmt.setString(2, t.getCpf());
            stmt.setString(3, t.getCategoriaHabilitacao());
            stmt.setString(4, t.getNumeroRegistroCir());
            stmt.setDate(5, t.getDataVencimentoCir());
            stmt.setString(6, t.getStatus());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao cadastrar tripulante: " + e.getMessage());
            return false;
        }
    }
}