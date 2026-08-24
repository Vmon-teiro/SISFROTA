package dao;

import model.Viagem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ViagemDAO {

    public boolean salvar(Viagem viagem) {
        String sql = "INSERT INTO viagens (id_embarcacao, id_tripulante, destino, data_saida, status) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConexaoDAO.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, viagem.getIdEmbarcacao());
            stmt.setInt(2, viagem.getIdTripulante());
            stmt.setString(3, viagem.getDestino());
            stmt.setTimestamp(4, Timestamp.valueOf(viagem.getDataSaida()));
            stmt.setString(5, viagem.getStatus());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao salvar viagem: " + e.getMessage());
            return false;
        }
    }

    public List<Viagem> listarTodas() {
        List<Viagem> lista = new ArrayList<>();
        String sql = "SELECT * FROM viagens ORDER BY data_saida DESC";

        try (Connection conn = ConexaoDAO.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Viagem v = new Viagem();
                v.setId(rs.getInt("id"));
                v.setIdEmbarcacao(rs.getInt("id_embarcacao"));
                v.setIdTripulante(rs.getInt("id_tripulante"));
                v.setDestino(rs.getString("destino"));
                v.setDataSaida(rs.getTimestamp("data_saida").toLocalDateTime());
                v.setStatus(rs.getString("status"));
                lista.add(v);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar viagens: " + e.getMessage());
        }
        return lista;
    }
}