package dao;

import model.Embarcacao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmbarcacaoDAO {

    public List<Embarcacao> listarTodas() {
        List<Embarcacao> lista = new ArrayList<>();
        String sql = "SELECT * FROM embarcacoes ORDER BY nome ASC";

        try (Connection conn = ConexaoDAO.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Embarcacao emb = new Embarcacao(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("modelo"),
                    rs.getInt("capacidade_passageiros"),
                    rs.getDouble("capacidade_carga_ton"),
                    rs.getInt("ano_fabricacao"),
                    rs.getInt("horimetro_horas"),
                    rs.getString("status")
                );
                lista.add(emb);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar embarcações: " + e.getMessage());
        }
        return lista;
    }

    public boolean cadastrar(Embarcacao emb) {
        String sql = "INSERT INTO embarcacoes (nome, modelo, capacidade_passageiros, capacidade_carga_ton, ano_fabricacao, horimetro_horas, status) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexaoDAO.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, emb.getNome());
            stmt.setString(2, emb.getModelo());
            stmt.setInt(3, emb.getCapacidadePassageiros());
            stmt.setDouble(4, emb.getCapacidadeCargaTon());
            stmt.setInt(5, emb.getAnoFabricacao());
            stmt.setInt(6, emb.getHorimetroHoras());
            stmt.setString(7, emb.getStatus());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao cadastrar embarcação: " + e.getMessage());
            return false;
        }
    }
}
