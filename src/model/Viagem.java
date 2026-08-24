package model;

import java.time.LocalDateTime;

public class Viagem {
    private int id;
    private int idEmbarcacao;
    private int idTripulante;
    private String destino;
    private LocalDateTime dataSaida;
    private LocalDateTime dataChegada;
    private String status;

    public Viagem() {}

    public Viagem(int id, int idEmbarcacao, int idTripulante, String destino, LocalDateTime dataSaida, LocalDateTime dataChegada, String status) {
        this.id = id;
        this.idEmbarcacao = idEmbarcacao;
        this.idTripulante = idTripulante;
        this.destino = destino;
        this.dataSaida = dataSaida;
        this.dataChegada = dataChegada;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdEmbarcacao() { return idEmbarcacao; }
    public void setIdEmbarcacao(int idEmbarcacao) { this.idEmbarcacao = idEmbarcacao; }
    public int getIdTripulante() { return idTripulante; }
    public void setIdTripulante(int idTripulante) { this.idTripulante = idTripulante; }
    public String getDestino() { return destino; }
    public void setDestino(String destino) { this.destino = destino; }
    public LocalDateTime getDataSaida() { return dataSaida; }
    public void setDataSaida(LocalDateTime dataSaida) { this.dataSaida = dataSaida; }
    public LocalDateTime getDataChegada() { return dataChegada; }
    public void setDataChegada(LocalDateTime dataChegada) { this.dataChegada = dataChegada; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}