package view;

import controller.TecnicoController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class TelaDashboardTecnico extends JFrame {

    private final TecnicoController controller = new TecnicoController();

    private JTable tblOS, tblAlertas, tblHistorico;
    private DefaultTableModel modelOS, modelAlertas, modelHistorico;
    private JTextArea txtDetalhesOS, txtDetalhesAlertas, txtDetalhesHistorico;

    public TelaDashboardTecnico() {
        setTitle("Dashboard Técnico - Ordens de Serviço & Manutenção Fleet");
        setSize(1000, 680);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Ordens de Serviço Ativas", criarPainelOS());
        tabbedPane.addTab("Alertas de Horímetro (Preventivas)", criarPainelAlertas());
        tabbedPane.addTab("Histórico de Motores & Peças", criarPainelHistorico());

        add(tabbedPane);
        carregarDados();
    }

    private JPanel criarPainelOS() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        modelOS = new DefaultTableModel(new String[]{"ID OS", "Embarcação", "Tipo", "Descrição do Serviço", "Data Agendada", "Custo Est. (R$)", "Status", "ID_EMB"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tblOS = new JTable(modelOS);
        ajustarColunasTabelaOS();

        txtDetalhesOS = criarAreaTextoDetalhes();
        tblOS.getSelectionModel().addListSelectionListener(e -> {
            int row = tblOS.getSelectedRow();
            if (row != -1) {
                txtDetalhesOS.setText((String) modelOS.getValueAt(row, 3));
            } else {
                txtDetalhesOS.setText("");
            }
        });

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(tblOS), criarPainelDetalhesContainer(" Detalhes do Serviço Selecionado (OS) ", txtDetalhesOS));
        splitPane.setResizeWeight(0.65);

        panel.add(splitPane, BorderLayout.CENTER);

        JPanel pnlAcoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        
        JButton btnNovaOS = new JButton("+ Abrir Nova OS");
        btnNovaOS.setBackground(new Color(41, 128, 185));
        btnNovaOS.setForeground(Color.WHITE);
        btnNovaOS.addActionListener(e -> abrirDialogoNovaOS());

        JButton btnConcluir = new JButton("Concluir Serviço (Baixa de OS)");
        btnConcluir.setBackground(new Color(39, 174, 96));
        btnConcluir.setForeground(Color.WHITE);
        btnConcluir.addActionListener(e -> abrirDialogoConclusao());

        pnlAcoes.add(btnNovaOS);
        pnlAcoes.add(btnConcluir);
        panel.add(pnlAcoes, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel criarPainelAlertas() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        modelAlertas = new DefaultTableModel(new String[]{"ID Emb.", "Embarcação", "Horímetro Atual (h)", "Horímetro Alvo (h)", "Serviço Previsto"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tblAlertas = new JTable(modelAlertas);
        tblAlertas.getColumnModel().getColumn(0).setPreferredWidth(60);
        tblAlertas.getColumnModel().getColumn(1).setPreferredWidth(180);
        tblAlertas.getColumnModel().getColumn(2).setPreferredWidth(130);
        tblAlertas.getColumnModel().getColumn(3).setPreferredWidth(130);
        tblAlertas.getColumnModel().getColumn(4).setPreferredWidth(400);

        txtDetalhesAlertas = criarAreaTextoDetalhes();
        tblAlertas.getSelectionModel().addListSelectionListener(e -> {
            int row = tblAlertas.getSelectedRow();
            if (row != -1) {
                txtDetalhesAlertas.setText((String) modelAlertas.getValueAt(row, 4));
            } else {
                txtDetalhesAlertas.setText("");
            }
        });

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(tblAlertas), criarPainelDetalhesContainer(" Detalhes do Alerta Preventivo ", txtDetalhesAlertas));
        splitPane.setResizeWeight(0.65);

        panel.add(splitPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel criarPainelHistorico() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        modelHistorico = new DefaultTableModel(new String[]{"ID OS", "Embarcação", "Tipo", "Serviço Realizado / Troca de Peças", "Data Execução", "Custo (R$)"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tblHistorico = new JTable(modelHistorico);
        tblHistorico.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblHistorico.getColumnModel().getColumn(1).setPreferredWidth(160);
        tblHistorico.getColumnModel().getColumn(2).setPreferredWidth(100);
        tblHistorico.getColumnModel().getColumn(3).setPreferredWidth(380);
        tblHistorico.getColumnModel().getColumn(4).setPreferredWidth(110);
        tblHistorico.getColumnModel().getColumn(5).setPreferredWidth(100);

        txtDetalhesHistorico = criarAreaTextoDetalhes();
        tblHistorico.getSelectionModel().addListSelectionListener(e -> {
            int row = tblHistorico.getSelectedRow();
            if (row != -1) {
                txtDetalhesHistorico.setText((String) modelHistorico.getValueAt(row, 3));
            } else {
                txtDetalhesHistorico.setText("");
            }
        });

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(tblHistorico), criarPainelDetalhesContainer(" Histórico Detalhado Mecânico ", txtDetalhesHistorico));
        splitPane.setResizeWeight(0.65);

        panel.add(splitPane, BorderLayout.CENTER);
        return panel;
    }

    private JTextArea criarAreaTextoDetalhes() {
        JTextArea area = new JTextArea(4, 50);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font("SansSerif", Font.PLAIN, 13));
        return area;
    }

    private JPanel criarPainelDetalhesContainer(String titulo, JTextArea areaTexto) {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setBorder(BorderFactory.createTitledBorder(titulo));
        pnl.add(new JScrollPane(areaTexto), BorderLayout.CENTER);
        return pnl;
    }

    private void ajustarColunasTabelaOS() {
        tblOS.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblOS.getColumnModel().getColumn(1).setPreferredWidth(150);
        tblOS.getColumnModel().getColumn(2).setPreferredWidth(90);
        tblOS.getColumnModel().getColumn(3).setPreferredWidth(320);
        tblOS.getColumnModel().getColumn(4).setPreferredWidth(100);
        tblOS.getColumnModel().getColumn(5).setPreferredWidth(100);
        tblOS.getColumnModel().getColumn(6).setPreferredWidth(110);

        tblOS.getColumnModel().getColumn(7).setMinWidth(0);
        tblOS.getColumnModel().getColumn(7).setMaxWidth(0);
        tblOS.getColumnModel().getColumn(7).setWidth(0);
    }

    private void carregarDados() {
        modelOS.setRowCount(0);
        NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));

        for (Object[] row : controller.obterOSAbertas()) {
            modelOS.addRow(new Object[]{
                row[0], row[1], row[2], row[3], row[4],
                nf.format((double) row[5]), row[6], row[7]
            });
        }

        modelAlertas.setRowCount(0);
        for (Object[] row : controller.obterAlertasHorimetro()) {
            modelAlertas.addRow(row);
        }

        modelHistorico.setRowCount(0);
        for (Object[] row : controller.obterHistoricoMotores()) {
            modelHistorico.addRow(new Object[]{
                row[0], row[1], row[2], row[3], row[4],
                nf.format((double) row[5])
            });
        }
    }

    private void abrirDialogoNovaOS() {
        List<Object[]> embarcacoes = controller.obterEmbarcacoes();
        if (embarcacoes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhuma embarcação cadastrada.");
            return;
        }

        JComboBox<String> cbEmbarcacao = new JComboBox<>();
        for (Object[] emb : embarcacoes) {
            cbEmbarcacao.addItem(emb[0] + " - " + emb[1]);
        }

        JComboBox<String> cbTipo = new JComboBox<>(new String[]{"PREVENTIVA", "CORRETIVA"});
        JTextArea txtDescricao = new JTextArea(4, 25);
        txtDescricao.setLineWrap(true);
        txtDescricao.setWrapStyleWord(true);

        JTextField txtHorimetro = new JTextField();
        JSpinner spDataAgendamento = new JSpinner(new SpinnerDateModel());
        spDataAgendamento.setEditor(new JSpinner.DateEditor(spDataAgendamento, "dd/MM/yyyy"));

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("Embarcação:"));
        panel.add(cbEmbarcacao);
        panel.add(new JLabel("Tipo de Manutenção:"));
        panel.add(cbTipo);
        panel.add(new JLabel("Descrição Detalhada do Serviço / Peças:"));
        panel.add(new JScrollPane(txtDescricao));
        panel.add(new JLabel("Horímetro Agendado (Gatilho Preventiva) [Opcional]:"));
        panel.add(txtHorimetro);
        panel.add(new JLabel("Data Prevista para Execução:"));
        panel.add(spDataAgendamento);

        int result = JOptionPane.showConfirmDialog(this, panel, "Abrir Nova Ordem de Serviço", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            int selectedIndex = cbEmbarcacao.getSelectedIndex();
            int idEmbarcacao = (int) embarcacoes.get(selectedIndex)[0];
            String tipo = (String) cbTipo.getSelectedItem();
            String desc = txtDescricao.getText();
            String horimetroStr = txtHorimetro.getText();
            java.util.Date dataAgendamento = (java.util.Date) spDataAgendamento.getValue();

            String res = controller.criarNovaOS(idEmbarcacao, tipo, desc, horimetroStr, dataAgendamento);
            if ("OK".equals(res)) {
                JOptionPane.showMessageDialog(this, "Ordem de Serviço criada com sucesso!");
                carregarDados();
            } else {
                JOptionPane.showMessageDialog(this, res, "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void abrirDialogoConclusao() {
        int row = tblOS.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma Ordem de Serviço na tabela para concluir.");
            return;
        }

        int idOS = (int) modelOS.getValueAt(row, 0);
        String embarcacao = (String) modelOS.getValueAt(row, 1);
        int idEmbarcacao = (int) modelOS.getValueAt(row, 7);

        JTextField txtHorimetro = new JTextField();
        JTextField txtCustoReal = new JTextField("0.00");

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("Embarcação: " + embarcacao));
        panel.add(new JLabel("Horímetro Atual do Motor (Horas Leitura):"));
        panel.add(txtHorimetro);
        panel.add(new JLabel("Custo Real Final do Serviço / Peças (R$):"));
        panel.add(txtCustoReal);

        int result = JOptionPane.showConfirmDialog(this, panel, "Encerrar e Dar Baixa na OS #" + idOS, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String res = controller.finalizarManutencao(idOS, idEmbarcacao, txtHorimetro.getText(), txtCustoReal.getText());
            if ("OK".equals(res)) {
                JOptionPane.showMessageDialog(this, "OS encerrada com sucesso e cadastrada no histórico do motor!");
                carregarDados();
            } else {
                JOptionPane.showMessageDialog(this, res, "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
}