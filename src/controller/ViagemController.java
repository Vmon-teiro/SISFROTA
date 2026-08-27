package controller;

import dao.ViagemDAO;
import model.Embarcacao;
import model.Viagem;

public class ViagemController {

    private final ViagemDAO viagemDAO = new ViagemDAO();

    public String registrarViagem(Viagem viagem, Embarcacao embarcacao) {
        // Regra RN01: Validação da Embarcação
        if ("EM_MANUTENCAO".equalsIgnoreCase(embarcacao.getStatus()) || "INATIVA".equalsIgnoreCase(embarcacao.getStatus())) {
            return "A embarcação selecionada está em manutenção ou inativa.";
        }

        if (viagemDAO.embarcacaoEmViagem(viagem.getIdEmbarcacao())) {
            return "A embarcação selecionada já possui uma viagem em andamento.";
        }

        // Regra RN02: Validação da Tripulação / Comandante
        if (viagemDAO.comandanteEmViagem(viagem.getIdComandante())) {
            return "O comandante selecionado já está escalado em outra viagem em andamento.";
        }

        // Validação de Capacidade
        if (viagem.getQuantidadePassageiros() > embarcacao.getCapacidadePassageiros()) {
            return "A quantidade de passageiros (" + viagem.getQuantidadePassageiros() + 
                   ") excede a capacidade máxima (" + embarcacao.getCapacidadePassageiros() + ").";
        }

        // Persistência
        boolean sucesso = viagemDAO.salvar(viagem);
        return sucesso ? "OK" : "Erro ao salvar a viagem no banco de dados.";
    }
}