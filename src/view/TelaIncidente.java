package view;

import controller.IncidenteController;
import dao.EmbarcacaoDAO;
import model.Embarcacao;

import javax.swing.*;
import java.awt.*;

public class TelaIncidente extends JFrame {

    private JComboBox<Embarcacao> cbEmbarcacoes;
    private JComboBox<String> cbGravidade;
    private JTextArea txtDescricao;
    private JTextField txtViagemId;

    private final IncidenteController incidenteController = new IncidenteController();
    private final EmbarcacaoDAO embarcacaoDAO = new EmbarcacaoDAO();

    public TelaIncidente() {
        setTitle("Registrar Incidente Operacional (RF12)");
        setSize(500, 420);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        // Embarcação
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Embarcação:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        cbEmbarcacoes = new JComboBox<>();
        embarcacaoDAO.listarTodas().forEach(cbEmbarcacoes::addItem);
        formPanel.add(cbEmbarcacoes, gbc);

        // ID Viagem (Opcional)
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(new JLabel("Cód. Viagem (Opcional):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtViagemId = new JTextField();
        formPanel.add(txtViagemId, gbc);

        // Gravidade
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        formPanel.add(new JLabel("Gravidade:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        cbGravidade = new JComboBox<>(new String[]{"BAIXA", "MEDIA", "ALTA", "CRITICA"});
        formPanel.add(cbGravidade, gbc);

        // Descrição
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        formPanel.add(new JLabel("Descrição do Ocorrido:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtDescricao = new JTextArea(5, 20);
        txtDescricao.setLineWrap(true);
        txtDescricao.setWrapStyleWord(true);
        formPanel.add(new JScrollPane(txtDescricao), gbc);

        add(formPanel, BorderLayout.CENTER);

        JButton btnSalvar = new JButton("Registrar Incidente");
        btnSalvar.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnSalvar.addActionListener(e -> salvar());
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 15));
        btnPanel.add(btnSalvar);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void salvar() {
        Embarcacao emb = (Embarcacao) cbEmbarcacoes.getSelectedItem();
        Integer idEmbarcacao = (emb != null) ? emb.getId() : null;
        String viagemIdStr = txtViagemId.getText();
        String gravidade = (String) cbGravidade.getSelectedItem();
        String descricao = txtDescricao.getText();

        String resultado = incidenteController.registrarIncidente(idEmbarcacao, viagemIdStr, gravidade, descricao);

        if ("OK".equals(resultado)) {
            JOptionPane.showMessageDialog(this, "Incidente registrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, resultado, "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }
}