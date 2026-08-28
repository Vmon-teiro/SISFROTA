package controller;

import dao.TripulanteDAO;
import model.Tripulante;
import java.sql.Date;
import java.util.List;

public class TripulanteController {

    private final TripulanteDAO dao = new TripulanteDAO();

    public List<Tripulante> listarTripulantes() {
        return dao.listarTodos();
    }

    public String salvarOuAtualizar(Integer id, String nome, String cpf, String categoria, String cir, Date dataVencimento, String status) {
        if (nome == null || nome.trim().isEmpty()) {
            return "O nome do tripulante é obrigatório.";
        }
        if (cpf == null || cpf.trim().isEmpty()) {
            return "O CPF é obrigatório.";
        }
        if (cir == null || cir.trim().isEmpty()) {
            return "O registro CIR é obrigatório.";
        }
        if (dataVencimento == null) {
            return "A data de vencimento da CIR é obrigatória.";
        }

        Tripulante t = new Tripulante();
        t.setNome(nome.trim());
        t.setCpf(cpf.trim());
        t.setCategoriaHabilitacao(categoria);
        t.setNumeroRegistroCir(cir.trim());
        t.setDataVencimentoCir(dataVencimento);
        t.setStatus(status);

        boolean sucesso;
        if (id == null || id == 0) {
            sucesso = dao.salvar(t);
        } else {
            t.setId(id);
            sucesso = dao.atualizar(t);
        }

        return sucesso ? "OK" : "Erro ao salvar tripulante no banco de dados (CPF ou CIR podem estar duplicados).";
    }

    public boolean excluirTripulante(int id) {
        return dao.excluir(id);
    }
}