package dao;

import model.Incidente;
import java.sql.*;

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
}