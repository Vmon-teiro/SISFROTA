package controller;

import dao.ManutencaoDAO;
import model.Manutencao;

import java.sql.Date;
import java.util.List;

public class ManutencaoController {

    private final ManutencaoDAO dao;

    public ManutencaoController() {
        this.dao = new ManutencaoDAO();
    }

    public List<Manutencao> listarTodas() {
        return dao.listarTodas();
    }

    public boolean agendar(int idEmbarcacao, String tipo, String descricao, Integer horimetro, String dataStr, String status) {
        if (idEmbarcacao <= 0 || descricao == null || descricao.trim().isEmpty() || dataStr == null || dataStr.trim().isEmpty()) {
            return false;
        }

        try {
            Date dataAgendamento = Date.valueOf(dataStr.trim()); // Formato YYYY-MM-DD
            Manutencao m = new Manutencao(0, idEmbarcacao, null, tipo, descricao, horimetro, dataAgendamento, null, 0.0, status);
            return dao.cadastrar(m);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
