package controller;

import dao.ConexaoDAO;
import model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AlertaController {

    public List<String> verificarAlertasVencimento(Usuario usuario) {
        List<String> alertas = new ArrayList<>();

        if (usuario == null || usuario.getPerfil() == null) {
            return alertas;
        }

        String perfil = usuario.getPerfil().toUpperCase();

        // Mapeamento de permissões de visualização por perfil
        boolean verTripulantes = perfil.equals("ADMINISTRADOR") || perfil.equals("OPERADOR");
        boolean verManutencoes  = perfil.equals("ADMINISTRADOR") || perfil.equals("TECNICO");

        try (Connection conn = ConexaoDAO.obterConexao()) {

            if (verTripulantes) {
                carregarAlertasTripulantes(conn, alertas);
            }

            if (verManutencoes) {
                carregarAlertasManutencoes(conn, alertas);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao verificar alertas de vencimento: " + e.getMessage());
        }

        return alertas;
    }

    private void carregarAlertasTripulantes(Connection conn, List<String> alertas) throws SQLException {
        String sqlTripulantes = "SELECT nome, data_vencimento_cir FROM tripulantes " +
                                "WHERE data_vencimento_cir <= DATE_ADD(CURDATE(), INTERVAL 15 DAY) " +
                                "AND status != 'INATIVO'";

        try (PreparedStatement stmt = conn.prepareStatement(sqlTripulantes);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                alertas.add("Tripulante: " + rs.getString("nome") + 
                            " | CIR vence em: " + rs.getDate("data_vencimento_cir"));
            }
        }
    }

    private void carregarAlertasManutencoes(Connection conn, List<String> alertas) throws SQLException {
        String sqlManutencoes = "SELECT e.nome AS embarcacao, m.descricao_servico, m.data_agendamento " +
                                "FROM manutencoes m " +
                                "JOIN embarcacoes e ON m.id_embarcacao = e.id " +
                                "WHERE m.data_agendamento <= DATE_ADD(CURDATE(), INTERVAL 15 DAY) " +
                                "AND m.status = 'AGENDADA'";

        try (PreparedStatement stmt = conn.prepareStatement(sqlManutencoes);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                alertas.add("Manutenção: " + rs.getString("embarcacao") + 
                            " (" + rs.getString("descricao_servico") + ") | Data: " + rs.getDate("data_agendamento"));
            }
        }
    }
}