package controller;

import dao.IncidenteDAO;
import model.Incidente;
import java.time.LocalDateTime;

public class IncidenteController {

    private final IncidenteDAO incidenteDAO = new IncidenteDAO();

    public String registrarIncidente(Integer idEmbarcacao, String viagemIdStr, String gravidade, String descricao) {
        if (idEmbarcacao == null || idEmbarcacao <= 0) {
            return "Selecione uma embarcação válida.";
        }
        if (descricao == null || descricao.trim().isEmpty()) {
            return "A descrição do incidente não pode ficar em branco.";
        }

        Integer idViagem = null;
        if (viagemIdStr != null && !viagemIdStr.trim().isEmpty()) {
            try {
                idViagem = Integer.parseInt(viagemIdStr.trim());
            } catch (NumberFormatException e) {
                return "O código da viagem deve ser um número inteiro válido.";
            }
        }

        Incidente incidente = new Incidente(
            idEmbarcacao,
            idViagem,
            LocalDateTime.now(),
            descricao.trim(),
            gravidade
        );

        boolean sucesso = incidenteDAO.salvar(incidente);
        return sucesso ? "OK" : "Erro ao salvar o incidente no banco de dados.";
    }
}
