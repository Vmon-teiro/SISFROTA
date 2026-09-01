package dao;

import controller.RelatorioController.CustoEmbarcacaoDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RelatorioDAO {

    public List<CustoEmbarcacaoDTO> obterResumoCustosPorEmbarcacao() {
        List<CustoEmbarcacaoDTO> lista = new ArrayList<>();
        String sql = "SELECT e.id, e.nome AS embarcacao, " +
                     "COALESCE(m.total_manutencao, 0) AS total_manutencao, " +
                     "COALESCE(a.total_abastecimento, 0) AS total_abastecimento, " +
                     "(COALESCE(m.total_manutencao, 0) + COALESCE(a.total_abastecimento, 0)) AS custo_total " +
                     "FROM embarcacoes e " +
                     "LEFT JOIN (SELECT id_embarcacao, SUM(custo_total) AS total_manutencao FROM manutencoes WHERE status != 'CANCELADA' GROUP BY id_embarcacao) m ON e.id = m.id_embarcacao " +
                     "LEFT JOIN (SELECT id_embarcacao, SUM(valor_total) AS total_abastecimento FROM abastecimentos GROUP BY id_embarcacao) a ON e.id = a.id_embarcacao " +
                     "ORDER BY custo_total DESC";

        try (Connection conn = ConexaoDAO.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(new CustoEmbarcacaoDTO(
                    rs.getInt("id"),
                    rs.getString("embarcacao"),
                    rs.getDouble("total_manutencao"),
                    rs.getDouble("total_abastecimento"),
                    rs.getDouble("custo_total")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao obter resumo de custos no DAO: " + e.getMessage());
        }
        return lista;
    }
}