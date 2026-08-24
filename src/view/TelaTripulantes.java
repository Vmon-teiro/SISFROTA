package view;

import controller.TripulanteController;
import model.Tripulante;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TelaTripulantes extends JFrame {

    private final TripulanteController controller;
    private JTable tabela;
    private DefaultTableModel tableModel;

    private JTextField txtNome, txtCpf, txtNumeroCir, txtVencimentoCir;
    private JComboBox<String> cbCategoria, cbStatus;

    public TelaTripulantes() {
        controller = new TripulanteController();

        setTitle("Gestão Náutica - Cadastro de Tripulação e CIR");
        setSize(850, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponentes();
        carregarTabela();
    }

    private void initComponentes() {
        setLayout(new BorderLayout());

        JPanel panelForm = new JPanel(new GridLayout(4, 3, 10, 10));
        panelForm.setBorder(BorderFactory.createTitledBorder(" Novo Tripulante "));

        panelForm.add(new JLabel("Nome Completo:"));
        txtNome = new JTextField();
        panelForm.add(txtNome);

        panelForm.add(new JLabel("CPF:"));
        txtCpf = new JTextField();
        panelForm.add(txtCpf);

        panelForm.add(new JLabel("Categoria Habilitação:"));
        cbCategoria = new JComboBox<>(new String[]{"PILOTO_FLUVIAL", "CONDUTOR_FLUVIAL", "ARRAIS_AMADOR", "MESTRE_AMADOR", "CAPITAO_AMADOR"});
        panelForm.add(cbCategoria);

        panelForm.add(new JLabel("Nº Registro CIR:"));
        txtNumeroCir = new JTextField();
        panelForm.add(txtNumeroCir);

        panelForm.add(new JLabel("Vencimento CIR (AAAA-MM-DD):"));
        txtVencimentoCir = new JTextField("2027-12-31");
        panelForm.add(txtVencimentoCir);

        panelForm.add(new JLabel("Status:"));
        cbStatus = new JComboBox<>(new String[]{"DISPONIVEL", "EM_VIAGEM", "INATIVO"});
        panelForm.add(cbStatus);

        JButton btnSalvar = new JButton("Cadastrar Tripulante");
        btnSalvar.addActionListener(e -> salvarTripulante());
        panelForm.add(btnSalvar);

        add(panelForm, BorderLayout.NORTH);

        String[] colunas = {"ID", "Nome", "CPF", "Categoria", "Nº CIR", "Vencimento CIR", "Status"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tabela = new JTable(tableModel);
        add(new JScrollPane(tabela), BorderLayout.CENTER);
    }

    private void carregarTabela() {
        tableModel.setRowCount(0);
        List<Tripulante> lista = controller.listarTodos();

        for (Tripulante t : lista) {
            tableModel.addRow(new Object[]{
                t.getId(),
                t.getNome(),
                t.getCpf(),
                t.getCategoriaHabilitacao(),
                t.getNumeroRegistroCir(),
                t.getDataVencimentoCir(),
                t.getStatus()
            });
        }
    }

    private void salvarTripulante() {
        String nome = txtNome.getText();
        String cpf = txtCpf.getText();
        String categoria = (String) cbCategoria.getSelectedItem();
        String cir = txtNumeroCir.getText();
        String vencimento = txtVencimentoCir.getText();
        String status = (String) cbStatus.getSelectedItem();

        if (controller.cadastrar(nome, cpf, categoria, cir, vencimento, status)) {
            JOptionPane.showMessageDialog(this, "Tripulante cadastrado com sucesso!");
            limparCampos();
            carregarTabela();
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao cadastrar. Verifique se os campos estão preenchidos e se a data está no formato AAAA-MM-DD.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparCampos() {
        txtNome.setText("");
        txtCpf.setText("");
        txtNumeroCir.setText("");
        txtVencimentoCir.setText("2027-12-31");
        cbCategoria.setSelectedIndex(0);
        cbStatus.setSelectedIndex(0);
    }
}