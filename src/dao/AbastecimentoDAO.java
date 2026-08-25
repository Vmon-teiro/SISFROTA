package dao;

import model.Abastecimento;
import java.sql.*;
import java.time.LocalDate;

public class AbastecimentoDAO {

    public boolean salvar(Abastecimento abastecimento) {
        String sql = "INSERT INTO abastecimentos (id_embarcacao, data_abastecimento, quantidade_litros, valor_total, fornecedor_posto) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexaoDAO.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, abastecimento.getEmbarcacaoId());
            stmt.setTimestamp(2, Timestamp.valueOf(abastecimento.getData().atStartOfDay()));
            stmt.setDouble(3, abastecimento.getLitros());
            stmt.setDouble(4, abastecimento.getValorTotal());
            stmt.setString(5, abastecimento.getFornecedor());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public double getTotalAbastecimentoPorEmbarcacao(int embarcacaoId, LocalDate inicio, LocalDate fim) {
        String sql = "SELECT SUM(valor_total) FROM abastecimentos WHERE id_embarcacao = ? AND data_abastecimento BETWEEN ? AND ?";
        try (Connection conn = ConexaoDAO.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, embarcacaoId);
            stmt.setTimestamp(2, Timestamp.valueOf(inicio.atStartOfDay()));
            stmt.setTimestamp(3, Timestamp.valueOf(fim.atTime(23, 59, 59)));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}