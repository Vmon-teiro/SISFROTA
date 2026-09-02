package view;

import controller.ManutencaoController;
import dao.EmbarcacaoDAO;
import dao.ManutencaoDAO;
import model.Embarcacao;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Date;

public class TelaManutencoes extends JFrame {

    // Paleta de Cores do Sistema
    private static final Color BG_APP = new Color(241, 245, 249);
    private static final Color HEADER_DARK = new Color(15, 23, 42);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color CARD_BORDER = new Color(226, 232, 240);
    private static final Color TEXT_TITLE = new Color(15, 23, 42);
    private static final Color TEXT_MUTED = new Color(100, 116, 139);
    private static final Color PRIMARY_BLUE = new Color(37, 99, 235);
    private static final Color PRIMARY_GREEN = new Color(16, 185, 129);
    private static final Color DANGER_RED = new Color(225, 29, 72);
    private static final Color WARNING_ORANGE = new Color(217, 119, 6);

    private final ManutencaoController controller = new ManutencaoController();
    private JTable tblIncidentes;
    private JTable tblManutencoes;
    private DefaultTableModel modelIncidentes;
    private DefaultTableModel modelManutencoes;

    // Componentes de detalhes
    private JTextArea txtDetalheIncidente;
    private JTextArea txtDetalheOS;

    // Formulário
    private JComboBox<Embarcacao> cbEmbarcacoes;
    private JComboBox<String> cbTipo;
    private JTextArea txtDescricao;
    private JTextField txtHorimetro;
    private JSpinner spDataAgendamento;
    private JTextField txtCusto;

    public TelaManutencoes() {
        setTitle("Gestão de Manutenções e Incidentes");
        setSize(1050, 750);
        setMinimumSize(new Dimension(900, 600));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponentes();
        carregarDados();
    }

    private void initComponentes() {
        setLayout(new BorderLayout());

        // Header Superior
        add(criarHeader(), BorderLayout.NORTH);

        // Abas da Aplicação
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabbedPane.addTab("Central de Incidentes (Operadores)", criarPainelIncidentes());
        tabbedPane.addTab("Ordens de Serviço e Agendamentos", criarPainelManutencoes());

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

        JLabel lblTitle = new JLabel("GESTÃO DE MANUTENÇÕES E INCIDENTES");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Controle de chamados operacionais, preventivas e ordens de serviço");
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

    private JPanel criarPainelIncidentes() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBackground(BG_APP);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        modelIncidentes = new DefaultTableModel(new String[]{"ID", "Embarcação", "Data Incidente", "Descrição", "Gravidade", "Status", "ID_EMB"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblIncidentes = new JTable(modelIncidentes);
        estilarTabela(tblIncidentes);

        // Oculta coluna ID_EMB
        tblIncidentes.getColumnModel().getColumn(6).setMinWidth(0);
        tblIncidentes.getColumnModel().getColumn(6).setMaxWidth(0);
        tblIncidentes.getColumnModel().getColumn(6).setWidth(0);

        JScrollPane scrollTabela = new JScrollPane(tblIncidentes);
        scrollTabela.setBorder(BorderFactory.createLineBorder(CARD_BORDER));

        // Detalhes do Incidente
        JPanel pnlDetalhes = new JPanel(new BorderLayout(0, 6));
        pnlDetalhes.setBackground(CARD_BG);
        pnlDetalhes.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        JLabel lblDetTitulo = new JLabel("Detalhes do Incidente Selecionado");
        lblDetTitulo.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblDetTitulo.setForeground(TEXT_TITLE);
        pnlDetalhes.add(lblDetTitulo, BorderLayout.NORTH);

        txtDetalheIncidente = new JTextArea(4, 50);
        txtDetalheIncidente.setEditable(false);
        txtDetalheIncidente.setLineWrap(true);
        txtDetalheIncidente.setWrapStyleWord(true);
        txtDetalheIncidente.setBackground(new Color(248, 250, 252));
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
                } else {
                    txtDetalheIncidente.setText("");
                }
            }
        });

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollTabela, pnlDetalhes);
        splitPane.setResizeWeight(0.65);
        splitPane.setBorder(null);

        panel.add(splitPane, BorderLayout.CENTER);

        JPanel pnlAcoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlAcoes.setBackground(BG_APP);

        JButton btnGerarOS = criarBotaoArredondado("Aprovar e Gerar OS", PRIMARY_BLUE);
        btnGerarOS.addActionListener(e -> aprovarEGerarOS());

        pnlAcoes.add(btnGerarOS);
        panel.add(pnlAcoes, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel criarPainelManutencoes() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBackground(BG_APP);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        // Formulário
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBackground(CARD_BG);
        pnlForm.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; pnlForm.add(criarLabel("Embarcação:"), gbc);
        gbc.gridx = 1; cbEmbarcacoes = new JComboBox<>(); pnlForm.add(cbEmbarcacoes, gbc);

        gbc.gridx = 2; gbc.gridy = 0; pnlForm.add(criarLabel("Tipo:"), gbc);
        gbc.gridx = 3; cbTipo = new JComboBox<>(new String[]{"PREVENTIVA", "CORRETIVA"}); pnlForm.add(cbTipo, gbc);

        gbc.gridx = 0; gbc.gridy = 1; pnlForm.add(criarLabel("Descrição Serviço:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        txtDescricao = new JTextArea(2, 20);
        txtDescricao.setLineWrap(true);
        txtDescricao.setWrapStyleWord(true);
        pnlForm.add(new JScrollPane(txtDescricao), gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 2; pnlForm.add(criarLabel("Horímetro Meta:"), gbc);
        gbc.gridx = 1; txtHorimetro = new JTextField(); pnlForm.add(txtHorimetro, gbc);

        gbc.gridx = 2; gbc.gridy = 2; pnlForm.add(criarLabel("Data Agendada:"), gbc);
        gbc.gridx = 3;
        spDataAgendamento = new JSpinner(new SpinnerDateModel());
        spDataAgendamento.setEditor(new JSpinner.DateEditor(spDataAgendamento, "dd/MM/yyyy"));
        pnlForm.add(spDataAgendamento, gbc);

        gbc.gridx = 0; gbc.gridy = 3; pnlForm.add(criarLabel("Custo Estimado (R$):"), gbc);
        gbc.gridx = 1; txtCusto = new JTextField("0.00"); pnlForm.add(txtCusto, gbc);

        gbc.gridx = 3; gbc.gridy = 3;
        JButton btnSalvar = criarBotaoArredondado("Encaminhar para Técnico", PRIMARY_GREEN);
        btnSalvar.addActionListener(e -> salvarManutencao());
        pnlForm.add(btnSalvar, gbc);

        panel.add(pnlForm, BorderLayout.NORTH);

        // Tabela
        modelManutencoes = new DefaultTableModel(new String[]{"ID", "Embarcação", "Tipo", "Descrição", "Horímetro", "Data Agendada", "Custo (R$)", "Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblManutencoes = new JTable(modelManutencoes);
        estilarTabela(tblManutencoes);

        // Menu de Contexto (Botão Direito) para Excluir do Histórico
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem itemExcluir = new JMenuItem("Excluir do Histórico");
        itemExcluir.setForeground(DANGER_RED);
        itemExcluir.setFont(new Font("Segoe UI", Font.BOLD, 12));
        popupMenu.add(itemExcluir);

        itemExcluir.addActionListener(e -> excluirOSCancelada());

        tblManutencoes.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                verificarMenuPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                verificarMenuPopup(e);
            }

            private void verificarMenuPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = tblManutencoes.rowAtPoint(e.getPoint());
                    if (row != -1) {
                        tblManutencoes.setRowSelectionInterval(row, row);
                        String status = String.valueOf(modelManutencoes.getValueAt(row, 7));
                        if ("CANCELADA".equalsIgnoreCase(status)) {
                            popupMenu.show(e.getComponent(), e.getX(), e.getY());
                        }
                    }
                }
            }
        });

        JScrollPane scrollTabela = new JScrollPane(tblManutencoes);
        scrollTabela.setBorder(BorderFactory.createLineBorder(CARD_BORDER));

        // Detalhes da OS
        JPanel pnlDetalhesOS = new JPanel(new BorderLayout(0, 6));
        pnlDetalhesOS.setBackground(CARD_BG);
        pnlDetalhesOS.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        JLabel lblDetOSTitulo = new JLabel("Detalhes da Ordem de Serviço Selecionada");
        lblDetOSTitulo.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblDetOSTitulo.setForeground(TEXT_TITLE);
        pnlDetalhesOS.add(lblDetOSTitulo, BorderLayout.NORTH);

        txtDetalheOS = new JTextArea(4, 50);
        txtDetalheOS.setEditable(false);
        txtDetalheOS.setLineWrap(true);
        txtDetalheOS.setWrapStyleWord(true);
        txtDetalheOS.setBackground(new Color(248, 250, 252));
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
                } else {
                    txtDetalheOS.setText("");
                }
            }
        });

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollTabela, pnlDetalhesOS);
        splitPane.setResizeWeight(0.55);
        splitPane.setBorder(null);

        panel.add(splitPane, BorderLayout.CENTER);

        // Barra de Ações Inferior
        JPanel pnlStatus = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlStatus.setBackground(BG_APP);

        JButton btnEmAndamento = criarBotaoArredondado("Marcar 'EM ANDAMENTO'", WARNING_ORANGE);
        JButton btnCancelar = criarBotaoArredondado("Cancelar OS", DANGER_RED);

        btnEmAndamento.addActionListener(e -> alterarStatusOS("EM_ANDAMENTO"));
        btnCancelar.addActionListener(e -> alterarStatusOS("CANCELADA"));

        pnlStatus.add(btnEmAndamento);
        pnlStatus.add(btnCancelar);
        panel.add(pnlStatus, BorderLayout.SOUTH);

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
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        return btn;
    }

    private JLabel criarLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setForeground(TEXT_TITLE);
        return label;
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
            JOptionPane.showMessageDialog(this, "Selecione um incidente na tabela para aprovação.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idIncidente = (int) modelIncidentes.getValueAt(row, 0);
        String desc = (String) modelIncidentes.getValueAt(row, 3);
        int idEmbarcacao = (int) modelIncidentes.getValueAt(row, 6);

        Date dataHoje = new Date(System.currentTimeMillis());
        boolean ok = controller.converterIncidenteEmOS(idIncidente, idEmbarcacao, desc, dataHoje);

        if (ok) {
            JOptionPane.showMessageDialog(this, "Ordem de Serviço gerada e encaminhada ao Técnico!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            carregarDados();
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao converter incidente em OS.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void salvarManutencao() {
        try {
            Embarcacao emb = (Embarcacao) cbEmbarcacoes.getSelectedItem();
            if (emb == null) {
                JOptionPane.showMessageDialog(this, "Selecione uma embarcação válida.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String desc = txtDescricao.getText().trim();
            if (desc.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha a descrição do serviço.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String tipo = (String) cbTipo.getSelectedItem();
            Integer horimetro = txtHorimetro.getText().trim().isEmpty() ? null : Integer.parseInt(txtHorimetro.getText().trim());
            java.util.Date d = (java.util.Date) spDataAgendamento.getValue();
            Date dataAgendada = new Date(d.getTime());

            String textoCusto = txtCusto.getText().trim().replace(",", ".");
            double custo = textoCusto.isEmpty() ? 0.0 : Double.parseDouble(textoCusto);

            String res = controller.criarOrdemServico(emb.getId(), tipo, desc, horimetro, dataAgendada, custo);
            if ("OK".equals(res)) {
                JOptionPane.showMessageDialog(this, "OS registrada e enviada para o painel do Técnico!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                txtDescricao.setText("");
                txtHorimetro.setText("");
                txtCusto.setText("0.00");
                carregarDados();
            } else {
                JOptionPane.showMessageDialog(this, res, "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Verifique os valores numéricos inseridos (Horímetro e Custo).", "Erro de Formatação", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar manutenção: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void alterarStatusOS(String novoStatus) {
        int row = tblManutencoes.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma manutenção na tabela.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) modelManutencoes.getValueAt(row, 0);
        if (controller.atualizarStatusOS(id, novoStatus)) {
            JOptionPane.showMessageDialog(this, "Status atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            carregarDados();
        } else {
            JOptionPane.showMessageDialog(this, "Não foi possível atualizar o status.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluirOSCancelada() {
        int row = tblManutencoes.getSelectedRow();
        if (row == -1) return;

        int id = (int) modelManutencoes.getValueAt(row, 0);
        String status = String.valueOf(modelManutencoes.getValueAt(row, 7));

        if (!"CANCELADA".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(this, "Apenas ordens com status CANCELADA podem ser excluídas.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacao = JOptionPane.showConfirmDialog(
            this,
            "Deseja realmente remover esta OS cancelada do histórico?",
            "Confirmar Exclusão",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirmacao == JOptionPane.YES_OPTION) {
            boolean ok = new ManutencaoDAO().excluir(id);
            if (ok) {
                JOptionPane.showMessageDialog(this, "OS removida do histórico com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarDados();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao excluir a OS do banco de dados.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}