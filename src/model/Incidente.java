package model;

import java.time.LocalDateTime;

public class Incidente {
    private int id;
    private int idEmbarcacao;
    private Integer idViagem;
    private LocalDateTime dataIncidente;
    private String descricao;
    private String gravidade;
    private String status;

    public Incidente() {}

    public Incidente(int idEmbarcacao, Integer idViagem, LocalDateTime dataIncidente, String descricao, String gravidade) {
        this.idEmbarcacao = idEmbarcacao;
        this.idViagem = idViagem;
        this.dataIncidente = dataIncidente;
        this.descricao = descricao;
        this.gravidade = gravidade;
        this.status = "PENDENTE";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdEmbarcacao() { return idEmbarcacao; }
    public void setIdEmbarcacao(int idEmbarcacao) { this.idEmbarcacao = idEmbarcacao; }

    public Integer getIdViagem() { return idViagem; }
    public void setIdViagem(Integer idViagem) { this.idViagem = idViagem; }

    public LocalDateTime getDataIncidente() { return dataIncidente; }
    public void setDataIncidente(LocalDateTime dataIncidente) { this.dataIncidente = dataIncidente; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getGravidade() { return gravidade; }
    public void setGravidade(String gravidade) { this.gravidade = gravidade; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}