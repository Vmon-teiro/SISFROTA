package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ManutencaoDAO {

    public boolean salvar(int idEmbarcacao, String tipo, String descricao, Integer horimetro, Date dataAgendada, double custoEstimado) {
        String sql = "INSERT INTO manutencoes (id_embarcacao, tipo_manutencao, descricao_servico, horimetro_agendado, data_agendamento, custo_total, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, 'AGENDADA')";

        try (Connection conn = ConexaoDAO.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idEmbarcacao);
            stmt.setString(2, tipo);
            stmt.setString(3, descricao);
            
            if (horimetro != null && horimetro > 0) {
                stmt.setInt(4, horimetro);
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            
            stmt.setDate(5, dataAgendada);
            stmt.setDouble(6, custoEstimado);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Object[]> listarTodasManutencoes() {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT m.id, e.nome AS embarcacao, m.tipo_manutencao, m.descricao_servico, " +
                     "m.horimetro_agendado, m.data_agendamento, m.custo_total, m.status " +
                     "FROM manutencoes m " +
                     "JOIN embarcacoes e ON m.id_embarcacao = e.id " +
                     "ORDER BY m.data_agendamento DESC";

        try (Connection conn = ConexaoDAO.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getInt("id"),
                    rs.getString("embarcacao"),
                    rs.getString("tipo_manutencao"),
                    rs.getString("descricao_servico"),
                    rs.getObject("horimetro_agendado") != null ? rs.getInt("horimetro_agendado") : "-",
                    rs.getDate("data_agendamento"),
                    rs.getDouble("custo_total"),
                    rs.getString("status")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean alterarStatus(int idManutencao, String novoStatus) {
        String sql = "UPDATE manutencoes SET status = ? WHERE id = ?";
        try (Connection conn = ConexaoDAO.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, novoStatus);
            stmt.setInt(2, idManutencao);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}