package view;

import controller.*;
import dao.*;
import dto.ConsultaHorarioDTO;
import model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class TelaDashboardOperador extends JFrame {

    // Paleta Visual
    private static final Color BG_APP            = new Color(241, 245, 249); // slate-100
    private static final Color HEADER_DARK       = new Color(15, 23, 42);    // slate-900
    private static final Color CARD_BG           = Color.WHITE;
    private static final Color CARD_BORDER       = new Color(226, 232, 240); // slate-200
    private static final Color TEXT_TITLE        = new Color(15, 23, 42);    // slate-900
    private static final Color TEXT_MUTED        = new Color(100, 116, 139); // slate-500
    private static final Color PRIMARY_BLUE      = new Color(37, 99, 235);   // blue-600
    private static final Color PRIMARY_GREEN     = new Color(16, 185, 129);  // emerald-500
    private static final Color DANGER_RED        = new Color(225, 29, 72);   // rose-600
    private static final Color SECONDARY_GRAY    = new Color(100, 116, 139); // slate-500

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Controllers e DAOs
    private final ViagemController viagemController = new ViagemController();
    private final ViagemDAO viagemDAO = new ViagemDAO();
    private final RotaDAO rotaDAO = new RotaDAO();
    private final AbastecimentoController abastecimentoController = new AbastecimentoController();
    private final AbastecimentoDAO abastecimentoDAO = new AbastecimentoDAO();
    private final IncidenteController incidenteController = new IncidenteController();
    private final IncidenteDAO incidenteDAO = new IncidenteDAO();
    private final EmbarcacaoDAO embarcacaoDAO = new EmbarcacaoDAO();
    private final ConsultaHorariosController consultaController = new ConsultaHorariosController();

    // Componentes - Aba Viagens
    private JComboBox<Embarcacao> cbViagemEmbarcacao;
    private JComboBox<Tripulante> cbViagemComandante;
    private JComboBox<String> cbViagemDestino;
    private JTextField txtViagemPassageiros;
    private JSpinner spViagemData;
    private JSpinner spViagemHora;
    private JTable tblViagens;
    private DefaultTableModel modelViagens;

    // Componentes - Aba Abastecimento
    private JComboBox<Embarcacao> cbAbastEmbarcacao;
    private JSpinner spAbastData;
    private JTextField txtAbastLitros;
    private JTextField txtAbastValorTotal;
    private JComboBox<Fornecedor> cbAbastFornecedor;
    private JTable tblAbastecimentos;
    private DefaultTableModel modelAbastecimentos;

    // Componentes - Aba Incidente
    private JComboBox<Embarcacao> cbIncidEmbarcacao;
    private JSpinner spIncidDataHora;
    private JTextField txtIncidViagemId;
    private JComboBox<String> cbIncidGravidade;
    private JTextArea txtIncidDescricao;
    private JTable tblIncidentes;
    private DefaultTableModel modelIncidentes;
    private JTextArea txtDetalheIncidente;

    // Componentes - Aba Consulta Horários
    private JComboBox<String> cbConsultaEmbarcacao;
    private JComboBox<String> cbConsultaComandante;
    private JComboBox<String> cbConsultaDestino;
    private JTable tblConsulta;
    private DefaultTableModel modelConsulta;

    public TelaDashboardOperador() {
        setTitle("Painel Integrado do Operador");
        setSize(1150, 750);
        setMinimumSize(new Dimension(950, 600));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponentes();
        carregarTodosOsDados();
    }

    private void initComponentes() {
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(BG_APP);

        add(criarHeader(), BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabbedPane.setBackground(BG_APP);

        tabbedPane.addTab("Registrar Viagem", criarPainelViagens());
        tabbedPane.addTab("Registrar Abastecimento", criarPainelAbastecimento());
        tabbedPane.addTab("Central de Incidentes", criarPainelIncidente());
        tabbedPane.addTab("Consultar Horários", criarPainelConsultaHorarios());

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

        JLabel lblTitle = new JLabel("PAINEL INTEGRADO DO OPERADOR");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Gestão operacional de viagens, abastecimentos, incidentes e consulta de horários");
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
    // ABA 1: REGISTRAR VIAGEM
    // ==========================================
    private JPanel criarPainelViagens() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(BG_APP);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

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
        gbc.gridx = 1; cbViagemEmbarcacao = new JComboBox<>(); pnlForm.add(cbViagemEmbarcacao, gbc);

        gbc.gridx = 2; gbc.gridy = 0; pnlForm.add(criarLabel("Comandante:"), gbc);
        gbc.gridx = 3; cbViagemComandante = new JComboBox<>(); pnlForm.add(cbViagemComandante, gbc);

        gbc.gridx = 0; gbc.gridy = 1; pnlForm.add(criarLabel("Rota / Destino:"), gbc);
        gbc.gridx = 1; cbViagemDestino = new JComboBox<>(); pnlForm.add(cbViagemDestino, gbc);

        gbc.gridx = 2; gbc.gridy = 1; pnlForm.add(criarLabel("Qtd. Passageiros:"), gbc);
        gbc.gridx = 3; txtViagemPassageiros = new JTextField(10); pnlForm.add(txtViagemPassageiros, gbc);

        gbc.gridx = 0; gbc.gridy = 2; pnlForm.add(criarLabel("Data Partida:"), gbc);
        gbc.gridx = 1;
        spViagemData = new JSpinner(new SpinnerDateModel());
        spViagemData.setEditor(new JSpinner.DateEditor(spViagemData, "dd/MM/yyyy"));
        pnlForm.add(spViagemData, gbc);

        gbc.gridx = 2; gbc.gridy = 2; pnlForm.add(criarLabel("Horário Partida:"), gbc);
        gbc.gridx = 3;
        spViagemHora = new JSpinner(new SpinnerDateModel());
        spViagemHora.setEditor(new JSpinner.DateEditor(spViagemHora, "HH:mm"));
        pnlForm.add(spViagemHora, gbc);

        gbc.gridx = 3; gbc.gridy = 3;
        JButton btnSalvar = criarBotaoArredondado("Registrar Viagem", PRIMARY_GREEN);
        btnSalvar.addActionListener(e -> salvarViagem());
        pnlForm.add(btnSalvar, gbc);

        panel.add(pnlForm, BorderLayout.NORTH);

        modelViagens = new DefaultTableModel(new String[]{"ID", "Embarcação", "Comandante", "Destino", "Passageiros", "Partida", "Chegada", "Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblViagens = new JTable(modelViagens);
        estilarTabela(tblViagens);
        ajustarLargurasViagens();

        JScrollPane scroll = new JScrollPane(tblViagens);
        scroll.setBorder(BorderFactory.createLineBorder(CARD_BORDER));
        scroll.getViewport().setBackground(Color.WHITE);
        panel.add(scroll, BorderLayout.CENTER);

        JPanel pnlAcoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        pnlAcoes.setBackground(BG_APP);

        JButton btnConcluir = criarBotaoArredondado("Concluir Viagem", PRIMARY_BLUE);
        JButton btnCancelar = criarBotaoArredondado("Cancelar Viagem", SECONDARY_GRAY);
        JButton btnExcluir = criarBotaoArredondado("Excluir Viagem", DANGER_RED);

        btnConcluir.addActionListener(e -> alterarStatusViagem(true));
        btnCancelar.addActionListener(e -> alterarStatusViagem(false));
        btnExcluir.addActionListener(e -> excluirViagem());

        pnlAcoes.add(btnConcluir);
        pnlAcoes.add(btnCancelar);
        pnlAcoes.add(btnExcluir);
        panel.add(pnlAcoes, BorderLayout.SOUTH);

        return panel;
    }

    private void ajustarLargurasViagens() {
        tblViagens.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblViagens.getColumnModel().getColumn(1).setPreferredWidth(160);
        tblViagens.getColumnModel().getColumn(2).setPreferredWidth(160);
        tblViagens.getColumnModel().getColumn(3).setPreferredWidth(160);
        tblViagens.getColumnModel().getColumn(4).setPreferredWidth(90);
        tblViagens.getColumnModel().getColumn(5).setPreferredWidth(130);
        tblViagens.getColumnModel().getColumn(6).setPreferredWidth(130);
        tblViagens.getColumnModel().getColumn(7).setPreferredWidth(110);
    }

    private void salvarViagem() {
        try {
            Embarcacao emb = (Embarcacao) cbViagemEmbarcacao.getSelectedItem();
            Tripulante trip = (Tripulante) cbViagemComandante.getSelectedItem();
            String destino = (String) cbViagemDestino.getSelectedItem();

            if (emb == null || trip == null || destino == null || destino.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Selecione todos os campos obrigatórios.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int passageiros = Integer.parseInt(txtViagemPassageiros.getText().trim());
            Date dPartida = (Date) spViagemData.getValue();
            Date dHora = (Date) spViagemHora.getValue();

            LocalDate lDate = dPartida.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalTime lTime = dHora.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
            LocalDateTime partida = LocalDateTime.of(lDate, lTime);

            Viagem v = new Viagem(emb.getId(), trip.getId(), destino, partida, passageiros);
            String res = viagemController.registrarViagem(v, emb);

            if ("OK".equals(res)) {
                JOptionPane.showMessageDialog(this, "Viagem registrada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                txtViagemPassageiros.setText("");
                carregarTabelaViagens();
                carregarTabelaConsulta();
            } else {
                JOptionPane.showMessageDialog(this, res, "Alerta", JOptionPane.WARNING_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Informe um número de passageiros válido.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void alterarStatusViagem(boolean concluir) {
        int row = tblViagens.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma viagem na tabela.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int idViagem = (int) modelViagens.getValueAt(row, 0);
        String status = (String) modelViagens.getValueAt(row, 7);

        if (!"EM_ANDAMENTO".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(this, "Apenas viagens 'EM_ANDAMENTO' podem ter seu status alterado.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean ok = concluir ? viagemDAO.finalizarViagem(idViagem) : viagemDAO.cancelarViagem(idViagem);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Status da viagem atualizado!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            carregarTabelaViagens();
            carregarTabelaConsulta();
        }
    }

    private void excluirViagem() {
        int row = tblViagens.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma viagem para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int idViagem = (int) modelViagens.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Confirma a exclusão da viagem ID " + idViagem + "?", "Atenção", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (viagemController.excluirViagem(idViagem)) {
                JOptionPane.showMessageDialog(this, "Viagem excluída com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarTabelaViagens();
                carregarTabelaConsulta();
            } else {
                JOptionPane.showMessageDialog(this, "Falha ao excluir a viagem.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ==========================================
    // ABA 2: REGISTRAR ABASTECIMENTO
    // ==========================================
    private JPanel criarPainelAbastecimento() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(BG_APP);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

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
        gbc.gridx = 1; cbAbastEmbarcacao = new JComboBox<>(); pnlForm.add(cbAbastEmbarcacao, gbc);

        gbc.gridx = 2; gbc.gridy = 0; pnlForm.add(criarLabel("Data Abastecimento:"), gbc);
        gbc.gridx = 3;
        spAbastData = new JSpinner(new SpinnerDateModel());
        spAbastData.setEditor(new JSpinner.DateEditor(spAbastData, "dd/MM/yyyy"));
        pnlForm.add(spAbastData, gbc);

        gbc.gridx = 0; gbc.gridy = 1; pnlForm.add(criarLabel("Litros (L):"), gbc);
        gbc.gridx = 1; txtAbastLitros = new JTextField(10); pnlForm.add(txtAbastLitros, gbc);

        gbc.gridx = 2; gbc.gridy = 1; pnlForm.add(criarLabel("Valor Total (R$):"), gbc);
        gbc.gridx = 3; txtAbastValorTotal = new JTextField(10); pnlForm.add(txtAbastValorTotal, gbc);

        gbc.gridx = 0; gbc.gridy = 2; pnlForm.add(criarLabel("Posto / Fornecedor:"), gbc);
        gbc.gridx = 1; cbAbastFornecedor = new JComboBox<>(); pnlForm.add(cbAbastFornecedor, gbc);

        gbc.gridx = 3; gbc.gridy = 2;
        JButton btnSalvar = criarBotaoArredondado("Salvar Abastecimento", PRIMARY_GREEN);
        btnSalvar.addActionListener(e -> salvarAbastecimento());
        pnlForm.add(btnSalvar, gbc);

        panel.add(pnlForm, BorderLayout.NORTH);

        modelAbastecimentos = new DefaultTableModel(new String[]{"ID", "Embarcação", "Data", "Litros", "Valor Total (R$)", "Fornecedor"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblAbastecimentos = new JTable(modelAbastecimentos);
        estilarTabela(tblAbastecimentos);
        ajustarLargurasAbastecimento();

        JScrollPane scroll = new JScrollPane(tblAbastecimentos);
        scroll.setBorder(BorderFactory.createLineBorder(CARD_BORDER));
        scroll.getViewport().setBackground(Color.WHITE);
        panel.add(scroll, BorderLayout.CENTER);

        JPanel pnlAcoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        pnlAcoes.setBackground(BG_APP);

        JButton btnExcluir = criarBotaoArredondado("Excluir Abastecimento", DANGER_RED);
        btnExcluir.addActionListener(e -> excluirAbastecimento());
        pnlAcoes.add(btnExcluir);

        panel.add(pnlAcoes, BorderLayout.SOUTH);

        return panel;
    }

    private void ajustarLargurasAbastecimento() {
        tblAbastecimentos.getColumnModel().getColumn(0).setPreferredWidth(60);
        tblAbastecimentos.getColumnModel().getColumn(1).setPreferredWidth(200);
        tblAbastecimentos.getColumnModel().getColumn(2).setPreferredWidth(120);
        tblAbastecimentos.getColumnModel().getColumn(3).setPreferredWidth(110);
        tblAbastecimentos.getColumnModel().getColumn(4).setPreferredWidth(130);
        tblAbastecimentos.getColumnModel().getColumn(5).setPreferredWidth(220);
    }

    private void salvarAbastecimento() {
        try {
            Embarcacao emb = (Embarcacao) cbAbastEmbarcacao.getSelectedItem();
            Fornecedor forn = (Fornecedor) cbAbastFornecedor.getSelectedItem();

            if (emb == null || forn == null) {
                JOptionPane.showMessageDialog(this, "Selecione uma embarcação e um fornecedor.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            java.util.Date dateVal = (java.util.Date) spAbastData.getValue();
            java.time.LocalDate data = dateVal.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            double litros = Double.parseDouble(txtAbastLitros.getText().trim().replace(",", "."));
            double valorTotal = Double.parseDouble(txtAbastValorTotal.getText().trim().replace(",", "."));

            Abastecimento a = new Abastecimento(emb.getId(), data, litros, valorTotal, forn.getNome());
            String res = abastecimentoController.registrar(a);

            if ("OK".equals(res)) {
                JOptionPane.showMessageDialog(this, "Abastecimento registrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                txtAbastLitros.setText("");
                txtAbastValorTotal.setText("");
                carregarTabelaAbastecimentos();
            } else {
                JOptionPane.showMessageDialog(this, res, "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Verifique os valores informados para Litros e Valor Total.", "Erro de Digitação", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluirAbastecimento() {
        int row = tblAbastecimentos.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um abastecimento na tabela para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int idAbast = (int) modelAbastecimentos.getValueAt(row, 0);

        if (JOptionPane.showConfirmDialog(this, "Confirma a exclusão do abastecimento ID " + idAbast + "?", "Atenção", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (abastecimentoDAO.excluir(idAbast)) {
                JOptionPane.showMessageDialog(this, "Abastecimento excluído com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarTabelaAbastecimentos();
            } else {
                JOptionPane.showMessageDialog(this, "Falha ao excluir abastecimento.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ==========================================
    // ABA 3: REGISTRAR INCIDENTE
    // ==========================================
    private JPanel criarPainelIncidente() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(BG_APP);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

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
        gbc.gridx = 1; cbIncidEmbarcacao = new JComboBox<>(); pnlForm.add(cbIncidEmbarcacao, gbc);

        gbc.gridx = 2; gbc.gridy = 0; pnlForm.add(criarLabel("Data / Hora Ocorrência:"), gbc);
        gbc.gridx = 3;
        spIncidDataHora = new JSpinner(new SpinnerDateModel());
        spIncidDataHora.setEditor(new JSpinner.DateEditor(spIncidDataHora, "dd/MM/yyyy HH:mm"));
        pnlForm.add(spIncidDataHora, gbc);

        gbc.gridx = 0; gbc.gridy = 1; pnlForm.add(criarLabel("Gravidade:"), gbc);
        gbc.gridx = 1; cbIncidGravidade = new JComboBox<>(new String[]{"BAIXA", "MEDIA", "ALTA", "CRITICA"}); pnlForm.add(cbIncidGravidade, gbc);

        gbc.gridx = 2; gbc.gridy = 1; pnlForm.add(criarLabel("Cód. Viagem (Opcional):"), gbc);
        gbc.gridx = 3; txtIncidViagemId = new JTextField(10); pnlForm.add(txtIncidViagemId, gbc);

        gbc.gridx = 0; gbc.gridy = 2; pnlForm.add(criarLabel("Descrição:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        txtIncidDescricao = new JTextArea(2, 40);
        txtIncidDescricao.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtIncidDescricao.setLineWrap(true);
        txtIncidDescricao.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtIncidDescricao);
        scrollDesc.setBorder(BorderFactory.createLineBorder(CARD_BORDER));
        pnlForm.add(scrollDesc, gbc);

        gbc.gridx = 3; gbc.gridy = 3; gbc.gridwidth = 1;
        JButton btnSalvar = criarBotaoArredondado("Reportar Incidente ao ADM", PRIMARY_GREEN);
        btnSalvar.addActionListener(e -> salvarIncidente());
        pnlForm.add(btnSalvar, gbc);

        panel.add(pnlForm, BorderLayout.NORTH);

        modelIncidentes = new DefaultTableModel(new String[]{"ID", "Embarcação", "Data Incidente", "Descrição", "Gravidade", "Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblIncidentes = new JTable(modelIncidentes);
        estilarTabela(tblIncidentes);
        ajustarLargurasIncidentes();

        // Renderização customizada da coluna de gravidade
        tblIncidentes.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                String grav = (value != null) ? value.toString() : "";
                if ("CRITICA".equalsIgnoreCase(grav) || "ALTA".equalsIgnoreCase(grav)) {
                    c.setForeground(DANGER_RED);
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                } else if ("MEDIA".equalsIgnoreCase(grav)) {
                    c.setForeground(new Color(217, 119, 6)); // amber-600
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                } else {
                    c.setForeground(PRIMARY_GREEN);
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                }
                return c;
            }
        });

        JScrollPane scrollTabela = new JScrollPane(tblIncidentes);
        scrollTabela.setBorder(BorderFactory.createLineBorder(CARD_BORDER));
        scrollTabela.getViewport().setBackground(Color.WHITE);

        JPanel pnlDetalhes = new JPanel(new BorderLayout());
        pnlDetalhes.setBackground(CARD_BG);
        pnlDetalhes.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        JLabel lblDetTitulo = criarLabel("Detalhes do Registro Selecionado:");
        lblDetTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));

        txtDetalheIncidente = new JTextArea(4, 50);
        txtDetalheIncidente.setEditable(false);
        txtDetalheIncidente.setLineWrap(true);
        txtDetalheIncidente.setWrapStyleWord(true);
        txtDetalheIncidente.setBackground(new Color(248, 250, 252));
        txtDetalheIncidente.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtDetalheIncidente.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        JScrollPane scrollDetalhes = new JScrollPane(txtDetalheIncidente);
        scrollDetalhes.setBorder(BorderFactory.createLineBorder(CARD_BORDER));

        pnlDetalhes.add(lblDetTitulo, BorderLayout.NORTH);
        pnlDetalhes.add(scrollDetalhes, BorderLayout.CENTER);

        tblIncidentes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tblIncidentes.getSelectedRow();
                if (row != -1) {
                    txtDetalheIncidente.setText(
                        "ID REGISTRO : " + modelIncidentes.getValueAt(row, 0) + "\n" +
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
        splitPane.setResizeWeight(0.6);
        splitPane.setBorder(null);

        panel.add(splitPane, BorderLayout.CENTER);

        JPanel pnlAcoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        pnlAcoes.setBackground(BG_APP);

        JButton btnExcluir = criarBotaoArredondado("Excluir Incidente", DANGER_RED);
        btnExcluir.addActionListener(e -> excluirIncidente());
        pnlAcoes.add(btnExcluir);

        panel.add(pnlAcoes, BorderLayout.SOUTH);

        return panel;
    }

    private void ajustarLargurasIncidentes() {
        tblIncidentes.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblIncidentes.getColumnModel().getColumn(1).setPreferredWidth(170);
        tblIncidentes.getColumnModel().getColumn(2).setPreferredWidth(140);
        tblIncidentes.getColumnModel().getColumn(3).setPreferredWidth(380);
        tblIncidentes.getColumnModel().getColumn(4).setPreferredWidth(90);
        tblIncidentes.getColumnModel().getColumn(5).setPreferredWidth(100);
    }

    private void salvarIncidente() {
        Embarcacao emb = (Embarcacao) cbIncidEmbarcacao.getSelectedItem();
        Integer idEmbarcacao = (emb != null) ? emb.getId() : null;
        String viagemIdStr = txtIncidViagemId.getText();
        String gravidade = (String) cbIncidGravidade.getSelectedItem();
        String descricao = txtIncidDescricao.getText();

        if (descricao == null || descricao.trim().length() < 5) {
            JOptionPane.showMessageDialog(this, "Descreva detalhadamente o incidente antes de enviar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String res = incidenteController.registrarIncidente(idEmbarcacao, viagemIdStr, gravidade, descricao);

        if ("OK".equals(res)) {
            JOptionPane.showMessageDialog(this, "Incidente reportado com sucesso!", "Enviado", JOptionPane.INFORMATION_MESSAGE);
            txtIncidViagemId.setText("");
            txtIncidDescricao.setText("");
            spIncidDataHora.setValue(new java.util.Date());
            carregarTabelaIncidentes();
        } else {
            JOptionPane.showMessageDialog(this, res, "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void excluirIncidente() {
        int row = tblIncidentes.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um incidente na tabela para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int idIncidente = (int) modelIncidentes.getValueAt(row, 0);

        if (JOptionPane.showConfirmDialog(this, "Confirma a exclusão do incidente ID " + idIncidente + "?", "Atenção", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (incidenteDAO.excluir(idIncidente)) {
                JOptionPane.showMessageDialog(this, "Incidente excluído com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                txtDetalheIncidente.setText("");
                carregarTabelaIncidentes();
            } else {
                JOptionPane.showMessageDialog(this, "Falha ao excluir incidente.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ==========================================
    // ABA 4: CONSULTAR HORÁRIOS
    // ==========================================
    private JPanel criarPainelConsultaHorarios() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(BG_APP);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        JPanel pnlFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        pnlFiltros.setBackground(CARD_BG);
        pnlFiltros.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        pnlFiltros.add(criarLabel("Embarcação:"));
        cbConsultaEmbarcacao = new JComboBox<>();
        pnlFiltros.add(cbConsultaEmbarcacao);

        pnlFiltros.add(criarLabel("Comandante:"));
        cbConsultaComandante = new JComboBox<>();
        pnlFiltros.add(cbConsultaComandante);

        pnlFiltros.add(criarLabel("Destino:"));
        cbConsultaDestino = new JComboBox<>();
        pnlFiltros.add(cbConsultaDestino);

        JButton btnFiltrar = criarBotaoArredondado("Filtrar", PRIMARY_BLUE);
        btnFiltrar.addActionListener(e -> carregarTabelaConsulta());
        pnlFiltros.add(btnFiltrar);

        JButton btnLimpar = criarBotaoArredondado("Resetar", SECONDARY_GRAY);
        btnLimpar.addActionListener(e -> {
            cbConsultaEmbarcacao.setSelectedIndex(0);
            cbConsultaComandante.setSelectedIndex(0);
            cbConsultaDestino.setSelectedIndex(0);
            carregarTabelaConsulta();
        });
        pnlFiltros.add(btnLimpar);

        panel.add(pnlFiltros, BorderLayout.NORTH);

        modelConsulta = new DefaultTableModel(new String[]{"ID", "Embarcação", "Comandante", "Destino", "Partida", "Chegada", "Passageiros", "Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblConsulta = new JTable(modelConsulta);
        estilarTabela(tblConsulta);
        ajustarLargurasConsulta();

        JScrollPane scroll = new JScrollPane(tblConsulta);
        scroll.setBorder(BorderFactory.createLineBorder(CARD_BORDER));
        scroll.getViewport().setBackground(Color.WHITE);

        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private void ajustarLargurasConsulta() {
        tblConsulta.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblConsulta.getColumnModel().getColumn(1).setPreferredWidth(160);
        tblConsulta.getColumnModel().getColumn(2).setPreferredWidth(160);
        tblConsulta.getColumnModel().getColumn(3).setPreferredWidth(160);
        tblConsulta.getColumnModel().getColumn(4).setPreferredWidth(130);
        tblConsulta.getColumnModel().getColumn(5).setPreferredWidth(130);
        tblConsulta.getColumnModel().getColumn(6).setPreferredWidth(90);
        tblConsulta.getColumnModel().getColumn(7).setPreferredWidth(110);
    }

    // ==========================================
    // MÉTODOS UTILITÁRIOS DE UI
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

    // ==========================================
    // CARREGAMENTO DE DADOS E TABELAS
    // ==========================================
    private void carregarTodosOsDados() {
        carregarCombos();
        carregarTabelaViagens();
        carregarTabelaAbastecimentos();
        carregarTabelaIncidentes();
        carregarTabelaConsulta();
    }

    private void carregarCombos() {
        List<Embarcacao> embarcacoes = embarcacaoDAO.listarTodas();
        List<Tripulante> tripulantes = new TripulanteDAO().listarTodos();
        List<String> rotas = rotaDAO.listarTodas();
        List<Fornecedor> fornecedores = new FornecedorDAO().listarTodos();

        cbViagemEmbarcacao.removeAllItems();
        cbAbastEmbarcacao.removeAllItems();
        cbIncidEmbarcacao.removeAllItems();
        cbViagemComandante.removeAllItems();
        cbViagemDestino.removeAllItems();
        cbAbastFornecedor.removeAllItems();

        embarcacoes.forEach(e -> {
            cbViagemEmbarcacao.addItem(e);
            cbAbastEmbarcacao.addItem(e);
            cbIncidEmbarcacao.addItem(e);
        });

        tripulantes.forEach(cbViagemComandante::addItem);
        rotas.forEach(cbViagemDestino::addItem);
        fornecedores.forEach(cbAbastFornecedor::addItem);

        // Combos da Consulta
        cbConsultaEmbarcacao.removeAllItems();
        cbConsultaComandante.removeAllItems();
        cbConsultaDestino.removeAllItems();

        consultaController.obterEmbarcacoes().forEach(cbConsultaEmbarcacao::addItem);
        consultaController.obterComandantes().forEach(cbConsultaComandante::addItem);
        consultaController.obterDestinos().forEach(cbConsultaDestino::addItem);
    }

    private void carregarTabelaViagens() {
        modelViagens.setRowCount(0);
        for (Viagem v : viagemDAO.listarTodas()) {
            modelViagens.addRow(new Object[]{
                v.getId(),
                v.getNomeEmbarcacao(),
                v.getNomeComandante(),
                v.getRotaDestino(),
                v.getQuantidadePassageiros(),
                v.getDataHoraPartida() != null ? v.getDataHoraPartida().format(dateTimeFormatter) : "-",
                v.getDataHoraChegada() != null ? v.getDataHoraChegada().format(dateTimeFormatter) : "-",
                v.getStatus()
            });
        }
    }

    private void carregarTabelaAbastecimentos() {
        modelAbastecimentos.setRowCount(0);
        for (AbastecimentoDAO.AbastecimentoDTO a : abastecimentoDAO.listarTodos()) {
            modelAbastecimentos.addRow(new Object[]{
                a.getId(),
                a.getNomeEmbarcacao(),
                a.getData().format(dateFormatter),
                String.format("%.2f L", a.getLitros()),
                String.format("R$ %.2f", a.getValorTotal()),
                a.getFornecedor()
            });
        }
    }

    private void carregarTabelaIncidentes() {
        modelIncidentes.setRowCount(0);
        List<Object[]> lista = incidenteDAO.listarTodosParaTabela();
        
        for (Object[] linha : lista) {
            java.time.LocalDateTime data = (java.time.LocalDateTime) linha[2];
            String dataStr = (data != null) ? data.format(dateTimeFormatter) : "-";

            modelIncidentes.addRow(new Object[]{
                linha[0], // ID
                linha[1], // Embarcação
                dataStr,  // Data Incidente
                linha[3], // Descrição
                linha[4], // Gravidade
                linha[5]  // Status
            });
        }
    }

    private void carregarTabelaConsulta() {
        modelConsulta.setRowCount(0);
        String selEmb = (String) cbConsultaEmbarcacao.getSelectedItem();
        String selCom = (String) cbConsultaComandante.getSelectedItem();
        String selDes = (String) cbConsultaDestino.getSelectedItem();

        List<ConsultaHorarioDTO> lista = consultaController.buscarHorarios(selEmb, selCom, selDes);
        for (ConsultaHorarioDTO dto : lista) {
            String partidaStr = dto.getDataHoraPartida() != null ? dto.getDataHoraPartida().format(dateTimeFormatter) : "-";
            String chegadaStr = dto.getDataHoraChegada() != null ? dto.getDataHoraChegada().format(dateTimeFormatter) : "Em Trânsito";

            modelConsulta.addRow(new Object[]{
                dto.getIdViagem(),
                dto.getEmbarcacao(),
                dto.getComandante(),
                dto.getRotaDestino(),
                partidaStr,
                chegadaStr,
                dto.getQuantidadePassageiros(),
                dto.getStatus()
            });
        }
    }
}