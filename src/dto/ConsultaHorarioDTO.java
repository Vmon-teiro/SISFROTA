package dto;

import java.time.LocalDateTime;

public class ConsultaHorarioDTO {
    private int idViagem;
    private String embarcacao;
    private String comandante;
    private String rotaDestino;
    private LocalDateTime dataHoraPartida;
    private LocalDateTime dataHoraChegada;
    private int quantidadePassageiros;
    private String status;

    public ConsultaHorarioDTO(int idViagem, String embarcacao, String comandante, String rotaDestino, 
                              LocalDateTime dataHoraPartida, LocalDateTime dataHoraChegada, 
                              int quantidadePassageiros, String status) {
        this.idViagem = idViagem;
        this.embarcacao = embarcacao;
        this.comandante = comandante;
        this.rotaDestino = rotaDestino;
        this.dataHoraPartida = dataHoraPartida;
        this.dataHoraChegada = dataHoraChegada;
        this.quantidadePassageiros = quantidadePassageiros;
        this.status = status;
    }

    public int getIdViagem() { return idViagem; }
    public String getEmbarcacao() { return embarcacao; }
    public String getComandante() { return comandante; }
    public String getRotaDestino() { return rotaDestino; }
    public LocalDateTime getDataHoraPartida() { return dataHoraPartida; }
    public LocalDateTime getDataHoraChegada() { return dataHoraChegada; }
    public int getQuantidadePassageiros() { return quantidadePassageiros; }
    public String getStatus() { return status; }
}
