package view;

import controller.TripulanteController;
import model.Tripulante;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.util.List;

public class TelaGerenciarTripulacao extends JFrame {

    // Paleta de Cores do Sistema
    private static final Color BG_APP            = new Color(241, 245, 249); // slate-100
    private static final Color HEADER_DARK       = new Color(15, 23, 42);    // slate-900
    private static final Color CARD_BG           = Color.WHITE;
    private static final Color CARD_BORDER       = new Color(226, 232, 240); // slate-200
    private static final Color TEXT_TITLE        = new Color(15, 23, 42);    // slate-900
    private static final Color TEXT_MUTED        = new Color(100, 116, 139); // slate-500
    private static final Color PRIMARY_GREEN     = new Color(16, 185, 129);  // emerald-500
    private static final Color DANGER_RED        = new Color(225, 29, 72);   // rose-600
    private static final Color SECONDARY_GRAY    = new Color(100, 116, 139); // slate-500

    private final TripulanteController controller = new TripulanteController();
    private JTable tblTripulantes;
    private DefaultTableModel tableModel;

    private JTextField txtId, txtNome, txtCpf, txtCir;
    private JComboBox<String> cbCategoria, cbStatus;
    private JSpinner spDataVencimento;

    public TelaGerenciarTripulacao() {
        setTitle("Gerenciamento de Tripulação");
        setSize(950, 650);
        setMinimumSize(new Dimension(850, 550));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponentes();
        carregarTabela();
    }

    private void initComponentes() {
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(BG_APP);

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

        JLabel lblTitle = new JLabel("GERENCIAMENTO DE TRIPULAÇÃO");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Cadastro, habilitações (CIR) e controle de disponibilidade dos tripulantes");
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
        wrapper.setBorder(BorderFactory.createEmptyBorder(12, 24, 8, 24));

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtId = new JTextField(); 
        txtId.setEditable(false);
        txtId.setBackground(new Color(241, 245, 249));

        txtNome = new JTextField(15);
        txtCpf = new JTextField(12);
        txtCir = new JTextField(12);

        cbCategoria = new JComboBox<>(new String[]{
            "PILOTO_FLUVIAL", "CONDUTOR_FLUVIAL", "ARRAIS_AMADOR", "MESTRE_AMADOR", "CAPITAO_AMADOR"
        });
        cbStatus = new JComboBox<>(new String[]{"DISPONIVEL", "EM_VIAGEM", "INATIVO"});

        spDataVencimento = new JSpinner(new SpinnerDateModel());
        spDataVencimento.setEditor(new JSpinner.DateEditor(spDataVencimento, "dd/MM/yyyy"));

        // Linha 0
        gbc.gridx = 0; gbc.gridy = 0; panel.add(criarLabel("ID:"), gbc);
        gbc.gridx = 1; panel.add(txtId, gbc);
        gbc.gridx = 2; panel.add(criarLabel("Nome Completo:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1.0; panel.add(txtNome, gbc); gbc.weightx = 0.0;

        // Linha 1
        gbc.gridx = 0; gbc.gridy = 1; panel.add(criarLabel("CPF:"), gbc);
        gbc.gridx = 1; panel.add(txtCpf, gbc);
        gbc.gridx = 2; panel.add(criarLabel("Categoria Habilitação:"), gbc);
        gbc.gridx = 3; panel.add(cbCategoria, gbc);

        // Linha 2
        gbc.gridx = 0; gbc.gridy = 2; panel.add(criarLabel("Nº Registro CIR:"), gbc);
        gbc.gridx = 1; panel.add(txtCir, gbc);
        gbc.gridx = 2; panel.add(criarLabel("Vencimento CIR:"), gbc);
        gbc.gridx = 3; panel.add(spDataVencimento, gbc);

        // Linha 3
        gbc.gridx = 0; gbc.gridy = 3; panel.add(criarLabel("Status:"), gbc);
        gbc.gridx = 1; panel.add(cbStatus, gbc);

        wrapper.add(panel, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel criarContainerTabela() {
        tableModel = new DefaultTableModel(new String[]{
            "ID", "Nome", "CPF", "Categoria", "Nº CIR", "Vencimento CIR", "Status"
        }, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tblTripulantes = new JTable(tableModel);
        estilarTabela(tblTripulantes);

        tblTripulantes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                carregarFormularioDaTabela();
            }
        });

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG_APP);
        wrapper.setBorder(BorderFactory.createEmptyBorder(0, 24, 12, 24));

        JScrollPane scroll = new JScrollPane(tblTripulantes);
        scroll.setBorder(BorderFactory.createLineBorder(CARD_BORDER));
        scroll.getViewport().setBackground(Color.WHITE);

        wrapper.add(scroll, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel criarBarraBotoes() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        panel.setBackground(BG_APP);

        JButton btnLimpar = criarBotaoArredondado("Limpar Campos", SECONDARY_GRAY);
        JButton btnExcluir = criarBotaoArredondado("Excluir", DANGER_RED);
        JButton btnSalvar = criarBotaoArredondado("Salvar Tripulante", PRIMARY_GREEN);

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
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
    }

    private void carregarTabela() {
        tableModel.setRowCount(0);
        List<Tripulante> lista = controller.listarTripulantes();
        for (Tripulante t : lista) {
            tableModel.addRow(new Object[]{
                t.getId(), t.getNome(), t.getCpf(), t.getCategoriaHabilitacao(),
                t.getNumeroRegistroCir(), t.getDataVencimentoCir(), t.getStatus()
            });
        }
    }

    private void carregarFormularioDaTabela() {
        int row = tblTripulantes.getSelectedRow();
        if (row != -1) {
            txtId.setText(tableModel.getValueAt(row, 0).toString());
            txtNome.setText(tableModel.getValueAt(row, 1).toString());
            txtCpf.setText(tableModel.getValueAt(row, 2).toString());
            cbCategoria.setSelectedItem(tableModel.getValueAt(row, 3).toString());
            txtCir.setText(tableModel.getValueAt(row, 4).toString());
            
            java.util.Date dataVenc = (java.util.Date) tableModel.getValueAt(row, 5);
            spDataVencimento.setValue(dataVenc);
            
            cbStatus.setSelectedItem(tableModel.getValueAt(row, 6).toString());
        }
    }

    private void salvar() {
        try {
            Integer id = txtId.getText().isEmpty() ? null : Integer.parseInt(txtId.getText());
            String nome = txtNome.getText();
            String cpf = txtCpf.getText();
            String categoria = (String) cbCategoria.getSelectedItem();
            String cir = txtCir.getText();
            java.util.Date d = (java.util.Date) spDataVencimento.getValue();
            Date dataVencimento = new Date(d.getTime());
            String status = (String) cbStatus.getSelectedItem();

            String res = controller.salvarOuAtualizar(id, nome, cpf, categoria, cir, dataVencimento, status);
            if ("OK".equals(res)) {
                JOptionPane.showMessageDialog(this, "Tripulante salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                limparCampos();
                carregarTabela();
            } else {
                JOptionPane.showMessageDialog(this, res, "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Verifique os dados informados.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluir() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione um tripulante na tabela.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Confirma a exclusão do tripulante?", "Atenção", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int id = Integer.parseInt(txtId.getText());
            if (controller.excluirTripulante(id)) {
                JOptionPane.showMessageDialog(this, "Tripulante excluído com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                limparCampos();
                carregarTabela();
            } else {
                JOptionPane.showMessageDialog(this, "Não foi possível excluir (o tripulante pode estar vinculado a viagens ativas).", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limparCampos() {
        txtId.setText("");
        txtNome.setText("");
        txtCpf.setText("");
        txtCir.setText("");
        cbCategoria.setSelectedIndex(0);
        cbStatus.setSelectedIndex(0);
        spDataVencimento.setValue(new java.util.Date());
        tblTripulantes.clearSelection();
    }
}