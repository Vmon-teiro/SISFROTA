package view;

import dao.EmbarcacaoDAO;
import dao.IncidenteDAO;
import model.Embarcacao;
import model.Incidente;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;

public class TelaIncidente extends JFrame {

    private JComboBox<Embarcacao> cbEmbarcacoes;
    private JComboBox<String> cbGravidade;
    private JTextArea txtDescricao;
    private JTextField txtViagemId;

    private final IncidenteDAO incidenteDAO = new IncidenteDAO();
    private final EmbarcacaoDAO embarcacaoDAO = new EmbarcacaoDAO();

    public TelaIncidente() {
        setTitle("Registrar Incidente Operacional (RF12)");
        setSize(480, 380);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Embarcação
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Embarcação:"), gbc);
        gbc.gridx = 1;
        cbEmbarcacoes = new JComboBox<>();
        embarcacaoDAO.listarTodas().forEach(cbEmbarcacoes::addItem);
        formPanel.add(cbEmbarcacoes, gbc);

        // ID Viagem (Opcional)
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Cód. Viagem (Opcional):"), gbc);
        gbc.gridx = 1;
        txtViagemId = new JTextField();
        formPanel.add(txtViagemId, gbc);

        // Gravidade
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Gravidade:"), gbc);
        gbc.gridx = 1;
        cbGravidade = new JComboBox<>(new String[]{"BAIXA", "MEDIA", "ALTA", "CRITICA"});
        formPanel.add(cbGravidade, gbc);

        // Descrição
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Descrição do Ocorrido:"), gbc);
        gbc.gridx = 1;
        txtDescricao = new JTextArea(4, 20);
        txtDescricao.setLineWrap(true);
        formPanel.add(new JScrollPane(txtDescricao), gbc);

        add(formPanel, BorderLayout.CENTER);

        JButton btnSalvar = new JButton("Registrar Incidente");
        btnSalvar.addActionListener(e -> salvar());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(btnSalvar);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void salvar() {
        try {
            Embarcacao emb = (Embarcacao) cbEmbarcacoes.getSelectedItem();
            if (emb == null || txtDescricao.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha a embarcação e a descrição.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Integer viagemId = null;
            if (!txtViagemId.getText().trim().isEmpty()) {
                viagemId = Integer.parseInt(txtViagemId.getText().trim());
            }

            Incidente inc = new Incidente(
                emb.getId(),
                viagemId,
                LocalDateTime.now(),
                txtDescricao.getText().trim(),
                (String) cbGravidade.getSelectedItem()
            );

            if (incidenteDAO.salvar(inc)) {
                JOptionPane.showMessageDialog(this, "Incidente registrado com sucesso!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao salvar o incidente no banco.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID da viagem deve ser um número inteiro válido.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
