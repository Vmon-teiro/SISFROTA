package model;

public class Embarcacao {

    private int id;
    private String nome;
    private String modelo;
    private int capacidadePassageiros;
    private double capacidadeCargaTon;
    private int anoFabricacao;
    private int horimetroHoras;
    private String status; // ATIVA, EM_MANUTENCAO, INATIVA

    public Embarcacao() {}

    public Embarcacao(int id, String nome, String modelo, int capacidadePassageiros, 
                      double capacidadeCargaTon, int anoFabricacao, int horimetroHoras, String status) {
        this.id = id;
        this.nome = nome;
        this.modelo = modelo;
        this.capacidadePassageiros = capacidadePassageiros;
        this.capacidadeCargaTon = capacidadeCargaTon;
        this.anoFabricacao = anoFabricacao;
        this.horimetroHoras = horimetroHoras;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public int getCapacidadePassageiros() { return capacidadePassageiros; }
    public void setCapacidadePassageiros(int capacidadePassageiros) { this.capacidadePassageiros = capacidadePassageiros; }

    public double getCapacidadeCargaTon() { return capacidadeCargaTon; }
    public void setCapacidadeCargaTon(double capacidadeCargaTon) { this.capacidadeCargaTon = capacidadeCargaTon; }

    public int getAnoFabricacao() { return anoFabricacao; }
    public void setAnoFabricacao(int anoFabricacao) { this.anoFabricacao = anoFabricacao; }

    public int getHorimetroHoras() { return horimetroHoras; }
    public void setHorimetroHoras(int horimetroHoras) { this.horimetroHoras = horimetroHoras; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
