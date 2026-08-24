package controller;

import dao.EmbarcacaoDAO;
import dao.ManutencaoDAO;
import model.Embarcacao;
import model.Manutencao;

import java.sql.Date;
import java.util.List;

public class ManutencaoController {
    private ManutencaoDAO manutencaoDAO;
    private EmbarcacaoDAO embarcacaoDAO;

    public ManutencaoController() {
        this.manutencaoDAO = new ManutencaoDAO();
        this.embarcacaoDAO = new EmbarcacaoDAO();
    }

    public boolean agendar(int idEmbarcacao, String tipo, String descricao, Integer horimetro, String dataStr, String status) {
        try {
            if (descricao == null || descricao.trim().isEmpty() || dataStr == null || dataStr.trim().isEmpty()) {
                return false;
            }

            // Utiliza o embarcacaoDAO para validar se a embarcação existe
            Embarcacao emb = embarcacaoDAO.buscarPorId(idEmbarcacao);
            if (emb == null) {
                System.err.println("Embarcação não encontrada.");
                return false;
            }

            Date dataAgendamento = Date.valueOf(dataStr.trim());

            Manutencao m = new Manutencao();
            m.setIdEmbarcacao(idEmbarcacao);
            m.setTipoManutencao(tipo);
            m.setDescricaoServico(descricao);
            m.setHorimetroAgendado(horimetro);
            m.setDataAgendamento(dataAgendamento);
            m.setStatus(status);

            return manutencaoDAO.cadastrar(m);
        } catch (Exception e) {
            System.err.println("Erro no agendamento: " + e.getMessage());
            return false;
        }
    }

    public List<Manutencao> listarTodas() {
        return manutencaoDAO.listarTodas();
    }

    public boolean concluirManutencao(int id, Date dataExecucao, double custoTotal) {
        if (custoTotal < 0) return false;
        return manutencaoDAO.concluirManutencao(id, dataExecucao, custoTotal);
    }
}