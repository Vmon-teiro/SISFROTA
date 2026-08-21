package view;

import controller.EmbarcacaoController;
import model.Embarcacao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TelaEmbarcacoes extends JFrame {

    private final EmbarcacaoController controller;
    private JTable tabela;
    private DefaultTableModel tableModel;

    private JTextField txtNome, txtModelo, txtCapPass, txtCapCarga, txtAno, txtHorimetro;
    private JComboBox<String> cbStatus;

    public TelaEmbarcacoes() {
        controller = new EmbarcacaoController();

        setTitle("Gestão Náutica - Cadastro e Consulta de Embarcações");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponentes();
        carregarTabela();
    }

    private void initComponentes() {
        setLayout(new BorderLayout());

        // Formulário de Cadastro (Norte)
        JPanel panelForm = new JPanel(new GridLayout(4, 4, 10, 10));
        panelForm.setBorder(BorderFactory.createTitledBorder(" Nova Embarcação "));

        panelForm.add(new JLabel("Nome:"));
        txtNome = new JTextField();
        panelForm.add(txtNome);

        panelForm.add(new JLabel("Modelo:"));
        txtModelo = new JTextField();
        panelForm.add(txtModelo);

        panelForm.add(new JLabel("Cap. Passageiros:"));
        txtCapPass = new JTextField();
        panelForm.add(txtCapPass);

        panelForm.add(new JLabel("Cap. Carga (Ton):"));
        txtCapCarga = new JTextField();
        panelForm.add(txtCapCarga);

        panelForm.add(new JLabel("Ano Fabricação:"));
        txtAno = new JTextField();
        panelForm.add(txtAno);

        panelForm.add(new JLabel("Horímetro (Horas):"));
        txtHorimetro = new JTextField();
        panelForm.add(txtHorimetro);

        panelForm.add(new JLabel("Status:"));
        cbStatus = new JComboBox<>(new String[]{"ATIVA", "EM_MANUTENCAO", "INATIVA"});
        panelForm.add(cbStatus);

        JButton btnSalvar = new JButton("Cadastrar Embarcação");
        btnSalvar.addActionListener(e -> salvarEmbarcacao());
        panelForm.add(btnSalvar);

        add(panelForm, BorderLayout.NORTH);

        // Tabela de Listagem (Centro)
        String[] colunas = {"ID", "Nome", "Modelo", "Passageiros", "Carga (t)", "Ano", "Horímetro", "Status"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tabela = new JTable(tableModel);
        add(new JScrollPane(tabela), BorderLayout.CENTER);
    }

    private void carregarTabela() {
        tableModel.setRowCount(0);
        List<Embarcacao> lista = controller.listarTodas();

        for (Embarcacao emb : lista) {
            tableModel.addRow(new Object[]{
                emb.getId(),
                emb.getNome(),
                emb.getModelo(),
                emb.getCapacidadePassageiros(),
                emb.getCapacidadeCargaTon(),
                emb.getAnoFabricacao(),
                emb.getHorimetroHoras() + " h",
                emb.getStatus()
            });
        }
    }

    private void salvarEmbarcacao() {
        try {
            String nome = txtNome.getText();
            String modelo = txtModelo.getText();
            int capPass = Integer.parseInt(txtCapPass.getText());
            double capCarga = Double.parseDouble(txtCapCarga.getText());
            int ano = Integer.parseInt(txtAno.getText());
            int horimetro = Integer.parseInt(txtHorimetro.getText());
            String status = (String) cbStatus.getSelectedItem();

            if (controller.cadastrar(nome, modelo, capPass, capCarga, ano, horimetro, status)) {
                JOptionPane.showMessageDialog(this, "Embarcação cadastrada com sucesso!");
                limparCampos();
                carregarTabela();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao cadastrar. Preencha os campos obrigatórios.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, insira valores numéricos válidos nos campos de capacidade, ano e horímetro.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void limparCampos() {
        txtNome.setText("");
        txtModelo.setText("");
        txtCapPass.setText("");
        txtCapCarga.setText("");
        txtAno.setText("");
        txtHorimetro.setText("");
        cbStatus.setSelectedIndex(0);
    }
}
