package controller;

import dao.EmbarcacaoDAO;
import model.Embarcacao;
import java.util.List;

public class EmbarcacaoController {

    private final EmbarcacaoDAO dao = new EmbarcacaoDAO();

    public List<Embarcacao> listarEmbarcacoes() {
        return dao.listarTodas();
    }

    public String salvarOuAtualizar(Integer id, String nome, String modelo, int capPassageiros, double capCarga, int ano, int horimetro, String status) {
        if (nome == null || nome.trim().isEmpty()) {
            return "O nome da embarcação é obrigatório.";
        }
        if (modelo == null || modelo.trim().isEmpty()) {
            return "O modelo é obrigatório.";
        }
        if (ano < 1900 || ano > 2030) {
            return "Ano de fabricação inválido.";
        }

        Embarcacao emb = new Embarcacao();
        emb.setNome(nome.trim());
        emb.setModelo(modelo.trim());
        emb.setCapacidadePassageiros(capPassageiros);
        emb.setCapacidadeCargaTon(capCarga);
        emb.setAnoFabricacao(ano);
        emb.setHorimetroHoras(horimetro);
        emb.setStatus(status);

        boolean sucesso;
        if (id == null || id == 0) {
            sucesso = dao.salvar(emb);
        } else {
            emb.setId(id);
            sucesso = dao.atualizar(emb);
        }

        return sucesso ? "OK" : "Erro ao salvar embarcação no banco de dados.";
    }

    public boolean excluirEmbarcacao(int id) {
        return dao.excluir(id);
    }
}