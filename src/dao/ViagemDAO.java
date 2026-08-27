package dao;

import model.Viagem;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ViagemDAO {

    public boolean salvar(Viagem v) {
        String sql = "INSERT INTO viagens (id_embarcacao, id_comandante, rota_destino, data_hora_partida, quantidade_passageiros, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexaoDAO.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, v.getIdEmbarcacao());
            stmt.setInt(2, v.getIdComandante());
            stmt.setString(3, v.getRotaDestino());
            stmt.setTimestamp(4, Timestamp.valueOf(v.getDataHoraPartida()));
            stmt.setInt(5, v.getQuantidadePassageiros());
            stmt.setString(6, v.getStatus());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean embarcacaoEmViagem(int idEmbarcacao) {
        String sql = "SELECT COUNT(*) FROM viagens WHERE id_embarcacao = ? AND status = 'EM_ANDAMENTO'";
        try (Connection conn = ConexaoDAO.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEmbarcacao);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean comandanteEmViagem(int idComandante) {
        String sql = "SELECT COUNT(*) FROM viagens WHERE id_comandante = ? AND status = 'EM_ANDAMENTO'";
        try (Connection conn = ConexaoDAO.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idComandante);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Viagem> listarTodas() {
        List<Viagem> lista = new ArrayList<>();
        String sql = "SELECT v.*, e.nome AS nome_embarcacao, t.nome AS nome_comandante " +
                     "FROM viagens v " +
                     "JOIN embarcacoes e ON v.id_embarcacao = e.id " +
                     "JOIN tripulantes t ON v.id_comandante = t.id " +
                     "ORDER BY v.data_hora_partida DESC";

        try (Connection conn = ConexaoDAO.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Timestamp partida = rs.getTimestamp("data_hora_partida");
                Timestamp chegada = rs.getTimestamp("data_hora_chegada");

                lista.add(new Viagem(
                    rs.getInt("id"),
                    rs.getInt("id_embarcacao"),
                    rs.getString("nome_embarcacao"),
                    rs.getInt("id_comandante"),
                    rs.getString("nome_comandante"),
                    rs.getString("rota_destino"),
                    partida != null ? partida.toLocalDateTime() : null,
                    chegada != null ? chegada.toLocalDateTime() : null,
                    rs.getInt("quantidade_passageiros"),
                    rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean finalizarViagem(int idViagem) {
        String sql = "UPDATE viagens SET status = 'CONCLUIDA', data_hora_chegada = ? WHERE id = ?";
        try (Connection conn = ConexaoDAO.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(2, idViagem);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean cancelarViagem(int idViagem) {
        String sql = "UPDATE viagens SET status = 'CANCELADA' WHERE id = ?";
        try (Connection conn = ConexaoDAO.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idViagem);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}