package controller;

import dao.EmbarcacaoDAO;
import dao.TripulanteDAO;
import dao.ViagemDAO;
import model.Embarcacao;
import model.Tripulante;
import model.Viagem;

import java.time.LocalDate;
import java.util.List;

public class ViagemController {

    private final ViagemDAO viagemDAO;
    private final EmbarcacaoDAO embarcacaoDAO;
    private final TripulanteDAO tripulanteDAO;

    public ViagemController() {
        this.viagemDAO = new ViagemDAO();
        this.embarcacaoDAO = new EmbarcacaoDAO();
        this.tripulanteDAO = new TripulanteDAO();
    }

    public void registrarViagem(Viagem viagem) throws Exception {
        // Validação da Embarcação (RN01)
        Embarcacao emb = embarcacaoDAO.buscarPorId(viagem.getIdEmbarcacao());
        if (emb == null) {
            throw new Exception("Erro: Embarcação com ID " + viagem.getIdEmbarcacao() + " não encontrada.");
        }
        if (!"ATIVA".equalsIgnoreCase(emb.getStatus())) {
            throw new Exception("Erro (RN01): A embarcação '" + emb.getNome() + "' não está ativa (Status atual: " + emb.getStatus() + ").");
        }

        // Validação do Tripulante (RN02)
        Tripulante trip = tripulanteDAO.buscarPorId(viagem.getIdTripulante());
        if (trip == null) {
            throw new Exception("Erro: Tripulante com ID " + viagem.getIdTripulante() + " não encontrado.");
        }
        if (!"DISPONIVEL".equalsIgnoreCase(trip.getStatus())) {
            throw new Exception("Erro (RN02): O tripulante '" + trip.getNome() + "' não está disponível (Status atual: " + trip.getStatus() + ").");
        }

        // Validação do Vencimento da CIR
        if (trip.getDataVencimentoCir() != null) {
            LocalDate dataVencimento = trip.getDataVencimentoCir().toLocalDate();
            LocalDate dataAtual = LocalDate.now();
            if (dataVencimento.isBefore(dataAtual)) {
                throw new Exception("Erro (RN02): A CIR do tripulante '" + trip.getNome() + "' está vencida desde " + dataVencimento + ".");
            }
        }

        // Persistência no Banco
        boolean sucesso = viagemDAO.salvar(viagem);
        if (!sucesso) {
            throw new Exception("Erro ao persistir a viagem no banco de dados.");
        }
    }

    public List<Viagem> listarViagens() {
        return viagemDAO.listarTodas();
    }
}