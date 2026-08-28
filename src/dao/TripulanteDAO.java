package dao;

import model.Tripulante;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TripulanteDAO {

    public boolean salvar(Tripulante t) {
        String sql = "INSERT INTO tripulantes (nome, cpf, categoria_habilitacao, numero_registro_cir, data_vencimento_cir, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexaoDAO.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, t.getNome());
            stmt.setString(2, t.getCpf());
            stmt.setString(3, t.getCategoriaHabilitacao());
            stmt.setString(4, t.getNumeroRegistroCir());
            stmt.setDate(5, t.getDataVencimentoCir());
            stmt.setString(6, t.getStatus() != null ? t.getStatus() : "DISPONIVEL");

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean atualizar(Tripulante t) {
        String sql = "UPDATE tripulantes SET nome = ?, cpf = ?, categoria_habilitacao = ?, " +
                     "numero_registro_cir = ?, data_vencimento_cir = ?, status = ? WHERE id = ?";
        try (Connection conn = ConexaoDAO.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, t.getNome());
            stmt.setString(2, t.getCpf());
            stmt.setString(3, t.getCategoriaHabilitacao());
            stmt.setString(4, t.getNumeroRegistroCir());
            stmt.setDate(5, t.getDataVencimentoCir());
            stmt.setString(6, t.getStatus());
            stmt.setInt(7, t.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Tripulante> listarTodos() {
        List<Tripulante> lista = new ArrayList<>();
        String sql = "SELECT * FROM tripulantes ORDER BY nome ASC";

        try (Connection conn = ConexaoDAO.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Tripulante t = new Tripulante();
                t.setId(rs.getInt("id"));
                t.setNome(rs.getString("nome"));
                t.setCpf(rs.getString("cpf"));
                t.setCategoriaHabilitacao(rs.getString("categoria_habilitacao"));
                t.setNumeroRegistroCir(rs.getString("numero_registro_cir"));
                t.setDataVencimentoCir(rs.getDate("data_vencimento_cir"));
                t.setStatus(rs.getString("status"));
                lista.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean excluir(int id) {
        String sql = "DELETE FROM tripulantes WHERE id = ?";
        try (Connection conn = ConexaoDAO.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}