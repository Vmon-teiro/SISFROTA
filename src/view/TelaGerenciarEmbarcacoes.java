package view;

import controller.EmbarcacaoController;
import model.Embarcacao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TelaGerenciarEmbarcacoes extends JFrame {

    private final EmbarcacaoController controller = new EmbarcacaoController();
    private JTable tblEmbarcacoes;
    private DefaultTableModel tableModel;

    private JTextField txtId, txtNome, txtModelo, txtCapPassageiros, txtCapCarga, txtAno, txtHorimetro;
    private JComboBox<String> cbStatus;
    private JButton btnSalvar, btnLimpar, btnExcluir;

    public TelaGerenciarEmbarcacoes() {
        setTitle("Gerenciamento de Embarcações e Frota (ADM)");
        setSize(950, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        add(criarFormulario(), BorderLayout.NORTH);
        add(criarTabela(), BorderLayout.CENTER);
        add(criarBarraBotoes(), BorderLayout.SOUTH);

        carregarTabela();
    }

    private JPanel criarFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(" Dados da Embarcação "));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtId = new JTextField(); txtId.setEditable(false);
        txtNome = new JTextField(15);
        txtModelo = new JTextField(15);
        txtCapPassageiros = new JTextField("0");
        txtCapCarga = new JTextField("0.00");
        txtAno = new JTextField("2024");
        txtHorimetro = new JTextField("0");
        cbStatus = new JComboBox<>(new String[]{"ATIVA", "EM_MANUTENCAO", "INATIVA"});

        // Linha 0
        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1; panel.add(txtId, gbc);
        gbc.gridx = 2; panel.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 3; panel.add(txtNome, gbc);

        // Linha 1
        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("Modelo:"), gbc);
        gbc.gridx = 1; panel.add(txtModelo, gbc);
        gbc.gridx = 2; panel.add(new JLabel("Cap. Passageiros:"), gbc);
        gbc.gridx = 3; panel.add(txtCapPassageiros, gbc);

        // Linha 2
        gbc.gridx = 0; gbc.gridy = 2; panel.add(new JLabel("Cap. Carga (Ton):"), gbc);
        gbc.gridx = 1; panel.add(txtCapCarga, gbc);
        gbc.gridx = 2; panel.add(new JLabel("Ano Fabricação:"), gbc);
        gbc.gridx = 3; panel.add(txtAno, gbc);

        // Linha 3
        gbc.gridx = 0; gbc.gridy = 3; panel.add(new JLabel("Horímetro (Horas):"), gbc);
        gbc.gridx = 1; panel.add(txtHorimetro, gbc);
        gbc.gridx = 2; panel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 3; panel.add(cbStatus, gbc);

        return panel;
    }

    private JScrollPane criarTabela() {
        tableModel = new DefaultTableModel(new String[]{
            "ID", "Nome", "Modelo", "Passageiros", "Carga (Ton)", "Ano", "Horímetro (h)", "Status"
        }, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tblEmbarcacoes = new JTable(tableModel);
        tblEmbarcacoes.getSelectionModel().addListSelectionListener(e -> carregarFormularioDaTabela());
        return new JScrollPane(tblEmbarcacoes);
    }

    private JPanel criarBarraBotoes() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        btnSalvar = new JButton("Salvar");
        btnLimpar = new JButton("Limpar Campos");
        btnExcluir = new JButton("Excluir");

        btnSalvar.setBackground(new Color(46, 204, 113));
        btnSalvar.setForeground(Color.WHITE);
        btnExcluir.setBackground(new Color(231, 76, 60));
        btnExcluir.setForeground(Color.WHITE);

        btnSalvar.addActionListener(e -> salvar());
        btnLimpar.addActionListener(e -> limparCampos());
        btnExcluir.addActionListener(e -> excluir());

        panel.add(btnLimpar);
        panel.add(btnExcluir);
        panel.add(btnSalvar);

        return panel;
    }

    private void carregarTabela() {
        tableModel.setRowCount(0);
        List<Embarcacao> lista = controller.listarEmbarcacoes();
        for (Embarcacao e : lista) {
            tableModel.addRow(new Object[]{
                e.getId(), e.getNome(), e.getModelo(), e.getCapacidadePassageiros(),
                e.getCapacidadeCargaTon(), e.getAnoFabricacao(), e.getHorimetroHoras(), e.getStatus()
            });
        }
    }

    private void carregarFormularioDaTabela() {
        int row = tblEmbarcacoes.getSelectedRow();
        if (row != -1) {
            txtId.setText(tableModel.getValueAt(row, 0).toString());
            txtNome.setText(tableModel.getValueAt(row, 1).toString());
            txtModelo.setText(tableModel.getValueAt(row, 2).toString());
            txtCapPassageiros.setText(tableModel.getValueAt(row, 3).toString());
            txtCapCarga.setText(tableModel.getValueAt(row, 4).toString());
            txtAno.setText(tableModel.getValueAt(row, 5).toString());
            txtHorimetro.setText(tableModel.getValueAt(row, 6).toString());
            cbStatus.setSelectedItem(tableModel.getValueAt(row, 7).toString());
        }
    }

    private void salvar() {
        try {
            Integer id = txtId.getText().isEmpty() ? null : Integer.parseInt(txtId.getText());
            String nome = txtNome.getText();
            String modelo = txtModelo.getText();
            int capPass = Integer.parseInt(txtCapPassageiros.getText().trim());
            double capCarga = Double.parseDouble(txtCapCarga.getText().replace(",", "."));
            int ano = Integer.parseInt(txtAno.getText().trim());
            int horimetro = Integer.parseInt(txtHorimetro.getText().trim());
            String status = (String) cbStatus.getSelectedItem();

            String res = controller.salvarOuAtualizar(id, nome, modelo, capPass, capCarga, ano, horimetro, status);
            if ("OK".equals(res)) {
                JOptionPane.showMessageDialog(this, "Embarcação salva com sucesso!");
                limparCampos();
                carregarTabela();
            } else {
                JOptionPane.showMessageDialog(this, res, "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Verifique os valores numéricos digitados.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluir() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione uma embarcação na tabela.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Confirma a exclusão da embarcação?", "Atenção", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int id = Integer.parseInt(txtId.getText());
            if (controller.excluirEmbarcacao(id)) {
                JOptionPane.showMessageDialog(this, "Embarcação excluída!");
                limparCampos();
                carregarTabela();
            } else {
                JOptionPane.showMessageDialog(this, "Não foi possível excluir (pode haver vínculos ativos no banco).", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limparCampos() {
        txtId.setText("");
        txtNome.setText("");
        txtModelo.setText("");
        txtCapPassageiros.setText("0");
        txtCapCarga.setText("0.00");
        txtAno.setText("2024");
        txtHorimetro.setText("0");
        cbStatus.setSelectedIndex(0);
        tblEmbarcacoes.clearSelection();
    }
}
