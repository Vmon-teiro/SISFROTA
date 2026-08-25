package dao;

import model.Manutencao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ManutencaoDAO {

    public List<Manutencao> listarTodas() {
        List<Manutencao> lista = new ArrayList<>();
        String sql = "SELECT m.*, e.nome AS nome_embarcacao " +
                     "FROM manutencoes m " +
                     "JOIN embarcacoes e ON m.id_embarcacao = e.id " +
                     "ORDER BY m.data_agendamento DESC";

        try (Connection conn = ConexaoDAO.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Manutencao m = new Manutencao(
                    rs.getInt("id"),
                    rs.getInt("id_embarcacao"),
                    rs.getString("nome_embarcacao"),
                    rs.getString("tipo_manutencao"),
                    rs.getString("descricao_servico"),
                    (Integer) rs.getObject("horimetro_agendado"),
                    rs.getDate("data_agendamento"),
                    rs.getDate("data_execucao"),
                    rs.getDouble("custo_total"),
                    rs.getString("status")
                );
                lista.add(m);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar manutenções: " + e.getMessage());
        }
        return lista;
    }

    public Manutencao buscarPorId(int id) {
        String sql = "SELECT m.*, e.nome AS nome_embarcacao " +
                     "FROM manutencoes m " +
                     "JOIN embarcacoes e ON m.id_embarcacao = e.id " +
                     "WHERE m.id = ?";

        try (Connection conn = ConexaoDAO.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Manutencao(
                        rs.getInt("id"),
                        rs.getInt("id_embarcacao"),
                        rs.getString("nome_embarcacao"),
                        rs.getString("tipo_manutencao"),
                        rs.getString("descricao_servico"),
                        (Integer) rs.getObject("horimetro_agendado"),
                        rs.getDate("data_agendamento"),
                        rs.getDate("data_execucao"),
                        rs.getDouble("custo_total"),
                        rs.getString("status")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar manutenção por ID: " + e.getMessage());
        }
        return null;
    }

    public boolean cadastrar(Manutencao m) {
        String sql = "INSERT INTO manutencoes (id_embarcacao, tipo_manutencao, descricao_servico, horimetro_agendado, data_agendamento, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexaoDAO.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, m.getIdEmbarcacao());
            stmt.setString(2, m.getTipoManutencao());
            stmt.setString(3, m.getDescricaoServico());
            
            if (m.getHorimetroAgendado() != null) {
                stmt.setInt(4, m.getHorimetroAgendado());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            
            stmt.setDate(5, m.getDataAgendamento());
            stmt.setString(6, m.getStatus());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao agendar manutenção: " + e.getMessage());
            return false;
        }
    }

    public boolean concluirManutencao(int id, Date dataExecucao, double custoTotal) {
        String sql = "UPDATE manutencoes SET data_execucao = ?, custo_total = ?, status = 'CONCLUIDA' WHERE id = ?";

        try (Connection conn = ConexaoDAO.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, dataExecucao);
            stmt.setDouble(2, custoTotal);
            stmt.setInt(3, id);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao concluir manutenção: " + e.getMessage());
            return false;
        }
    }

    public double getTotalManutencaoPorEmbarcacao(int embarcacaoId, LocalDate inicio, LocalDate fim) {
        String sql = "SELECT SUM(custo_total) FROM manutencoes WHERE id_embarcacao = ? AND status = 'CONCLUIDA' AND data_agendamento BETWEEN ? AND ?";

        try (Connection conn = ConexaoDAO.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, embarcacaoId);
            stmt.setDate(2, Date.valueOf(inicio));
            stmt.setDate(3, Date.valueOf(fim));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao somar custos de manutenção: " + e.getMessage());
        }
        return 0.0;
    }
}