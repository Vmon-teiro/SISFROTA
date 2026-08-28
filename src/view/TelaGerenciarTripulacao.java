package view;

import controller.TripulanteController;
import model.Tripulante;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.util.List;

public class TelaGerenciarTripulacao extends JFrame {

    private final TripulanteController controller = new TripulanteController();
    private JTable tblTripulantes;
    private DefaultTableModel tableModel;

    private JTextField txtId, txtNome, txtCpf, txtCir;
    private JComboBox<String> cbCategoria, cbStatus;
    private JSpinner spDataVencimento;
    private JButton btnSalvar, btnLimpar, btnExcluir;

    public TelaGerenciarTripulacao() {
        setTitle("Gerenciamento de Tripulação (ADM)");
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
        panel.setBorder(BorderFactory.createTitledBorder(" Dados do Tripulante "));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtId = new JTextField(); txtId.setEditable(false);
        txtNome = new JTextField(15);
        txtCpf = new JTextField(12);
        txtCir = new JTextField(12);

        cbCategoria = new JComboBox<>(new String[]{
            "PILOTO_FLUVIAL", "CONDUTOR_FLUVIAL", "ARRAIS_AMADOR", "MESTRE_AMADOR", "CAPITAO_AMADOR"
        });
        cbStatus = new JComboBox<>(new String[]{"DISPONIVEL", "EM_VIAGEM", "INATIVO"});

        spDataVencimento = new JSpinner(new SpinnerDateModel());
        spDataVencimento.setEditor(new JSpinner.DateEditor(spDataVencimento, "dd/MM/yyyy"));

        // Linha 0
        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1; panel.add(txtId, gbc);
        gbc.gridx = 2; panel.add(new JLabel("Nome Completo:"), gbc);
        gbc.gridx = 3; panel.add(txtNome, gbc);

        // Linha 1
        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("CPF:"), gbc);
        gbc.gridx = 1; panel.add(txtCpf, gbc);
        gbc.gridx = 2; panel.add(new JLabel("Categoria Habilitação:"), gbc);
        gbc.gridx = 3; panel.add(cbCategoria, gbc);

        // Linha 2
        gbc.gridx = 0; gbc.gridy = 2; panel.add(new JLabel("Nº Registro CIR:"), gbc);
        gbc.gridx = 1; panel.add(txtCir, gbc);
        gbc.gridx = 2; panel.add(new JLabel("Vencimento CIR:"), gbc);
        gbc.gridx = 3; panel.add(spDataVencimento, gbc);

        // Linha 3
        gbc.gridx = 0; gbc.gridy = 3; panel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1; panel.add(cbStatus, gbc);

        return panel;
    }

    private JScrollPane criarTabela() {
        tableModel = new DefaultTableModel(new String[]{
            "ID", "Nome", "CPF", "Categoria", "Nº CIR", "Vencimento CIR", "Status"
        }, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tblTripulantes = new JTable(tableModel);
        tblTripulantes.getSelectionModel().addListSelectionListener(e -> carregarFormularioDaTabela());
        return new JScrollPane(tblTripulantes);
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
        List<Tripulante> lista = controller.listarTripulantes();
        for (Tripulante t : lista) {
            tableModel.addRow(new Object[]{
                t.getId(), t.getNome(), t.getCpf(), t.getCategoriaHabilitacao(),
                t.getNumeroRegistroCir(), t.getDataVencimentoCir(), t.getStatus()
            });
        }
    }

    private void carregarFormularioDaTabela() {
        int row = tblTripulantes.getSelectedRow();
        if (row != -1) {
            txtId.setText(tableModel.getValueAt(row, 0).toString());
            txtNome.setText(tableModel.getValueAt(row, 1).toString());
            txtCpf.setText(tableModel.getValueAt(row, 2).toString());
            cbCategoria.setSelectedItem(tableModel.getValueAt(row, 3).toString());
            txtCir.setText(tableModel.getValueAt(row, 4).toString());
            
            java.util.Date dataVenc = (java.util.Date) tableModel.getValueAt(row, 5);
            spDataVencimento.setValue(dataVenc);
            
            cbStatus.setSelectedItem(tableModel.getValueAt(row, 6).toString());
        }
    }

    private void salvar() {
        try {
            Integer id = txtId.getText().isEmpty() ? null : Integer.parseInt(txtId.getText());
            String nome = txtNome.getText();
            String cpf = txtCpf.getText();
            String categoria = (String) cbCategoria.getSelectedItem();
            String cir = txtCir.getText();
            java.util.Date d = (java.util.Date) spDataVencimento.getValue();
            Date dataVencimento = new Date(d.getTime());
            String status = (String) cbStatus.getSelectedItem();

            String res = controller.salvarOuAtualizar(id, nome, cpf, categoria, cir, dataVencimento, status);
            if ("OK".equals(res)) {
                JOptionPane.showMessageDialog(this, "Tripulante salvo com sucesso!");
                limparCampos();
                carregarTabela();
            } else {
                JOptionPane.showMessageDialog(this, res, "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Verifique os dados informados.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluir() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione um tripulante na tabela.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Confirma a exclusão do tripulante?", "Atenção", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int id = Integer.parseInt(txtId.getText());
            if (controller.excluirTripulante(id)) {
                JOptionPane.showMessageDialog(this, "Tripulante excluído!");
                limparCampos();
                carregarTabela();
            } else {
                JOptionPane.showMessageDialog(this, "Não foi possível excluir (o tripulante pode estar vinculado a viagens ativas).", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limparCampos() {
        txtId.setText("");
        txtNome.setText("");
        txtCpf.setText("");
        txtCir.setText("");
        cbCategoria.setSelectedIndex(0);
        cbStatus.setSelectedIndex(0);
        spDataVencimento.setValue(new java.util.Date());
        tblTripulantes.clearSelection();
    }
}
