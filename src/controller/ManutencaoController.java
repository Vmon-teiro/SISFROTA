package controller;

import dao.IncidenteDAO;
import dao.ManutencaoDAO;

import java.sql.Date;
import java.util.List;

public class ManutencaoController {

    private final ManutencaoDAO manutencaoDAO = new ManutencaoDAO();
    private final IncidenteDAO incidenteDAO = new IncidenteDAO();

    public List<Object[]> obterIncidentesPendentes() {
        return incidenteDAO.listarIncidentesPendentes();
    }

    public List<Object[]> obterManutencoes() {
        return manutencaoDAO.listarTodasManutencoes();
    }

    public String criarOrdemServico(int idEmbarcacao, String tipo, String descricao, Integer horimetro, Date dataAgendada, double custo) {
        if (descricao == null || descricao.trim().isEmpty()) {
            return "A descrição do serviço é obrigatória.";
        }
        if (dataAgendada == null) {
            return "A data de agendamento é obrigatória.";
        }

        boolean ok = manutencaoDAO.salvar(idEmbarcacao, tipo, descricao, horimetro, dataAgendada, custo);
        return ok ? "OK" : "Erro ao registrar ordem de serviço no banco de dados.";
    }

    public boolean converterIncidenteEmOS(int idIncidente, int idEmbarcacao, String descricao, Date dataAgendada) {
        boolean osCriada = manutencaoDAO.salvar(idEmbarcacao, "CORRETIVA", "OS Originada do Incidente #" + idIncidente + ": " + descricao, null, dataAgendada, 0.0);
        if (osCriada) {
            incidenteDAO.atualizarStatus(idIncidente, "EM_ANALISE");
            return true;
        }
        return false;
    }

    public boolean atualizarStatusOS(int idManutencao, String status) {
        return manutencaoDAO.alterarStatus(idManutencao, status);
    }
}