package view;

import controller.EmbarcacaoController;
import controller.ManutencaoController;
import model.Embarcacao;
import model.Manutencao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.util.List;

public class TelaManutencoes extends JFrame {

    private final ManutencaoController controller;
    private final EmbarcacaoController embarcacaoController;
    private JTable tabela;
    private DefaultTableModel tableModel;
    private JComboBox<String> cbEmbarcacoes, cbTipo, cbStatus;
    private JTextField txtDescricao, txtHorimetro, txtDataAgendamento;
    private JTextField txtDataExecucao, txtCustoTotal;
    private List<Embarcacao> listaEmbarcacoes;
    private List<Manutencao> listaManutencoes;

    public TelaManutencoes() {
        controller = new ManutencaoController();
        embarcacaoController = new EmbarcacaoController();
        setTitle("Gestão Náutica - Ordens de Serviço e Manutenções");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponentes();
        carregarEmbarcacoes();
        carregarTabela();
    }

    private void initComponentes() {
        setLayout(new BorderLayout(10, 10));

        // Formulário Superior: Agendar / Abrir O.S.
        JPanel panelForm = new JPanel(new GridLayout(4, 3, 10, 10));
        panelForm.setBorder(BorderFactory.createTitledBorder(" Nova Ordem de Serviço / Agendamento "));
        
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
        txtDataAgendamento = new JTextField();
        panelForm.add(txtDataAgendamento);

        panelForm.add(new JLabel("Status Inicial:"));
        cbStatus = new JComboBox<>(new String[]{"AGENDADA", "EM_ANDAMENTO"});
        panelForm.add(cbStatus);

        JButton btnAgendar = new JButton("Salvar Ordem de Serviço");
        btnAgendar.addActionListener(e -> agendarManutencao());
        panelForm.add(btnAgendar);

        add(panelForm, BorderLayout.NORTH);

        // Tabela Central: Listagem
        String[] colunas = {"ID", "Embarcação", "Tipo", "Descrição", "Horímetro", "Data Agendada", "Custo (R$)", "Status"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabela = new JTable(tableModel);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        // Painel Inferior: Baixa / Conclusão de O.S. (Funcionalidade Técnica)
        JPanel panelBaixa = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelBaixa.setBorder(BorderFactory.createTitledBorder(" Encerrar / Dar Baixa em O.S. Selecionada "));

        panelBaixa.add(new JLabel("Data Execução (AAAA-MM-DD):"));
        txtDataExecucao = new JTextField(10);
        panelBaixa.add(txtDataExecucao);

        panelBaixa.add(new JLabel("Custo Total (R$):"));
        txtCustoTotal = new JTextField(8);
        panelBaixa.add(txtCustoTotal);

        JButton btnConcluir = new JButton("Concluir Manutenção");
        btnConcluir.addActionListener(e -> concluirManutencao());
        panelBaixa.add(btnConcluir);

        add(panelBaixa, BorderLayout.SOUTH);
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
        listaManutencoes = controller.listarTodas();
        for (Manutencao m : listaManutencoes) {
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
            txtDataAgendamento.setText("");
            carregarTabela();
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao agendar. Verifique a data (AAAA-MM-DD).", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void concluirManutencao() {
        int linhaSelecionada = tabela.getSelectedRow();
        if (linhaSelecionada < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma manutenção na tabela para concluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int idManutencao = (int) tableModel.getValueAt(linhaSelecionada, 0);
            Date dataExec = Date.valueOf(txtDataExecucao.getText().trim());
            double custo = Double.parseDouble(txtCustoTotal.getText().trim());

            if (controller.concluirManutencao(idManutencao, dataExec, custo)) {
                JOptionPane.showMessageDialog(this, "Ordem de Serviço concluída com sucesso!");
                txtDataExecucao.setText("");
                txtCustoTotal.setText("");
                carregarTabela();
            } else {
                JOptionPane.showMessageDialog(this, "Não foi possível concluir a O.S.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Informe a data no formato AAAA-MM-DD e um custo válido.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
        }
    }
}