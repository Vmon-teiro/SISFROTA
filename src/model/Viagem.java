package model;

import java.time.LocalDateTime;

public class Viagem {
    private int id;
    private int idEmbarcacao;
    private String nomeEmbarcacao;
    private int idComandante;
    private String nomeComandante;
    private String rotaDestino;
    private LocalDateTime dataHoraPartida;
    private LocalDateTime dataHoraChegada;
    private int quantidadePassageiros;
    private String status;

    public Viagem() {}

    public Viagem(int idEmbarcacao, int idComandante, String rotaDestino, 
                  LocalDateTime dataHoraPartida, int quantidadePassageiros) {
        this.idEmbarcacao = idEmbarcacao;
        this.idComandante = idComandante;
        this.rotaDestino = rotaDestino;
        this.dataHoraPartida = dataHoraPartida;
        this.quantidadePassageiros = quantidadePassageiros;
        this.status = "EM_ANDAMENTO";
    }

    public Viagem(int id, int idEmbarcacao, String nomeEmbarcacao, int idComandante, 
                  String nomeComandante, String rotaDestino, LocalDateTime dataHoraPartida, 
                  LocalDateTime dataHoraChegada, int quantidadePassageiros, String status) {
        this.id = id;
        this.idEmbarcacao = idEmbarcacao;
        this.nomeEmbarcacao = nomeEmbarcacao;
        this.idComandante = idComandante;
        this.nomeComandante = nomeComandante;
        this.rotaDestino = rotaDestino;
        this.dataHoraPartida = dataHoraPartida;
        this.dataHoraChegada = dataHoraChegada;
        this.quantidadePassageiros = quantidadePassageiros;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdEmbarcacao() { return idEmbarcacao; }
    public String getNomeEmbarcacao() { return nomeEmbarcacao; }
    public int getIdComandante() { return idComandante; }
    public String getNomeComandante() { return nomeComandante; }
    public String getRotaDestino() { return rotaDestino; }
    public LocalDateTime getDataHoraPartida() { return dataHoraPartida; }
    public LocalDateTime getDataHoraChegada() { return dataHoraChegada; }
    public int getQuantidadePassageiros() { return quantidadePassageiros; }
    public String getStatus() { return status; }
}