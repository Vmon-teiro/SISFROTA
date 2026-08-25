package controller;

import dao.ConexaoDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AlertaController {

    public List<String> verificarAlertasVencimento() {
        List<String> alertas = new ArrayList<>();

        // Query 1: Validação de CIR dos tripulantes vencidas ou a vencer em 15 dias
        String sqlTripulantes = "SELECT nome, data_vencimento_cir FROM tripulantes " +
                                "WHERE data_vencimento_cir <= DATE_ADD(CURDATE(), INTERVAL 15 DAY) " +
                                "AND status != 'INATIVO'";

        // Query 2: Manutenções pendentes/agendadas para os próximos 15 dias
        String sqlManutencoes = "SELECT e.nome AS embarcacao, m.descricao_servico, m.data_agendamento " +
                                "FROM manutencoes m " +
                                "JOIN embarcacoes e ON m.id_embarcacao = e.id " +
                                "WHERE m.data_agendamento <= DATE_ADD(CURDATE(), INTERVAL 15 DAY) " +
                                "AND m.status = 'AGENDADA'";

        try (Connection conn = ConexaoDAO.obterConexao()) {
            // Consulta Tripulantes
            try (PreparedStatement stmt = conn.prepareStatement(sqlTripulantes);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    alertas.add(" Tripulante: " + rs.getString("nome") + 
                                " | CIR vence em: " + rs.getDate("data_vencimento_cir"));
                }
            }

            // Consulta Manutenções
            try (PreparedStatement stmt = conn.prepareStatement(sqlManutencoes);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    alertas.add(" Manutenção: " + rs.getString("embarcacao") + 
                                " (" + rs.getString("descricao_servico") + ") | Data: " + rs.getDate("data_agendamento"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao verificar alertas de vencimento: " + e.getMessage());
        }

        return alertas;
    }
}