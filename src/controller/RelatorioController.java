package controller;

import dao.ConexaoDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RelatorioController {

    public static class CustoEmbarcacaoDTO {
        private String nomeEmbarcacao;
        private int totalManutencoes;
        private double custoTotal;

        public CustoEmbarcacaoDTO(String nomeEmbarcacao, int totalManutencoes, double custoTotal) {
            this.nomeEmbarcacao = nomeEmbarcacao;
            this.totalManutencoes = totalManutencoes;
            this.custoTotal = custoTotal;
        }

        public String getNomeEmbarcacao() { return nomeEmbarcacao; }
        public int getTotalManutencoes() { return totalManutencoes; }
        public double getCustoTotal() { return custoTotal; }
    }

    public List<CustoEmbarcacaoDTO> obterConsolidadoCustos() {
        List<CustoEmbarcacaoDTO> lista = new ArrayList<>();
        String sql = "SELECT e.nome AS embarcacao, COUNT(m.id) AS qtd_manutencoes, COALESCE(SUM(m.custo_total), 0) AS custo_total " +
                     "FROM embarcacoes e " +
                     "LEFT JOIN manutencoes m ON e.id = m.id_embarcacao AND m.status = 'CONCLUIDA' " +
                     "GROUP BY e.id, e.nome " +
                     "ORDER BY custo_total DESC";

        try (Connection conn = ConexaoDAO.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(new CustoEmbarcacaoDTO(
                    rs.getString("embarcacao"),
                    rs.getInt("qtd_manutencoes"),
                    rs.getDouble("custo_total")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao gerar relatório de custos: " + e.getMessage());
        }
        return lista;
    }
}