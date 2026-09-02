package view;

import controller.EmbarcacaoController;
import model.Embarcacao;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TelaGerenciarEmbarcacoes extends JFrame {

    // Paleta de Cores do Sistema
    private static final Color BG_APP = new Color(241, 245, 249);
    private static final Color HEADER_DARK = new Color(15, 23, 42);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color CARD_BORDER = new Color(226, 232, 240);
    private static final Color TEXT_TITLE = new Color(15, 23, 42);
    private static final Color TEXT_MUTED = new Color(100, 116, 139);

    private static final Color PRIMARY_GREEN = new Color(16, 185, 129);
    private static final Color DANGER_RED = new Color(225, 29, 72);
    private static final Color SECONDARY_GRAY = new Color(100, 116, 139);

    private final EmbarcacaoController controller = new EmbarcacaoController();
    private JTable tblEmbarcacoes;
    private DefaultTableModel tableModel;

    private JTextField txtId, txtNome, txtModelo, txtCapPassageiros, txtCapCarga, txtAno, txtHorimetro;
    private JComboBox<String> cbStatus;

    public TelaGerenciarEmbarcacoes() {
        setTitle("Gerenciamento de Embarcações e Frota");
        setSize(980, 680);
        setMinimumSize(new Dimension(850, 550));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponentes();
        carregarTabela();
    }

    private void initComponentes() {
        setLayout(new BorderLayout());

        JPanel pnlTopo = new JPanel();
        pnlTopo.setLayout(new BoxLayout(pnlTopo, BoxLayout.Y_AXIS));
        pnlTopo.setBackground(BG_APP);
        pnlTopo.add(criarHeader());
        pnlTopo.add(criarFormulario());

        add(pnlTopo, BorderLayout.NORTH);
        add(criarContainerTabela(), BorderLayout.CENTER);
        add(criarBarraBotoes(), BorderLayout.SOUTH);
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

        JLabel lblTitle = new JLabel("GERENCIAMENTO DE EMBARCAÇÕES E FROTA");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Cadastro, edição e controle operacional de embarcações");
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

    private JPanel criarFormulario() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG_APP);
        wrapper.setBorder(BorderFactory.createEmptyBorder(12, 16, 6, 16));

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtId = new JTextField(); txtId.setEditable(false); txtId.setFocusable(false);
        txtNome = new JTextField(15);
        txtModelo = new JTextField(15);
        txtCapPassageiros = new JTextField("0");
        txtCapCarga = new JTextField("0.00");
        txtAno = new JTextField("2024");
        txtHorimetro = new JTextField("0");
        cbStatus = new JComboBox<>(new String[]{"ATIVA", "EM_MANUTENCAO", "INATIVA"});

        // Linha 0
        gbc.gridx = 0; gbc.gridy = 0; panel.add(criarLabel("ID:"), gbc);
        gbc.gridx = 1; panel.add(txtId, gbc);
        gbc.gridx = 2; panel.add(criarLabel("Nome:"), gbc);
        gbc.gridx = 3; panel.add(txtNome, gbc);

        // Linha 1
        gbc.gridx = 0; gbc.gridy = 1; panel.add(criarLabel("Modelo:"), gbc);
        gbc.gridx = 1; panel.add(txtModelo, gbc);
        gbc.gridx = 2; panel.add(criarLabel("Cap. Passageiros:"), gbc);
        gbc.gridx = 3; panel.add(txtCapPassageiros, gbc);

        // Linha 2
        gbc.gridx = 0; gbc.gridy = 2; panel.add(criarLabel("Cap. Carga (Ton):"), gbc);
        gbc.gridx = 1; panel.add(txtCapCarga, gbc);
        gbc.gridx = 2; panel.add(criarLabel("Ano Fabricação:"), gbc);
        gbc.gridx = 3; panel.add(txtAno, gbc);

        // Linha 3
        gbc.gridx = 0; gbc.gridy = 3; panel.add(criarLabel("Horímetro (Horas):"), gbc);
        gbc.gridx = 1; panel.add(txtHorimetro, gbc);
        gbc.gridx = 2; panel.add(criarLabel("Status:"), gbc);
        gbc.gridx = 3; panel.add(cbStatus, gbc);

        wrapper.add(panel, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel criarContainerTabela() {
        tableModel = new DefaultTableModel(new String[]{
                "ID", "Nome", "Modelo", "Passageiros", "Carga (Ton)", "Ano", "Horímetro (h)", "Status"
        }, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tblEmbarcacoes = new JTable(tableModel);
        estilarTabela(tblEmbarcacoes);

        tblEmbarcacoes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                carregarFormularioDaTabela();
            }
        });

        JPanel pnlWrapper = new JPanel(new BorderLayout());
        pnlWrapper.setBackground(BG_APP);
        pnlWrapper.setBorder(BorderFactory.createEmptyBorder(6, 16, 12, 16));

        JScrollPane scroll = new JScrollPane(tblEmbarcacoes);
        scroll.setBorder(BorderFactory.createLineBorder(CARD_BORDER));
        scroll.getViewport().setBackground(Color.WHITE);

        pnlWrapper.add(scroll, BorderLayout.CENTER);
        return pnlWrapper;
    }

    private JPanel criarBarraBotoes() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        panel.setBackground(BG_APP);

        JButton btnSalvar = criarBotaoArredondado("Salvar Embarcação", PRIMARY_GREEN);
        JButton btnLimpar = criarBotaoArredondado("Limpar Campos", SECONDARY_GRAY);
        JButton btnExcluir = criarBotaoArredondado("Excluir", DANGER_RED);

        btnSalvar.addActionListener(e -> salvar());
        btnLimpar.addActionListener(e -> limparCampos());
        btnExcluir.addActionListener(e -> excluir());

        panel.add(btnLimpar);
        panel.add(btnExcluir);
        panel.add(btnSalvar);

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
        
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);

        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // ID
        table.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);  // Passageiros
        table.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);  // Carga
        table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer); // Ano
        table.getColumnModel().getColumn(6).setCellRenderer(rightRenderer);  // Horimetro
        table.getColumnModel().getColumn(7).setCellRenderer(centerRenderer); // Status
    }

    private void carregarTabela() {
        tableModel.setRowCount(0);
        List<Embarcacao> lista = controller.listarEmbarcacoes();
        for (Embarcacao e : lista) {
            tableModel.addRow(new Object[]{
                    e.getId(), e.getNome(), e.getModelo(), e.getCapacidadePassageiros(),
                    e.getCapacidadeCargaTon(), e.getAnoFabricacao(), e.getHorimetroHoras(), e.getStatus()
            });
        }
    }

    private void carregarFormularioDaTabela() {
        int row = tblEmbarcacoes.getSelectedRow();
        if (row != -1) {
            txtId.setText(tableModel.getValueAt(row, 0).toString());
            txtNome.setText(tableModel.getValueAt(row, 1).toString());
            txtModelo.setText(tableModel.getValueAt(row, 2).toString());
            txtCapPassageiros.setText(tableModel.getValueAt(row, 3).toString());
            txtCapCarga.setText(tableModel.getValueAt(row, 4).toString());
            txtAno.setText(tableModel.getValueAt(row, 5).toString());
            txtHorimetro.setText(tableModel.getValueAt(row, 6).toString());
            cbStatus.setSelectedItem(tableModel.getValueAt(row, 7).toString());
        }
    }

    private void salvar() {
        try {
            String nome = txtNome.getText().trim();
            String modelo = txtModelo.getText().trim();

            if (nome.isEmpty() || modelo.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha os campos obrigatórios (Nome e Modelo).", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Integer id = txtId.getText().isEmpty() ? null : Integer.parseInt(txtId.getText());
            int capPass = Integer.parseInt(txtCapPassageiros.getText().trim());
            double capCarga = Double.parseDouble(txtCapCarga.getText().replace(",", ".").trim());
            int ano = Integer.parseInt(txtAno.getText().trim());
            int horimetro = Integer.parseInt(txtHorimetro.getText().trim());
            String status = (String) cbStatus.getSelectedItem();

            String res = controller.salvarOuAtualizar(id, nome, modelo, capPass, capCarga, ano, horimetro, status);
            if ("OK".equals(res)) {
                JOptionPane.showMessageDialog(this, "Embarcação salva com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                limparCampos();
                carregarTabela();
            } else {
                JOptionPane.showMessageDialog(this, res, "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Verifique os valores numéricos digitados (Capacidade, Ano e Horímetro).", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar embarcação: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluir() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione uma embarcação na tabela para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Confirma a exclusão da embarcação?", "Atenção", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int id = Integer.parseInt(txtId.getText());
                if (controller.excluirEmbarcacao(id)) {
                    JOptionPane.showMessageDialog(this, "Embarcação excluída com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    limparCampos();
                    carregarTabela();
                } else {
                    JOptionPane.showMessageDialog(this, "Não foi possível excluir (pode haver vínculos ativos no banco de dados).", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao excluir: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limparCampos() {
        txtId.setText("");
        txtNome.setText("");
        txtModelo.setText("");
        txtCapPassageiros.setText("0");
        txtCapCarga.setText("0.00");
        txtAno.setText("2024");
        txtHorimetro.setText("0");
        cbStatus.setSelectedIndex(0);
        tblEmbarcacoes.clearSelection();
    }
}