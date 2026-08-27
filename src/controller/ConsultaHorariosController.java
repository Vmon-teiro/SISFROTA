package controller;

import dao.ConsultaHorariosDAO;
import dto.ConsultaHorarioDTO;

import java.util.List;
import java.util.stream.Collectors;

public class ConsultaHorariosController {

    private final ConsultaHorariosDAO dao = new ConsultaHorariosDAO();

    public List<ConsultaHorarioDTO> buscarHorarios(String embarcacao, String comandante, String destino) {
        List<ConsultaHorarioDTO> todos = dao.listarHorariosSaida();

        return todos.stream().filter(item -> {
            boolean matchEmbarcacao = (embarcacao == null || embarcacao.equals("Todos") || item.getEmbarcacao().equalsIgnoreCase(embarcacao));
            boolean matchComandante = (comandante == null || comandante.equals("Todos") || item.getComandante().equalsIgnoreCase(comandante));
            boolean matchDestino = (destino == null || destino.equals("Todos") || item.getRotaDestino().equalsIgnoreCase(destino));

            return matchEmbarcacao && matchComandante && matchDestino;
        }).collect(Collectors.toList());
    }

    public List<String> obterEmbarcacoes() {
        List<String> lista = dao.listarTodasEmbarcacoes();
        lista.add(0, "Todos");
        return lista;
    }

    public List<String> obterComandantes() {
        List<String> lista = dao.listarTodosComandantes();
        lista.add(0, "Todos");
        return lista;
    }

    public List<String> obterDestinos() {
        List<String> lista = dao.listarTodosDestinos();
        lista.add(0, "Todos");
        return lista;
    }
}