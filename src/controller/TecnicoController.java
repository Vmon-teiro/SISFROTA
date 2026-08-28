package controller;

import dao.TecnicoDAO;
import java.sql.Date;
import java.util.List;

public class TecnicoController {

    private final TecnicoDAO dao = new TecnicoDAO();

    public List<Object[]> obterOSAbertas() {
        return dao.listarOSAbertas();
    }

    public List<Object[]> obterAlertasHorimetro() {
        return dao.listarAlertasHorimetro();
    }

    public List<Object[]> obterHistoricoMotores() {
        return dao.listarHistoricoCompleto();
    }

    public List<Object[]> obterEmbarcacoes() {
        return dao.obterEmbarcacoesSimplificado();
    }

    public String criarNovaOS(int idEmbarcacao, String tipo, String descricao, String horimetroStr, java.util.Date dataAgendamento) {
        if (descricao == null || descricao.trim().isEmpty()) {
            return "A descrição detalhada do serviço é obrigatória.";
        }
        if (dataAgendamento == null) {
            return "A data de agendamento é obrigatória.";
        }

        Integer horimetro = null;
        if (horimetroStr != null && !horimetroStr.trim().isEmpty()) {
            try {
                horimetro = Integer.parseInt(horimetroStr.trim());
            } catch (NumberFormatException e) {
                return "Horímetro deve ser um número inteiro válido.";
            }
        }

        Date dataSQL = new Date(dataAgendamento.getTime());
        boolean ok = dao.salvarNovaOS(idEmbarcacao, tipo, descricao.trim(), horimetro, dataSQL);
        return ok ? "OK" : "Erro ao abrir Ordem de Serviço no banco de dados.";
    }

    public String finalizarManutencao(int idManutencao, int idEmbarcacao, String horimetroStr, String custoStr) {
        try {
            int horimetro = Integer.parseInt(horimetroStr.trim());
            double custo = Double.parseDouble(custoStr.replace(",", ".").trim());

            if (horimetro < 0 || custo < 0) {
                return "Valores não podem ser negativos.";
            }

            boolean ok = dao.concluirOS(idManutencao, idEmbarcacao, horimetro, custo);
            return ok ? "OK" : "Erro ao concluir Ordem de Serviço no banco de dados.";
        } catch (NumberFormatException e) {
            return "Por favor, insira valores numéricos válidos para Horímetro e Custo.";
        }
    }
}