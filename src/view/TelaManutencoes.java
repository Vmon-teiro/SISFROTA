package view;

import controller.EmbarcacaoController;
import controller.ManutencaoController;
import model.Embarcacao;
import model.Manutencao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TelaManutencoes extends JFrame {

    private final ManutencaoController controller;
    private final EmbarcacaoController embarcacaoController;

    private JTable tabela;
    private DefaultTableModel tableModel;

    private JComboBox<String> cbEmbarcacoes, cbTipo, cbStatus;
    private JTextField txtDescricao, txtHorimetro, txtDataAgendamento;
    private List<Embarcacao> listaEmbarcacoes;

    public TelaManutencoes() {
        controller = new ManutencaoController();
        embarcacaoController = new EmbarcacaoController();

        setTitle("Gestão Náutica - Ordens de Serviço e Manutenções");
        setSize(850, 520);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponentes();
        carregarEmbarcacoes();
        carregarTabela();
    }

    private void initComponentes() {
        setLayout(new BorderLayout());

        JPanel panelForm = new JPanel(new GridLayout(4, 3, 10, 10));
        panelForm.setBorder(BorderFactory.createTitledBorder(" Agendar Manutenção "));

        panelForm.add(new JLabel("Embarcação:"));
        cbEmbarcacoes = new JComboBox<>();
        panelForm.add(cbEmbarcacoes);

        panelForm.add(new JLabel("Tipo:"));
        cbTipo = new JComboBox<>(new String[]{"PREVENTIVA", "CORRETIVA"});
        panelForm.add(cbTipo);

        panelForm.add(new JLabel("Descrição do Serviço:"));
        txtDescricao = new JTextField();
        panelForm.add(txtDescricao);

        panelForm.add(new JLabel("Horímetro Limite (h):"));
        txtHorimetro = new JTextField();
        panelForm.add(txtHorimetro);

        panelForm.add(new JLabel("Data Agendamento (AAAA-MM-DD):"));
        txtDataAgendamento = new JTextField("");
        panelForm.add(txtDataAgendamento);

        panelForm.add(new JLabel("Status Inicial:"));
        cbStatus = new JComboBox<>(new String[]{"AGENDADA", "EM_ANDAMENTO"});
        panelForm.add(cbStatus);

        JButton btnAgendar = new JButton("Salvar Ordem de Serviço");
        btnAgendar.addActionListener(e -> agendarManutencao());
        panelForm.add(btnAgendar);

        add(panelForm, BorderLayout.NORTH);

        String[] colunas = {"ID", "Embarcação", "Tipo", "Descrição", "Horímetro", "Data Agendada", "Custo (R$)", "Status"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tabela = new JTable(tableModel);
        add(new JScrollPane(tabela), BorderLayout.CENTER);
    }

    private void carregarEmbarcacoes() {
        cbEmbarcacoes.removeAllItems();
        listaEmbarcacoes = embarcacaoController.listarTodas();
        for (Embarcacao emb : listaEmbarcacoes) {
            cbEmbarcacoes.addItem(emb.getNome() + " (" + emb.getModelo() + ")");
        }
    }

    private void carregarTabela() {
        tableModel.setRowCount(0);
        List<Manutencao> lista = controller.listarTodas();

        for (Manutencao m : lista) {
            tableModel.addRow(new Object[]{
                m.getId(),
                m.getNomeEmbarcacao(),
                m.getTipoManutencao(),
                m.getDescricaoServico(),
                m.getHorimetroAgendado() != null ? m.getHorimetroAgendado() + " h" : "N/A",
                m.getDataAgendamento(),
                String.format("R$ %.2f", m.getCustoTotal()),
                m.getStatus()
            });
        }
    }

    private void agendarManutencao() {
        int indexEmb = cbEmbarcacoes.getSelectedIndex();
        if (indexEmb < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma embarcação válida.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int idEmbarcacao = listaEmbarcacoes.get(indexEmb).getId();
        String tipo = (String) cbTipo.getSelectedItem();
        String descricao = txtDescricao.getText();
        String dataStr = txtDataAgendamento.getText();
        String status = (String) cbStatus.getSelectedItem();

        Integer horimetro = null;
        if (!txtHorimetro.getText().trim().isEmpty()) {
            try {
                horimetro = Integer.parseInt(txtHorimetro.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Horímetro deve ser um número inteiro.", "Erro", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        if (controller.agendar(idEmbarcacao, tipo, descricao, horimetro, dataStr, status)) {
            JOptionPane.showMessageDialog(this, "Manutenção agendada com sucesso!");
            txtDescricao.setText("");
            txtHorimetro.setText("");
            carregarTabela();
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao agendar. Verifique o formato da data (AAAA-MM-DD) e preencha os campos obrigatórios.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
