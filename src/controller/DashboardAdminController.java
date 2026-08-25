package controller;

import dao.AbastecimentoDAO;
import dao.ManutencaoDAO;
import dto.CustoConsolidadoDTO;
import java.time.LocalDate;

public class DashboardAdminController {
    private final AbastecimentoDAO abastecimentoDAO = new AbastecimentoDAO();
    private final ManutencaoDAO manutencaoDAO = new ManutencaoDAO();

    public CustoConsolidadoDTO obterResumoFinanceiro(int embarcacaoId, String nomeEmbarcacao, LocalDate inicio, LocalDate fim) {
        double custoAbastecimento = abastecimentoDAO.getTotalAbastecimentoPorEmbarcacao(embarcacaoId, inicio, fim);
        double custoManutencao = manutencaoDAO.getTotalManutencaoPorEmbarcacao(embarcacaoId, inicio, fim);

        return new CustoConsolidadoDTO(nomeEmbarcacao, custoManutencao, custoAbastecimento);
    }
}