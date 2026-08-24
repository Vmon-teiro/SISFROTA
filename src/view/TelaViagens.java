package view;

import controller.ViagemController;
import model.Viagem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TelaViagens extends JFrame {
    private ViagemController controller;
    private JTextField txtEmbarcacaoId, txtTripulanteId, txtDestino, txtDataSaida;
    private JTable tabelaViagens;
    private DefaultTableModel tableModel;

    public TelaViagens() {
        this.controller = new ViagemController();
        setTitle("Gestão Náutica - Registro de Viagens");
        setSize(750, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panelForm = new JPanel(new GridLayout(5, 2, 5, 5));
        panelForm.setBorder(BorderFactory.createTitledBorder("Nova Viagem"));

        panelForm.add(new JLabel("ID Embarcação:"));
        txtEmbarcacaoId = new JTextField();
        panelForm.add(txtEmbarcacaoId);

        panelForm.add(new JLabel("ID Comandante:"));
        txtTripulanteId = new JTextField();
        panelForm.add(txtTripulanteId);

        panelForm.add(new JLabel("Destino:"));
        txtDestino = new JTextField();
        panelForm.add(txtDestino);

        panelForm.add(new JLabel("Data/Hora Saída (yyyy-MM-dd HH:mm):"));
        txtDataSaida = new JTextField();
        panelForm.add(txtDataSaida);

        JButton btnSalvar = new JButton("Registrar Viagem");
        panelForm.add(btnSalvar);

        add(panelForm, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"ID", "Embarcação", "Tripulante", "Destino", "Saída", "Status"}, 0);
        tabelaViagens = new JTable(tableModel);
        add(new JScrollPane(tabelaViagens), BorderLayout.CENTER);

        btnSalvar.addActionListener(e -> salvarViagem());
        carregarViagens();
    }

    private void salvarViagem() {
        try {
            Viagem v = new Viagem();
            v.setIdEmbarcacao(Integer.parseInt(txtEmbarcacaoId.getText()));
            v.setIdTripulante(Integer.parseInt(txtTripulanteId.getText()));
            v.setDestino(txtDestino.getText());
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            v.setDataSaida(LocalDateTime.parse(txtDataSaida.getText(), formatter));
            v.setStatus("Agendada");

            controller.registrarViagem(v);
            JOptionPane.showMessageDialog(this, "Viagem registrada com sucesso!");
            carregarViagens();
            limparCampos();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro de Validação", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void carregarViagens() {
        try {
            tableModel.setRowCount(0);
            List<Viagem> lista = controller.listarViagens();
            for (Viagem v : lista) {
                tableModel.addRow(new Object[]{v.getId(), v.getIdEmbarcacao(), v.getIdTripulante(), v.getDestino(), v.getDataSaida(), v.getStatus()});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar lista: " + ex.getMessage());
        }
    }

    private void limparCampos() {
        txtEmbarcacaoId.setText("");
        txtTripulanteId.setText("");
        txtDestino.setText("");
        txtDataSaida.setText("");
    }
}