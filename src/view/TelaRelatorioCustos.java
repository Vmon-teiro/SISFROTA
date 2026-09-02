package view;

import controller.RelatorioController;
import controller.RelatorioController.CustoEmbarcacaoDTO;
import service.EmailService;
import service.GeradorPDFService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TelaRelatorioCustos extends JFrame {

    // Paleta Visual
    private static final Color BG_APP            = new Color(241, 245, 249); // slate-100
    private static final Color HEADER_DARK       = new Color(15, 23, 42);    // slate-900
    private static final Color CARD_BG           = Color.WHITE;
    private static final Color CARD_BORDER       = new Color(226, 232, 240); // slate-200
    private static final Color TEXT_TITLE        = new Color(15, 23, 42);    // slate-900
    private static final Color TEXT_MUTED        = new Color(100, 116, 139); // slate-500
    private static final Color PRIMARY_BLUE      = new Color(37, 99, 235);   // blue-600
    private static final Color PRIMARY_GREEN     = new Color(16, 185, 129);  // emerald-500
    private static final Color WARNING_ORANGE    = new Color(217, 119, 6);   // amber-600

    private final RelatorioController controller = new RelatorioController();
    private JTable tblCustos;
    private DefaultTableModel tableModel;
    private List<CustoEmbarcacaoDTO> dadosCarregados = new ArrayList<>();

    // Cards de Indicadores
    private JLabel lblTotalGeral;
    private JLabel lblTotalManutencao;
    private JLabel lblTotalAbastecimento;

    public TelaRelatorioCustos() {
        setTitle("Relatório e Indicadores de Custos");
        setSize(950, 620);
        setMinimumSize(new Dimension(850, 500));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponentes();
        carregarDados();
    }

    private void initComponentes() {
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(BG_APP);

        JPanel pnlTopo = new JPanel();
        pnlTopo.setLayout(new BoxLayout(pnlTopo, BoxLayout.Y_AXIS));
        pnlTopo.setBackground(BG_APP);
        pnlTopo.add(criarHeader());
        pnlTopo.add(criarPainelKPIs());

        add(pnlTopo, BorderLayout.NORTH);
        add(criarContainerTabela(), BorderLayout.CENTER);
        add(criarBarraAcoes(), BorderLayout.SOUTH);
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

        JLabel lblTitle = new JLabel("RELATÓRIO FINANCEIRO E OPERACIONAL");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Consolidado de despesas por embarcação da frota");
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

    private JPanel criarPainelKPIs() {
        JPanel container = new JPanel(new GridLayout(1, 3, 16, 0));
        container.setBackground(BG_APP);
        container.setBorder(BorderFactory.createEmptyBorder(16, 24, 12, 24));

        lblTotalGeral = new JLabel("R$ 0,00");
        lblTotalManutencao = new JLabel("R$ 0,00");
        lblTotalAbastecimento = new JLabel("R$ 0,00");

        container.add(criarCardKPI("CUSTO TOTAL CONSOLIDADO", lblTotalGeral, PRIMARY_BLUE));
        container.add(criarCardKPI("TOTAL EM MANUTENÇÕES", lblTotalManutencao, WARNING_ORANGE));
        container.add(criarCardKPI("TOTAL EM ABASTECIMENTOS", lblTotalAbastecimento, PRIMARY_GREEN));

        return container;
    }

    private JPanel criarCardKPI(String titulo, JLabel lblValor, Color corDestaque) {
        JPanel card = new JPanel(new BorderLayout(0, 4)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.setColor(CARD_BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        JLabel lblTit = new JLabel(titulo);
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblTit.setForeground(TEXT_MUTED);

        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblValor.setForeground(corDestaque);

        card.add(lblTit, BorderLayout.NORTH);
        card.add(lblValor, BorderLayout.CENTER);
        return card;
    }

    private JPanel criarContainerTabela() {
        tableModel = new DefaultTableModel(new String[]{"ID", "Embarcação", "Manutenção (R$)", "Abastecimento (R$)", "Custo Total (R$)"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblCustos = new JTable(tableModel);
        tblCustos.setRowHeight(32);
        tblCustos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblCustos.setShowGrid(false);
        tblCustos.setIntercellSpacing(new Dimension(0, 0));
        tblCustos.setSelectionBackground(new Color(224, 231, 255));
        tblCustos.setSelectionForeground(TEXT_TITLE);

        // Estilização do Cabeçalho da Tabela
        tblCustos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tblCustos.getTableHeader().setBackground(new Color(248, 250, 252));
        tblCustos.getTableHeader().setForeground(TEXT_MUTED);
        tblCustos.getTableHeader().setPreferredSize(new Dimension(0, 36));

        // Alinhamento das colunas
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tblCustos.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tblCustos.getColumnModel().getColumn(0).setPreferredWidth(50);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        tblCustos.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
        tblCustos.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
        tblCustos.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);

        JPanel pnlWrapper = new JPanel(new BorderLayout());
        pnlWrapper.setBackground(BG_APP);
        pnlWrapper.setBorder(BorderFactory.createEmptyBorder(0, 24, 16, 24));

        JScrollPane scroll = new JScrollPane(tblCustos);
        scroll.setBorder(BorderFactory.createLineBorder(CARD_BORDER));
        scroll.getViewport().setBackground(Color.WHITE);

        pnlWrapper.add(scroll, BorderLayout.CENTER);
        return pnlWrapper;
    }

    private JPanel criarBarraAcoes() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 16));
        panel.setBackground(BG_APP);

        JButton btnBaixarPDF = criarBotaoArredondado("Gerar e Baixar PDF", PRIMARY_BLUE);
        JButton btnEnviarEmail = criarBotaoArredondado("Enviar por E-mail", PRIMARY_GREEN);

        btnBaixarPDF.addActionListener(e -> baixarPDF());
        btnEnviarEmail.addActionListener(e -> enviarEmail());

        panel.add(btnBaixarPDF);
        panel.add(btnEnviarEmail);
        return panel;
    }

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
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        return btn;
    }

    private void carregarDados() {
        tableModel.setRowCount(0);
        dadosCarregados = controller.obterConsolidadoCustos();
        NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));

        double somaGeral = 0;
        double somaManutencao = 0;
        double somaAbastecimento = 0;

        for (CustoEmbarcacaoDTO dto : dadosCarregados) {
            tableModel.addRow(new Object[]{
                dto.getId(),
                dto.getNomeEmbarcacao(),
                nf.format(dto.getTotalManutencao()),
                nf.format(dto.getTotalAbastecimento()),
                nf.format(dto.getCustoTotal())
            });

            somaManutencao += dto.getTotalManutencao();
            somaAbastecimento += dto.getTotalAbastecimento();
            somaGeral += dto.getCustoTotal();
        }

        lblTotalGeral.setText(nf.format(somaGeral));
        lblTotalManutencao.setText(nf.format(somaManutencao));
        lblTotalAbastecimento.setText(nf.format(somaAbastecimento));
    }

    private List<Object[]> converterParaListaObject(List<CustoEmbarcacaoDTO> dtos) {
        List<Object[]> lista = new ArrayList<>();
        for (CustoEmbarcacaoDTO dto : dtos) {
            lista.add(new Object[]{
                dto.getId(),
                dto.getNomeEmbarcacao(),
                dto.getTotalManutencao(),
                dto.getTotalAbastecimento(),
                dto.getCustoTotal()
            });
        }
        return lista;
    }

    private void baixarPDF() {
        try {
            List<Object[]> listaDados = converterParaListaObject(dadosCarregados);
            String caminhoPDF = GeradorPDFService.gerarRelatorioCustos(listaDados);

            JOptionPane.showMessageDialog(this,
                    "PDF gerado com sucesso!\nSalvo em: " + caminhoPDF,
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);

            Desktop.getDesktop().open(new File(caminhoPDF));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao gerar PDF: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void enviarEmail() {
        String emailDestino = JOptionPane.showInputDialog(this,
                "Informe o e-mail do destinatário:",
                "Enviar Relatório", JOptionPane.QUESTION_MESSAGE);

        if (emailDestino != null && !emailDestino.trim().isEmpty()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            // SwingWorker para processamento em segundo plano
            SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    List<Object[]> listaDados = converterParaListaObject(dadosCarregados);
                    String caminhoPDF = GeradorPDFService.gerarRelatorioCustos(listaDados);
                    return EmailService.enviarRelatorioPorEmail(emailDestino.trim(), caminhoPDF);
                }

                @Override
                protected void done() {
                    setCursor(Cursor.getDefaultCursor());
                    try {
                        boolean enviado = get();
                        if (enviado) {
                            JOptionPane.showMessageDialog(TelaRelatorioCustos.this,
                                    "E-mail enviado com sucesso para " + emailDestino.trim(),
                                    "Confirmação", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(TelaRelatorioCustos.this,
                                    "Falha ao enviar e-mail. Verifique as credenciais SMTP no EmailService.",
                                    "Erro de Envio", JOptionPane.WARNING_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(TelaRelatorioCustos.this,
                                "Erro na operação: " + ex.getMessage(),
                                "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        }
    }
}