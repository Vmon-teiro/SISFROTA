package view;

import controller.TecnicoController;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class TelaDashboardTecnico extends JFrame {

    // Paleta Visual Padronizada
    private static final Color BG_APP            = new Color(241, 245, 249); // slate-100
    private static final Color HEADER_DARK       = new Color(15, 23, 42);    // slate-900
    private static final Color CARD_BG           = Color.WHITE;
    private static final Color CARD_BORDER       = new Color(226, 232, 240); // slate-200
    private static final Color TEXT_TITLE        = new Color(15, 23, 42);    // slate-900
    private static final Color TEXT_MUTED        = new Color(100, 116, 139); // slate-500
    private static final Color PRIMARY_BLUE      = new Color(37, 99, 235);   // blue-600
    private static final Color PRIMARY_GREEN     = new Color(16, 185, 129);  // emerald-500

    private final TecnicoController controller = new TecnicoController();
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));

    // Componentes de Tabelas
    private JTable tblOS, tblAlertas, tblHistorico;
    private DefaultTableModel modelOS, modelAlertas, modelHistorico;
    private JTextArea txtDetalhesOS, txtDetalhesAlertas, txtDetalhesHistorico;

    public TelaDashboardTecnico() {
        setTitle("Dashboard Técnico - Ordens de Serviço & Manutenção Fleet");
        setSize(1150, 750);
        setMinimumSize(new Dimension(950, 600));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponentes();
        carregarDados();
    }

    private void initComponentes() {
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(BG_APP);

        add(criarHeader(), BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabbedPane.setBackground(BG_APP);

        tabbedPane.addTab("Ordens de Serviço Ativas", criarPainelOS());
        tabbedPane.addTab("Alertas de Horímetro (Preventivas)", criarPainelAlertas());
        tabbedPane.addTab("Histórico de Motores & Peças", criarPainelHistorico());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel criarHeader() {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(HEADER_DARK);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        header.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        JLabel lblTitle = new JLabel("DASHBOARD TÉCNICO & MANUTENÇÃO");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Gestão de ordens de serviço, alertas de horímetro e histórico preventivo/corretivo da frota");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setForeground(new Color(148, 163, 184));

        JPanel pnlTexto = new JPanel();
        pnlTexto.setLayout(new BoxLayout(pnlTexto, BoxLayout.Y_AXIS));
        pnlTexto.setOpaque(false);
        pnlTexto.add(lblTitle);
        pnlTexto.add(lblSub);

        header.add(pnlTexto, BorderLayout.WEST);
        return header;
    }

    // ==========================================
    // ABA 1: ORDENS DE SERVIÇO ATIVAS
    // ==========================================
    private JPanel criarPainelOS() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(BG_APP);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        modelOS = new DefaultTableModel(new String[]{"ID OS", "Embarcação", "Tipo", "Descrição do Serviço", "Data Agendada", "Custo Est. (R$)", "Status", "ID_EMB"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tblOS = new JTable(modelOS);
        estilarTabela(tblOS);
        ajustarColunasTabelaOS();

        txtDetalhesOS = criarAreaTextoDetalhes();
        tblOS.getSelectionModel().addListSelectionListener(e -> {
            int row = tblOS.getSelectedRow();
            if (row != -1) {
                txtDetalhesOS.setText((String) modelOS.getValueAt(row, 3));
                txtDetalhesOS.setCaretPosition(0);
            } else {
                txtDetalhesOS.setText("");
            }
        });

        JScrollPane scrollTabela = new JScrollPane(tblOS);
        scrollTabela.setBorder(BorderFactory.createLineBorder(CARD_BORDER));
        scrollTabela.getViewport().setBackground(Color.WHITE);

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                scrollTabela,
                criarPainelDetalhesContainer("Detalhes do Serviço Selecionado (OS)", txtDetalhesOS)
        );
        splitPane.setResizeWeight(0.65);
        splitPane.setBorder(null);

        panel.add(splitPane, BorderLayout.CENTER);

        JPanel pnlAcoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        pnlAcoes.setBackground(BG_APP);

        JButton btnNovaOS = criarBotaoArredondado("+ Abrir Nova OS", PRIMARY_BLUE);
        JButton btnConcluir = criarBotaoArredondado("Concluir Serviço (Baixa de OS)", PRIMARY_GREEN);

        btnNovaOS.addActionListener(e -> abrirDialogoNovaOS());
        btnConcluir.addActionListener(e -> abrirDialogoConclusao());

        pnlAcoes.add(btnNovaOS);
        pnlAcoes.add(btnConcluir);
        panel.add(pnlAcoes, BorderLayout.SOUTH);

        return panel;
    }

    // ==========================================
    // ABA 2: ALERTAS DE HORÍMETRO
    // ==========================================
    private JPanel criarPainelAlertas() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(BG_APP);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        modelAlertas = new DefaultTableModel(new String[]{"ID Emb.", "Embarcação", "Horímetro Atual (h)", "Horímetro Alvo (h)", "Serviço Previsto"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tblAlertas = new JTable(modelAlertas);
        estilarTabela(tblAlertas);
        ajustarColunasTabelaAlertas();

        txtDetalhesAlertas = criarAreaTextoDetalhes();
        tblAlertas.getSelectionModel().addListSelectionListener(e -> {
            int row = tblAlertas.getSelectedRow();
            if (row != -1) {
                txtDetalhesAlertas.setText((String) modelAlertas.getValueAt(row, 4));
                txtDetalhesAlertas.setCaretPosition(0);
            } else {
                txtDetalhesAlertas.setText("");
            }
        });

        JScrollPane scrollTabela = new JScrollPane(tblAlertas);
        scrollTabela.setBorder(BorderFactory.createLineBorder(CARD_BORDER));
        scrollTabela.getViewport().setBackground(Color.WHITE);

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                scrollTabela,
                criarPainelDetalhesContainer("Detalhes do Alerta Preventivo", txtDetalhesAlertas)
        );
        splitPane.setResizeWeight(0.65);
        splitPane.setBorder(null);

        panel.add(splitPane, BorderLayout.CENTER);
        return panel;
    }

    // ==========================================
    // ABA 3: HISTÓRICO DE MOTORES & PEÇAS
    // ==========================================
    private JPanel criarPainelHistorico() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(BG_APP);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        modelHistorico = new DefaultTableModel(new String[]{"ID OS", "Embarcação", "Tipo", "Serviço Realizado / Troca de Peças", "Data Execução", "Custo (R$)"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tblHistorico = new JTable(modelHistorico);
        estilarTabela(tblHistorico);
        ajustarColunasTabelaHistorico();

        txtDetalhesHistorico = criarAreaTextoDetalhes();
        tblHistorico.getSelectionModel().addListSelectionListener(e -> {
            int row = tblHistorico.getSelectedRow();
            if (row != -1) {
                txtDetalhesHistorico.setText((String) modelHistorico.getValueAt(row, 3));
                txtDetalhesHistorico.setCaretPosition(0);
            } else {
                txtDetalhesHistorico.setText("");
            }
        });

        JScrollPane scrollTabela = new JScrollPane(tblHistorico);
        scrollTabela.setBorder(BorderFactory.createLineBorder(CARD_BORDER));
        scrollTabela.getViewport().setBackground(Color.WHITE);

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                scrollTabela,
                criarPainelDetalhesContainer("Histórico Detalhado Mecânico", txtDetalhesHistorico)
        );
        splitPane.setResizeWeight(0.65);
        splitPane.setBorder(null);

        panel.add(splitPane, BorderLayout.CENTER);
        return panel;
    }

    // ==========================================
    // LÓGICA DE DADOS & DIÁLOGOS
    // ==========================================
    private void carregarDados() {
        modelOS.setRowCount(0);
        for (Object[] row : controller.obterOSAbertas()) {
            modelOS.addRow(new Object[]{
                row[0], row[1], row[2], row[3], row[4],
                currencyFormatter.format((double) row[5]), row[6], row[7]
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
                currencyFormatter.format((double) row[5])
            });
        }
    }

    private void abrirDialogoNovaOS() {
        List<Object[]> embarcacoes = controller.obterEmbarcacoes();
        if (embarcacoes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhuma embarcação cadastrada.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JComboBox<String> cbEmbarcacao = new JComboBox<>();
        for (Object[] emb : embarcacoes) {
            cbEmbarcacao.addItem(emb[0] + " - " + emb[1]);
        }

        JComboBox<String> cbTipo = new JComboBox<>(new String[]{"PREVENTIVA", "CORRETIVA"});
        JTextArea txtDescricao = new JTextArea(4, 25);
        txtDescricao.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtDescricao.setLineWrap(true);
        txtDescricao.setWrapStyleWord(true);

        JTextField txtHorimetro = new JTextField();
        JSpinner spDataAgendamento = new JSpinner(new SpinnerDateModel());
        spDataAgendamento.setEditor(new JSpinner.DateEditor(spDataAgendamento, "dd/MM/yyyy"));

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.add(criarLabel("Embarcação:"));
        panel.add(cbEmbarcacao);
        panel.add(criarLabel("Tipo de Manutenção:"));
        panel.add(cbTipo);
        panel.add(criarLabel("Descrição Detalhada do Serviço / Peças:"));
        panel.add(new JScrollPane(txtDescricao));
        panel.add(criarLabel("Horímetro Agendado (Gatilho Preventiva) [Opcional]:"));
        panel.add(txtHorimetro);
        panel.add(criarLabel("Data Prevista para Execução:"));
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
                JOptionPane.showMessageDialog(this, "Ordem de Serviço criada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarDados();
            } else {
                JOptionPane.showMessageDialog(this, res, "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void abrirDialogoConclusao() {
        int row = tblOS.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma Ordem de Serviço na tabela para concluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idOS = (int) modelOS.getValueAt(row, 0);
        String embarcacao = (String) modelOS.getValueAt(row, 1);
        int idEmbarcacao = (int) modelOS.getValueAt(row, 7);

        JTextField txtHorimetro = new JTextField();
        JTextField txtCustoReal = new JTextField("0.00");

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.add(criarLabel("Embarcação: " + embarcacao));
        panel.add(criarLabel("Horímetro Atual do Motor (Horas Leitura):"));
        panel.add(txtHorimetro);
        panel.add(criarLabel("Custo Real Final do Serviço / Peças (R$):"));
        panel.add(txtCustoReal);

        int result = JOptionPane.showConfirmDialog(this, panel, "Encerrar e Dar Baixa na OS #" + idOS, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String res = controller.finalizarManutencao(idOS, idEmbarcacao, txtHorimetro.getText(), txtCustoReal.getText());
            if ("OK".equals(res)) {
                JOptionPane.showMessageDialog(this, "OS encerrada com sucesso e cadastrada no histórico do motor!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarDados();
            } else {
                JOptionPane.showMessageDialog(this, res, "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    // ==========================================
    // UTILITÁRIOS VISUAIS E CONFIGURAÇÃO
    // ==========================================
    private JButton criarBotaoArredondado(String texto, Color corFundo) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2.setColor(corFundo.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(corFundo.brighter());
                } else {
                    g2.setColor(corFundo);
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();

                super.paintComponent(g);
            }
        };

        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        return btn;
    }

    private JLabel criarLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setForeground(TEXT_TITLE);
        return label;
    }

    private JTextArea criarAreaTextoDetalhes() {
        JTextArea area = new JTextArea(4, 50);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBackground(new Color(248, 250, 252));
        area.setFont(new Font("Consolas", Font.PLAIN, 12));
        area.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        return area;
    }

    private JPanel criarPainelDetalhesContainer(String titulo, JTextArea areaTexto) {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setBackground(CARD_BG);
        pnl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        JLabel lblTitulo = criarLabel(titulo + ":");
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));

        JScrollPane scrollArea = new JScrollPane(areaTexto);
        scrollArea.setBorder(BorderFactory.createLineBorder(CARD_BORDER));

        pnl.add(lblTitulo, BorderLayout.NORTH);
        pnl.add(scrollArea, BorderLayout.CENTER);
        return pnl;
    }

    private void estilarTabela(JTable table) {
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(224, 231, 255));
        table.setSelectionForeground(TEXT_TITLE);

        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        table.getTableHeader().setBackground(new Color(248, 250, 252));
        table.getTableHeader().setForeground(TEXT_MUTED);
        table.getTableHeader().setPreferredSize(new Dimension(0, 32));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
    }

    private void ajustarColunasTabelaOS() {
        tblOS.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblOS.getColumnModel().getColumn(1).setPreferredWidth(160);
        tblOS.getColumnModel().getColumn(2).setPreferredWidth(100);
        tblOS.getColumnModel().getColumn(3).setPreferredWidth(320);
        tblOS.getColumnModel().getColumn(4).setPreferredWidth(110);
        tblOS.getColumnModel().getColumn(5).setPreferredWidth(110);
        tblOS.getColumnModel().getColumn(6).setPreferredWidth(110);

        tblOS.getColumnModel().getColumn(7).setMinWidth(0);
        tblOS.getColumnModel().getColumn(7).setMaxWidth(0);
        tblOS.getColumnModel().getColumn(7).setWidth(0);
    }

    private void ajustarColunasTabelaAlertas() {
        tblAlertas.getColumnModel().getColumn(0).setPreferredWidth(60);
        tblAlertas.getColumnModel().getColumn(1).setPreferredWidth(180);
        tblAlertas.getColumnModel().getColumn(2).setPreferredWidth(140);
        tblAlertas.getColumnModel().getColumn(3).setPreferredWidth(140);
        tblAlertas.getColumnModel().getColumn(4).setPreferredWidth(400);
    }

    private void ajustarColunasTabelaHistorico() {
        tblHistorico.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblHistorico.getColumnModel().getColumn(1).setPreferredWidth(160);
        tblHistorico.getColumnModel().getColumn(2).setPreferredWidth(100);
        tblHistorico.getColumnModel().getColumn(3).setPreferredWidth(380);
        tblHistorico.getColumnModel().getColumn(4).setPreferredWidth(110);
        tblHistorico.getColumnModel().getColumn(5).setPreferredWidth(110);
    }
}