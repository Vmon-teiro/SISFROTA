package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TecnicoDAO {

    public List<Object[]> listarOSAbertas() {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT m.id, e.nome AS embarcacao, m.tipo_manutencao, m.descricao_servico, " +
                     "m.data_agendamento, m.custo_total, m.status, m.id_embarcacao " +
                     "FROM manutencoes m " +
                     "JOIN embarcacoes e ON m.id_embarcacao = e.id " +
                     "WHERE m.status IN ('AGENDADA', 'EM_ANDAMENTO') " +
                     "ORDER BY m.data_agendamento ASC";

        try (Connection conn = ConexaoDAO.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getInt("id"),
                    rs.getString("embarcacao"),
                    rs.getString("tipo_manutencao"),
                    rs.getString("descricao_servico"),
                    rs.getDate("data_agendamento"),
                    rs.getDouble("custo_total"),
                    rs.getString("status"),
                    rs.getInt("id_embarcacao")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean salvarNovaOS(int idEmbarcacao, String tipo, String descricao, Integer horimetroAgendado, Date dataAgendamento) {
        String sql = "INSERT INTO manutencoes (id_embarcacao, tipo_manutencao, descricao_servico, horimetro_agendado, data_agendamento, status) " +
                     "VALUES (?, ?, ?, ?, ?, 'AGENDADA')";
        try (Connection conn = ConexaoDAO.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idEmbarcacao);
            stmt.setString(2, tipo);
            stmt.setString(3, descricao);
            if (horimetroAgendado != null && horimetroAgendado > 0) {
                stmt.setInt(4, horimetroAgendado);
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            stmt.setDate(5, dataAgendamento);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean concluirOS(int idManutencao, int idEmbarcacao, int novoHorimetro, double custoFinal) {
        String sqlOS = "UPDATE manutencoes SET status = 'CONCLUIDA', custo_total = ?, data_execucao = CURRENT_DATE WHERE id = ?";
        String sqlEmb = "UPDATE embarcacoes SET horimetro_horas = ?, status = 'ATIVA' WHERE id = ?";

        Connection conn = null;
        try {
            conn = ConexaoDAO.obterConexao();
            conn.setAutoCommit(false);

            try (PreparedStatement stmtOS = conn.prepareStatement(sqlOS);
                 PreparedStatement stmtEmb = conn.prepareStatement(sqlEmb)) {

                stmtOS.setDouble(1, custoFinal);
                stmtOS.setInt(2, idManutencao);
                stmtOS.executeUpdate();

                stmtEmb.setInt(1, novoHorimetro);
                stmtEmb.setInt(2, idEmbarcacao);
                stmtEmb.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Object[]> listarAlertasHorimetro() {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT e.id, e.nome, e.horimetro_horas, m.horimetro_agendado, m.descricao_servico " +
                     "FROM embarcacoes e " +
                     "JOIN manutencoes m ON e.id = m.id_embarcacao " +
                     "WHERE m.status = 'AGENDADA' AND m.horimetro_agendado IS NOT NULL " +
                     "AND e.horimetro_horas >= (m.horimetro_agendado - 50)";

        try (Connection conn = ConexaoDAO.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getInt("horimetro_horas"),
                    rs.getInt("horimetro_agendado"),
                    rs.getString("descricao_servico")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<Object[]> listarHistoricoCompleto() {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT m.id, e.nome, m.tipo_manutencao, m.descricao_servico, m.data_execucao, m.custo_total " +
                     "FROM manutencoes m " +
                     "JOIN embarcacoes e ON m.id_embarcacao = e.id " +
                     "WHERE m.status = 'CONCLUIDA' " +
                     "ORDER BY m.data_execucao DESC";

        try (Connection conn = ConexaoDAO.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("tipo_manutencao"),
                    rs.getString("descricao_servico"),
                    rs.getDate("data_execucao"),
                    rs.getDouble("custo_total")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<Object[]> obterEmbarcacoesSimplificado() {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT id, nome FROM embarcacoes ORDER BY nome ASC";
        try (Connection conn = ConexaoDAO.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(new Object[]{rs.getInt("id"), rs.getString("nome")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}