package controller;

import dao.EmbarcacaoDAO;
import model.Embarcacao;
import java.util.List;

public class EmbarcacaoController {

    private final EmbarcacaoDAO dao;

    public EmbarcacaoController() {
        this.dao = new EmbarcacaoDAO();
    }

    public List<Embarcacao> listarTodas() {
        return dao.listarTodas();
    }

    public boolean cadastrar(String nome, String modelo, int capPass, double capCarga, int ano, int horimetro, String status) {
        if (nome == null || nome.trim().isEmpty() || modelo == null || modelo.trim().isEmpty()) {
            return false;
        }
        Embarcacao emb = new Embarcacao(0, nome, modelo, capPass, capCarga, ano, horimetro, status);
        return dao.cadastrar(emb);
    }
}
