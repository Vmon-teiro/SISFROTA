package controller;

import dao.ViagemDAO;
import dao.EmbarcacaoDAO;
import dao.TripulanteDAO;
import model.Viagem;
import model.Embarcacao;
import model.Tripulante;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ViagemController {
    private ViagemDAO viagemDAO;
    private EmbarcacaoDAO embarcacaoDAO;
    private TripulanteDAO tripulanteDAO;

    public ViagemController(Connection conexao) {
        this.viagemDAO = new ViagemDAO(conexao);
        this.embarcacaoDAO = new EmbarcacaoDAO(conexao);
        this.tripulanteDAO = new TripulanteDAO(conexao);
    }

    public void registrarViagem(Viagem viagem) throws Exception {
        // RN01: Verificar se documentação da embarcação está regularizada
        Embarcacao emb = embarcacaoDAO.buscarPorId(viagem.getIdEmbarcacao());
        if (emb == null || emb.isDocumentoVencido()) {
            throw new Exception("Operação bloqueada (RN01): Documentação da embarcação está vencida ou não encontrada.");
        }

        // RN02: Verificar habilitação do tripulante/comandante
        Tripulante trip = tripulanteDAO.buscarPorId(viagem.getIdTripulante());
        if (trip == null || !trip.isHabilitado()) {
            throw new Exception("Operação bloqueada (RN02): Comandante/Tripulante não possui habilitação válida.");
        }

        viagemDAO.salvar(viagem);
    }

    public List<Viagem> listarViagens() throws SQLException {
        return viagemDAO.listarTodas();
    }
}