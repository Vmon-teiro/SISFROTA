package model;

import java.time.LocalDate;

public class Abastecimento {
    private int id;
    private int embarcacaoId;
    private LocalDate data;
    private double litros;
    private double valorTotal;
    private String fornecedor;

    public Abastecimento() {}

    public Abastecimento(int embarcacaoId, LocalDate data, double litros, double valorTotal, String fornecedor) {
        this.embarcacaoId = embarcacaoId;
        this.data = data;
        this.litros = litros;
        this.valorTotal = valorTotal;
        this.fornecedor = fornecedor;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getEmbarcacaoId() { return embarcacaoId; }
    public void setEmbarcacaoId(int embarcacaoId) { this.embarcacaoId = embarcacaoId; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public double getLitros() { return litros; }
    public void setLitros(double litros) { this.litros = litros; }

    public double getValorTotal() { return valorTotal; }
    public void setValorTotal(double valorTotal) { this.valorTotal = valorTotal; }

    public String getFornecedor() { return fornecedor; }
    public void setFornecedor(String fornecedor) { this.fornecedor = fornecedor; }
}