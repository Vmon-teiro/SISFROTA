package controller;

import dao.TripulanteDAO;
import model.Tripulante;

import java.sql.Date;
import java.util.List;

public class TripulanteController {

    private final TripulanteDAO dao;

    public TripulanteController() {
        this.dao = new TripulanteDAO();
    }

    public List<Tripulante> listarTodos() {
        return dao.listarTodos();
    }

    public boolean cadastrar(String nome, String cpf, String categoria, String cir, String vencimentoStr, String status) {
        if (nome == null || nome.trim().isEmpty() || cpf == null || cpf.trim().isEmpty() || vencimentoStr == null || vencimentoStr.trim().isEmpty()) {
            return false;
        }

        try {
            Date dataVencimento = Date.valueOf(vencimentoStr.trim());
            Tripulante t = new Tripulante(0, nome.trim(), cpf.trim(), categoria, cir.trim(), dataVencimento, status);
            return dao.cadastrar(t);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}