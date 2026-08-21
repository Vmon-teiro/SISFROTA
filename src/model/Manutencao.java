package model;

import java.sql.Date;

public class Manutencao {

    private int id;
    private int idEmbarcacao;
    private String nomeEmbarcacao; // Exibição na tabela
    private String tipoManutencao; // PREVENTIVA, CORRETIVA
    private String descricaoServico;
    private Integer horimetroAgendado;
    private Date dataAgendamento;
    private Date dataExecucao;
    private double custoTotal;
    private String status; // AGENDADA, EM_ANDAMENTO, CONCLUIDA, CANCELADA

    public Manutencao() {}

    public Manutencao(int id, int idEmbarcacao, String nomeEmbarcacao, String tipoManutencao,
                      String descricaoServico, Integer horimetroAgendado, Date dataAgendamento,
                      Date dataExecucao, double custoTotal, String status) {
        this.id = id;
        this.idEmbarcacao = idEmbarcacao;
        this.nomeEmbarcacao = nomeEmbarcacao;
        this.tipoManutencao = tipoManutencao;
        this.descricaoServico = descricaoServico;
        this.horimetroAgendado = horimetroAgendado;
        this.dataAgendamento = dataAgendamento;
        this.dataExecucao = dataExecucao;
        this.custoTotal = custoTotal;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdEmbarcacao() { return idEmbarcacao; }
    public void setIdEmbarcacao(int idEmbarcacao) { this.idEmbarcacao = idEmbarcacao; }

    public String getNomeEmbarcacao() { return nomeEmbarcacao; }
    public void setNomeEmbarcacao(String nomeEmbarcacao) { this.nomeEmbarcacao = nomeEmbarcacao; }

    public String getTipoManutencao() { return tipoManutencao; }
    public void setTipoManutencao(String tipoManutencao) { this.tipoManutencao = tipoManutencao; }

    public String getDescricaoServico() { return descricaoServico; }
    public void setDescricaoServico(String descricaoServico) { this.descricaoServico = descricaoServico; }

    public Integer getHorimetroAgendado() { return horimetroAgendado; }
    public void setHorimetroAgendado(Integer horimetroAgendado) { this.horimetroAgendado = horimetroAgendado; }

    public Date getDataAgendamento() { return dataAgendamento; }
    public void setDataAgendamento(Date dataAgendamento) { this.dataAgendamento = dataAgendamento; }

    public Date getDataExecucao() { return dataExecucao; }
    public void setDataExecucao(Date dataExecucao) { this.dataExecucao = dataExecucao; }

    public double getCustoTotal() { return custoTotal; }
    public void setCustoTotal(double custoTotal) { this.custoTotal = custoTotal; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
