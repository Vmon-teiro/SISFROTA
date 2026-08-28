package dao;

import model.Incidente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IncidenteDAO {

    public boolean salvar(Incidente incidente) {
        String sql = "INSERT INTO incidentes (id_embarcacao, id_viagem, data_incidente, descricao, gravidade, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexaoDAO.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, incidente.getIdEmbarcacao());
            
            if (incidente.getIdViagem() != null && incidente.getIdViagem() > 0) {
                stmt.setInt(2, incidente.getIdViagem());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }

            stmt.setTimestamp(3, Timestamp.valueOf(incidente.getDataIncidente()));
            stmt.setString(4, incidente.getDescricao());
            stmt.setString(5, incidente.getGravidade());
            stmt.setString(6, incidente.getStatus() != null ? incidente.getStatus() : "PENDENTE");

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Object[]> listarIncidentesPendentes() {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT i.id, e.nome AS embarcacao, i.data_incidente, i.descricao, i.gravidade, i.status, i.id_embarcacao " +
                     "FROM incidentes i " +
                     "JOIN embarcacoes e ON i.id_embarcacao = e.id " +
                     "WHERE i.status != 'RESOLVIDO' " +
                     "ORDER BY i.data_incidente DESC";

        try (Connection conn = ConexaoDAO.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getInt("id"),
                    rs.getString("embarcacao"),
                    rs.getTimestamp("data_incidente"),
                    rs.getString("descricao"),
                    rs.getString("gravidade"),
                    rs.getString("status"),
                    rs.getInt("id_embarcacao")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean atualizarStatus(int idIncidente, String novoStatus) {
        String sql = "UPDATE incidentes SET status = ? WHERE id = ?";
        try (Connection conn = ConexaoDAO.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, novoStatus);
            stmt.setInt(2, idIncidente);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}