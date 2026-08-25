package dto;

public class CustoConsolidadoDTO {
    private String nomeEmbarcacao;
    private double totalManutencao;
    private double totalAbastecimento;
    private double totalGeral;

    public CustoConsolidadoDTO(String nomeEmbarcacao, double totalManutencao, double totalAbastecimento) {
        this.nomeEmbarcacao = nomeEmbarcacao;
        this.totalManutencao = totalManutencao;
        this.totalAbastecimento = totalAbastecimento;
        this.totalGeral = totalManutencao + totalAbastecimento;
    }

    public String getNomeEmbarcacao() { return nomeEmbarcacao; }
    public double getTotalManutencao() { return totalManutencao; }
    public double getTotalAbastecimento() { return totalAbastecimento; }
    public double getTotalGeral() { return totalGeral; }
}
