package controller;

import dao.RelatorioDAO;
import java.util.List;

public class RelatorioController {

    private final RelatorioDAO dao = new RelatorioDAO();

    public static class CustoEmbarcacaoDTO {
        private final int id;
        private final String nomeEmbarcacao;
        private final double totalManutencao;
        private final double totalAbastecimento;
        private final double custoTotal;

        public CustoEmbarcacaoDTO(int id, String nomeEmbarcacao, double totalManutencao, double totalAbastecimento, double custoTotal) {
            this.id = id;
            this.nomeEmbarcacao = nomeEmbarcacao;
            this.totalManutencao = totalManutencao;
            this.totalAbastecimento = totalAbastecimento;
            this.custoTotal = custoTotal;
        }

        public int getId() { return id; }
        public String getNomeEmbarcacao() { return nomeEmbarcacao; }
        public double getTotalManutencao() { return totalManutencao; }
        public double getTotalAbastecimento() { return totalAbastecimento; }
        public double getCustoTotal() { return custoTotal; }
    }

    public List<CustoEmbarcacaoDTO> obterConsolidadoCustos() {
        return dao.obterResumoCustosPorEmbarcacao();
    }
}