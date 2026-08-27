package controller;

import dao.AbastecimentoDAO;
import model.Abastecimento;

public class AbastecimentoController {

    private final AbastecimentoDAO abastecimentoDAO = new AbastecimentoDAO();

    public String registrar(Abastecimento abastecimento) {
        if (abastecimento == null) {
            return "Dados de abastecimento inválidos.";
        }
        if (abastecimento.getEmbarcacaoId() <= 0) {
            return "Selecione uma embarcação válida.";
        }
        if (abastecimento.getLitros() <= 0) {
            return "A quantidade de litros deve ser maior que zero.";
        }
        if (abastecimento.getValorTotal() <= 0) {
            return "O valor total deve ser maior que zero.";
        }
        if (abastecimento.getFornecedor() == null || abastecimento.getFornecedor().trim().isEmpty()) {
            return "Selecione um posto / fornecedor válido.";
        }

        boolean sucesso = abastecimentoDAO.salvar(abastecimento);
        return sucesso ? "OK" : "Erro ao registrar o abastecimento no banco de dados.";
    }
}