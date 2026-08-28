package view;

import controller.ManutencaoController;
import dao.EmbarcacaoDAO;
import model.Embarcacao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;

public class TelaManutencoes extends JFrame {

    private final ManutencaoController controller = new ManutencaoController();
    private JTable tblIncidentes;
    private JTable tblManutencoes;
    private DefaultTableModel modelIncidentes;
    private DefaultTableModel modelManutencoes;

    // Componentes para exibição de detalhes expansíveis
    private JTextArea txtDetalheIncidente;
    private JTextArea txtDetalheOS;

    private JComboBox<Embarcacao> cbEmbarcacoes;
    private JComboBox<String> cbTipo;
    private JTextArea txtDescricao;
    private JTextField txtHorimetro;
    private JSpinner spDataAgendamento;
    private JTextField txtCusto;

    public TelaManutencoes() {
        setTitle("Gestão de Manutenções, Preventivas e Incidentes (ADM)");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Central de Incidentes (Operadores)", criarPainelIncidentes());
        tabbedPane.addTab("Ordens de Serviço e Agendamentos", criarPainelManutencoes());

        add(tabbedPane);
        carregarDados();
    }

    private JPanel criarPainelIncidentes() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        modelIncidentes = new DefaultTableModel(new String[]{"ID", "Embarcação", "Data Incidente", "Descrição", "Gravidade", "Status", "ID_EMB"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblIncidentes = new JTable(modelIncidentes);
        
        // Esconde a coluna ID_EMB que guarda a chave estrangeira
        tblIncidentes.getColumnModel().getColumn(6).setMinWidth(0);
        tblIncidentes.getColumnModel().getColumn(6).setMaxWidth(0);
        tblIncidentes.getColumnModel().getColumn(6).setWidth(0);

        JScrollPane scrollTabela = new JScrollPane(tblIncidentes);

        // Painel de Detalhes do Incidente
        JPanel pnlDetalhes = new JPanel(new BorderLayout());
        pnlDetalhes.setBorder(BorderFactory.createTitledBorder(" Detalhes do Incidente Selecionado "));

        txtDetalheIncidente = new JTextArea(4, 50);
        txtDetalheIncidente.setEditable(false);
        txtDetalheIncidente.setLineWrap(true);
        txtDetalheIncidente.setWrapStyleWord(true);
        txtDetalheIncidente.setBackground(new Color(245, 245, 245));
        txtDetalheIncidente.setFont(new Font("Monospaced", Font.PLAIN, 12));
        pnlDetalhes.add(new JScrollPane(txtDetalheIncidente), BorderLayout.CENTER);

        tblIncidentes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tblIncidentes.getSelectedRow();
                if (row != -1) {
                    txtDetalheIncidente.setText(
                        "ID INCIDENTE : " + modelIncidentes.getValueAt(row, 0) + "\n" +
                        "EMBARCAÇÃO  : " + modelIncidentes.getValueAt(row, 1) + "\n" +
                        "DATA/HORA   : " + modelIncidentes.getValueAt(row, 2) + "\n" +
                        "GRAVIDADE   : " + modelIncidentes.getValueAt(row, 4) + "\n" +
                        "STATUS      : " + modelIncidentes.getValueAt(row, 5) + "\n" +
                        "----------------------------------------------------------------------\n" +
                        "DESCRIÇÃO COMPLETA:\n" + modelIncidentes.getValueAt(row, 3)
                    );
                    txtDetalheIncidente.setCaretPosition(0);
                }
            }
        });

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollTabela, pnlDetalhes);
        splitPane.setResizeWeight(0.65);

        panel.add(splitPane, BorderLayout.CENTER);

        JPanel pnlAcoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnGerarOS = new JButton("Aprovar e Gerar Ordem de Serviço (OS)");
        btnGerarOS.setBackground(new Color(41, 128, 185));
        btnGerarOS.setForeground(Color.WHITE);

        btnGerarOS.addActionListener(e -> aprovarEGerarOS());
        pnlAcoes.add(btnGerarOS);
        panel.add(pnlAcoes, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel criarPainelManutencoes() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBorder(BorderFactory.createTitledBorder(" Nova Ordem de Serviço / Agendamento "));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; pnlForm.add(new JLabel("Embarcação:"), gbc);
        gbc.gridx = 1; cbEmbarcacoes = new JComboBox<>(); pnlForm.add(cbEmbarcacoes, gbc);

        gbc.gridx = 2; gbc.gridy = 0; pnlForm.add(new JLabel("Tipo:"), gbc);
        gbc.gridx = 3; cbTipo = new JComboBox<>(new String[]{"PREVENTIVA", "CORRETIVA"}); pnlForm.add(cbTipo, gbc);

        gbc.gridx = 0; gbc.gridy = 1; pnlForm.add(new JLabel("Descrição Serviço:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        txtDescricao = new JTextArea(2, 20);
        pnlForm.add(new JScrollPane(txtDescricao), gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 2; pnlForm.add(new JLabel("Horímetro Meta:"), gbc);
        gbc.gridx = 1; txtHorimetro = new JTextField(); pnlForm.add(txtHorimetro, gbc);

        gbc.gridx = 2; gbc.gridy = 2; pnlForm.add(new JLabel("Data Agendada:"), gbc);
        gbc.gridx = 3;
        spDataAgendamento = new JSpinner(new SpinnerDateModel());
        spDataAgendamento.setEditor(new JSpinner.DateEditor(spDataAgendamento, "dd/MM/yyyy"));
        pnlForm.add(spDataAgendamento, gbc);

        gbc.gridx = 0; gbc.gridy = 3; pnlForm.add(new JLabel("Custo Estimado (R$):"), gbc);
        gbc.gridx = 1; txtCusto = new JTextField("0.00"); pnlForm.add(txtCusto, gbc);

        gbc.gridx = 3; gbc.gridy = 3;
        JButton btnSalvar = new JButton("Encaminhar para Técnico");
        btnSalvar.addActionListener(e -> salvarManutencao());
        pnlForm.add(btnSalvar, gbc);

        panel.add(pnlForm, BorderLayout.NORTH);

        modelManutencoes = new DefaultTableModel(new String[]{"ID", "Embarcação", "Tipo", "Descrição", "Horímetro", "Data Agendada", "Custo (R$)", "Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblManutencoes = new JTable(modelManutencoes);

        JScrollPane scrollTabela = new JScrollPane(tblManutencoes);

        // Painel de Detalhes da Ordem de Serviço
        JPanel pnlDetalhesOS = new JPanel(new BorderLayout());
        pnlDetalhesOS.setBorder(BorderFactory.createTitledBorder(" Detalhes da Ordem de Serviço Selecionada "));

        txtDetalheOS = new JTextArea(4, 50);
        txtDetalheOS.setEditable(false);
        txtDetalheOS.setLineWrap(true);
        txtDetalheOS.setWrapStyleWord(true);
        txtDetalheOS.setBackground(new Color(245, 245, 245));
        txtDetalheOS.setFont(new Font("Monospaced", Font.PLAIN, 12));
        pnlDetalhesOS.add(new JScrollPane(txtDetalheOS), BorderLayout.CENTER);

        tblManutencoes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tblManutencoes.getSelectedRow();
                if (row != -1) {
                    txtDetalheOS.setText(
                        "ID OS               : " + modelManutencoes.getValueAt(row, 0) + "\n" +
                        "EMBARCAÇÃO          : " + modelManutencoes.getValueAt(row, 1) + "\n" +
                        "TIPO MANUTENÇÃO     : " + modelManutencoes.getValueAt(row, 2) + "\n" +
                        "HORÍMETRO META      : " + modelManutencoes.getValueAt(row, 4) + "\n" +
                        "DATA AGENDADA       : " + modelManutencoes.getValueAt(row, 5) + "\n" +
                        "CUSTO ESTIMADO (R$) : " + modelManutencoes.getValueAt(row, 6) + "\n" +
                        "STATUS              : " + modelManutencoes.getValueAt(row, 7) + "\n" +
                        "----------------------------------------------------------------------\n" +
                        "DESCRIÇÃO DO SERVIÇO:\n" + modelManutencoes.getValueAt(row, 3)
                    );
                    txtDetalheOS.setCaretPosition(0);
                }
            }
        });

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollTabela, pnlDetalhesOS);
        splitPane.setResizeWeight(0.55);

        panel.add(splitPane, BorderLayout.CENTER);

        JPanel pnlStatus = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnEmAndamento = new JButton("Marcar 'EM ANDAMENTO'");
        JButton btnCancelar = new JButton("Cancelar OS");

        btnEmAndamento.addActionListener(e -> alterarStatusOS("EM_ANDAMENTO"));
        btnCancelar.addActionListener(e -> alterarStatusOS("CANCELADA"));

        pnlStatus.add(btnEmAndamento);
        pnlStatus.add(btnCancelar);
        panel.add(pnlStatus, BorderLayout.SOUTH);

        return panel;
    }

    private void carregarDados() {
        cbEmbarcacoes.removeAllItems();
        new EmbarcacaoDAO().listarTodas().forEach(cbEmbarcacoes::addItem);

        modelIncidentes.setRowCount(0);
        for (Object[] row : controller.obterIncidentesPendentes()) {
            modelIncidentes.addRow(row);
        }

        modelManutencoes.setRowCount(0);
        for (Object[] row : controller.obterManutencoes()) {
            modelManutencoes.addRow(row);
        }

        if (txtDetalheIncidente != null) txtDetalheIncidente.setText("");
        if (txtDetalheOS != null) txtDetalheOS.setText("");
    }

    private void aprovarEGerarOS() {
        int row = tblIncidentes.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um incidente na tabela para aprovação.");
            return;
        }

        int idIncidente = (int) modelIncidentes.getValueAt(row, 0);
        String desc = (String) modelIncidentes.getValueAt(row, 3);
        int idEmbarcacao = (int) modelIncidentes.getValueAt(row, 6);

        Date dataHoje = new Date(System.currentTimeMillis());
        boolean ok = controller.converterIncidenteEmOS(idIncidente, idEmbarcacao, desc, dataHoje);

        if (ok) {
            JOptionPane.showMessageDialog(this, "Ordem de Serviço gerada e encaminhada ao Técnico!");
            carregarDados();
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao converter incidente em OS.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void salvarManutencao() {
        try {
            Embarcacao emb = (Embarcacao) cbEmbarcacoes.getSelectedItem();
            String tipo = (String) cbTipo.getSelectedItem();
            String desc = txtDescricao.getText();
            Integer horimetro = txtHorimetro.getText().trim().isEmpty() ? null : Integer.parseInt(txtHorimetro.getText().trim());
            java.util.Date d = (java.util.Date) spDataAgendamento.getValue();
            Date dataAgendada = new Date(d.getTime());
            double custo = Double.parseDouble(txtCusto.getText().replace(",", "."));

            String res = controller.criarOrdemServico(emb.getId(), tipo, desc, horimetro, dataAgendada, custo);
            if ("OK".equals(res)) {
                JOptionPane.showMessageDialog(this, "OS registrada e enviada para o painel do Técnico!");
                txtDescricao.setText("");
                txtHorimetro.setText("");
                txtCusto.setText("0.00");
                carregarDados();
            } else {
                JOptionPane.showMessageDialog(this, res, "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Verifique os campos numéricos e datas.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void alterarStatusOS(String novoStatus) {
        int row = tblManutencoes.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma manutenção na tabela.");
            return;
        }
        int id = (int) modelManutencoes.getValueAt(row, 0);
        if (controller.atualizarStatusOS(id, novoStatus)) {
            JOptionPane.showMessageDialog(this, "Status atualizado!");
            carregarDados();
        }
    }
}