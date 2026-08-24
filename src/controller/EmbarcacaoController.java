package controller;

import dao.EmbarcacaoDAO;
import model.Embarcacao;

import java.util.List;

public class EmbarcacaoController {

    private EmbarcacaoDAO embarcacaoDAO;

    public EmbarcacaoController() {
        this.embarcacaoDAO = new EmbarcacaoDAO();
    }

    public List<Embarcacao> listarTodas() {
        return embarcacaoDAO.listarTodas();
    }

    public boolean cadastrar(Embarcacao emb) {
        return embarcacaoDAO.cadastrar(emb);
    }

    public boolean cadastrar(String nome, String modelo, int capPass, double capCarga, int ano, int horimetro, String status) {
        if (nome == null || nome.trim().isEmpty() || modelo == null || modelo.trim().isEmpty()) {
            return false;
        }
        Embarcacao emb = new Embarcacao(0, nome, modelo, capPass, capCarga, ano, horimetro, status);
        return embarcacaoDAO.cadastrar(emb);
    }

    public Embarcacao buscarPorId(int id) {
        return embarcacaoDAO.buscarPorId(id);
    }
}