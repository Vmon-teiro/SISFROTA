package dao;

import dto.ConsultaHorarioDTO;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ConsultaHorariosDAO {

    public List<ConsultaHorarioDTO> listarHorariosSaida() {
        List<ConsultaHorarioDTO> lista = new ArrayList<>();
        String sql = "SELECT v.id, e.nome AS embarcacao, t.nome AS comandante, v.rota_destino, " +
                     "       v.data_hora_partida, v.data_hora_chegada, v.quantidade_passageiros, v.status " +
                     "FROM viagens v " +
                     "JOIN embarcacoes e ON v.id_embarcacao = e.id " +
                     "JOIN tripulantes t ON v.id_comandante = t.id " +
                     "ORDER BY v.data_hora_partida DESC";

        try (Connection conn = ConexaoDAO.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Timestamp tsChegada = rs.getTimestamp("data_hora_chegada");
                LocalDateTime chegada = tsChegada != null ? tsChegada.toLocalDateTime() : null;

                ConsultaHorarioDTO dto = new ConsultaHorarioDTO(
                    rs.getInt("id"),
                    rs.getString("embarcacao"),
                    rs.getString("comandante"),
                    rs.getString("rota_destino"),
                    rs.getTimestamp("data_hora_partida").toLocalDateTime(),
                    chegada,
                    rs.getInt("quantidade_passageiros"),
                    rs.getString("status")
                );
                lista.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<String> listarTodasEmbarcacoes() {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT nome FROM embarcacoes ORDER BY nome";
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

    public List<String> listarTodosComandantes() {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT nome FROM tripulantes ORDER BY nome";
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

    public List<String> listarTodosDestinos() {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT DISTINCT rota_destino FROM viagens WHERE rota_destino IS NOT NULL AND rota_destino != '' ORDER BY rota_destino";
        try (Connection conn = ConexaoDAO.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(rs.getString("rota_destino"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}