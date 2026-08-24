package model;

import java.sql.Date;

public class Tripulante {

    private int id;
    private String nome;
    private String cpf;
    private String categoriaHabilitacao;
    private String numeroRegistroCir;
    private Date dataVencimentoCir;
    private String status;

    public Tripulante() {}

    public Tripulante(int id, String nome, String cpf, String categoriaHabilitacao, String numeroRegistroCir, Date dataVencimentoCir, String status) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.categoriaHabilitacao = categoriaHabilitacao;
        this.numeroRegistroCir = numeroRegistroCir;
        this.dataVencimentoCir = dataVencimentoCir;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getCategoriaHabilitacao() { return categoriaHabilitacao; }
    public void setCategoriaHabilitacao(String categoriaHabilitacao) { this.categoriaHabilitacao = categoriaHabilitacao; }

    public String getNumeroRegistroCir() { return numeroRegistroCir; }
    public void setNumeroRegistroCir(String numeroRegistroCir) { this.numeroRegistroCir = numeroRegistroCir; }

    public Date getDataVencimentoCir() { return dataVencimentoCir; }
    public void setDataVencimentoCir(Date dataVencimentoCir) { this.dataVencimentoCir = dataVencimentoCir; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}