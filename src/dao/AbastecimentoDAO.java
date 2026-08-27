package dao;

import model.Abastecimento;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    public List<AbastecimentoDTO> listarTodos() {
        List<AbastecimentoDTO> lista = new ArrayList<>();
        String sql = "SELECT a.id, e.nome AS embarcacao, a.data_abastecimento, a.quantidade_litros, a.valor_total, a.fornecedor_posto " +
                     "FROM abastecimentos a " +
                     "JOIN embarcacoes e ON a.id_embarcacao = e.id " +
                     "ORDER BY a.data_abastecimento DESC";
        try (Connection conn = ConexaoDAO.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(new AbastecimentoDTO(
                    rs.getInt("id"),
                    rs.getString("embarcacao"),
                    rs.getTimestamp("data_abastecimento").toLocalDateTime().toLocalDate(),
                    rs.getDouble("quantidade_litros"),
                    rs.getDouble("valor_total"),
                    rs.getString("fornecedor_posto")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
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

    public static class AbastecimentoDTO {
        private final int id;
        private final String nomeEmbarcacao;
        private final LocalDate data;
        private final double litros;
        private final double valorTotal;
        private final String fornecedor;

        public AbastecimentoDTO(int id, String nomeEmbarcacao, LocalDate data, double litros, double valorTotal, String fornecedor) {
            this.id = id;
            this.nomeEmbarcacao = nomeEmbarcacao;
            this.data = data;
            this.litros = litros;
            this.valorTotal = valorTotal;
            this.fornecedor = fornecedor;
        }

        public int getId() { return id; }
        public String getNomeEmbarcacao() { return nomeEmbarcacao; }
        public LocalDate getData() { return data; }
        public double getLitros() { return litros; }
        public double getValorTotal() { return valorTotal; }
        public String getFornecedor() { return fornecedor; }
    }
}