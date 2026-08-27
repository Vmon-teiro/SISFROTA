package view;

import dao.ViagemDAO;
import model.Viagem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TelaConsultaHorarios extends JFrame {

    private JTable tabela;
    private DefaultTableModel model;
    private final ViagemDAO viagemDAO = new ViagemDAO();

    public TelaConsultaHorarios() {
        setTitle("Consulta de Horários e Saídas de Viagens");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        model = new DefaultTableModel(new String[]{"ID", "Embarcação", "Destino", "Partida", "Status"}, 0);
        tabela = new JTable(model);

        add(new JScrollPane(tabela), BorderLayout.CENTER);
        carregarTabela();
    }

    private void carregarTabela() {
        model.setRowCount(0);
        List<Viagem> viagens = viagemDAO.listarTodas();
        for (Viagem v : viagens) {
            model.addRow(new Object[]{
                v.getId(),
                v.getNomeEmbarcacao(),
                v.getRotaDestino(),
                v.getDataHoraPartida(),
                v.getStatus()
            });
        }
    }
}
