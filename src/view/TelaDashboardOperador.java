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
        setTitle("Painel Integrado do Operador - Gestao Nautica");
        setSize(1150, 750);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 12));

        tabbedPane.addTab("Registrar Viagem", criarPainelViagens());
        tabbedPane.addTab("Registrar Abastecimento", criarPainelAbastecimento());
        tabbedPane.addTab("Central de Incidentes", criarPainelIncidente());
        tabbedPane.addTab("Consultar Horários", criarPainelConsultaHorarios());

        add(tabbedPane);
        carregarTodosOsDados();
    }

    // ==========================================
    // ABA 1: REGISTRAR VIAGEM
    // ==========================================
    private JPanel criarPainelViagens() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBorder(BorderFactory.createTitledBorder(" Formulario de Nova Viagem "));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; pnlForm.add(new JLabel("Embarcaçao:"), gbc);
        gbc.gridx = 1; cbViagemEmbarcacao = new JComboBox<>(); pnlForm.add(cbViagemEmbarcacao, gbc);

        gbc.gridx = 2; gbc.gridy = 0; pnlForm.add(new JLabel("Comandante:"), gbc);
        gbc.gridx = 3; cbViagemComandante = new JComboBox<>(); pnlForm.add(cbViagemComandante, gbc);

        gbc.gridx = 0; gbc.gridy = 1; pnlForm.add(new JLabel("Rota / Destino:"), gbc);
        gbc.gridx = 1; cbViagemDestino = new JComboBox<>(); pnlForm.add(cbViagemDestino, gbc);

        gbc.gridx = 2; gbc.gridy = 1; pnlForm.add(new JLabel("Qtd. Passageiros:"), gbc);
        gbc.gridx = 3; txtViagemPassageiros = new JTextField(10); pnlForm.add(txtViagemPassageiros, gbc);

        gbc.gridx = 0; gbc.gridy = 2; pnlForm.add(new JLabel("Data Partida:"), gbc);
        gbc.gridx = 1;
        spViagemData = new JSpinner(new SpinnerDateModel());
        spViagemData.setEditor(new JSpinner.DateEditor(spViagemData, "dd/MM/yyyy"));
        pnlForm.add(spViagemData, gbc);

        gbc.gridx = 2; gbc.gridy = 2; pnlForm.add(new JLabel("Horario Partida:"), gbc);
        gbc.gridx = 3;
        spViagemHora = new JSpinner(new SpinnerDateModel());
        spViagemHora.setEditor(new JSpinner.DateEditor(spViagemHora, "HH:mm"));
        pnlForm.add(spViagemHora, gbc);

        gbc.gridx = 3; gbc.gridy = 3;
        JButton btnSalvar = new JButton("Registrar Viagem");
        btnSalvar.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnSalvar.addActionListener(e -> salvarViagem());
        pnlForm.add(btnSalvar, gbc);

        panel.add(pnlForm, BorderLayout.NORTH);

        modelViagens = new DefaultTableModel(new String[]{"ID", "Embarcaçao", "Comandante", "Destino", "Passageiros", "Partida", "Chegada", "Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblViagens = new JTable(modelViagens);
        ajustarLargurasViagens();

        panel.add(new JScrollPane(tblViagens), BorderLayout.CENTER);

        JPanel pnlAcoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        JButton btnConcluir = new JButton("Concluir Viagem");
        JButton btnCancelar = new JButton("Cancelar Viagem");
        JButton btnExcluir = new JButton("Excluir Viagem");

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
        tblViagens.getColumnModel().getColumn(0).setPreferredWidth(50);   // ID
        tblViagens.getColumnModel().getColumn(1).setPreferredWidth(160);  // Embarcação
        tblViagens.getColumnModel().getColumn(2).setPreferredWidth(160);  // Comandante
        tblViagens.getColumnModel().getColumn(3).setPreferredWidth(160);  // Destino
        tblViagens.getColumnModel().getColumn(4).setPreferredWidth(90);   // Passageiros
        tblViagens.getColumnModel().getColumn(5).setPreferredWidth(130);  // Partida
        tblViagens.getColumnModel().getColumn(6).setPreferredWidth(130);  // Chegada
        tblViagens.getColumnModel().getColumn(7).setPreferredWidth(110);  // Status
    }

    private void salvarViagem() {
        try {
            Embarcacao emb = (Embarcacao) cbViagemEmbarcacao.getSelectedItem();
            Tripulante trip = (Tripulante) cbViagemComandante.getSelectedItem();
            String destino = (String) cbViagemDestino.getSelectedItem();

            if (emb == null || trip == null || destino == null || destino.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Selecione todos os campos obrigatorios.", "Aviso", JOptionPane.WARNING_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Informe um numero de passageiros valido.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void alterarStatusViagem(boolean concluir) {
        int row = tblViagens.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma viagem na tabela.");
            return;
        }
        int idViagem = (int) modelViagens.getValueAt(row, 0);
        String status = (String) modelViagens.getValueAt(row, 7);

        if (!"EM_ANDAMENTO".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(this, "Apenas viagens 'EM_ANDAMENTO' podem ter seu status alterado.");
            return;
        }

        boolean ok = concluir ? viagemDAO.finalizarViagem(idViagem) : viagemDAO.cancelarViagem(idViagem);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Status da viagem atualizado!");
            carregarTabelaViagens();
            carregarTabelaConsulta();
        }
    }

    private void excluirViagem() {
        int row = tblViagens.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma viagem para excluir.");
            return;
        }
        int idViagem = (int) modelViagens.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Confirma a exclusao da viagem ID " + idViagem + "?", "Atençao", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (viagemController.excluirViagem(idViagem)) {
                JOptionPane.showMessageDialog(this, "Viagem excluida!");
                carregarTabelaViagens();
                carregarTabelaConsulta();
            } else {
                JOptionPane.showMessageDialog(this, "Falha ao excluir a viagem.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
// ==========================================
// ABA 2: REGISTRAR ABASTECIMENTO (COM EXCLUSÃO)
// ==========================================
private JPanel criarPainelAbastecimento() {
    JPanel panel = new JPanel(new BorderLayout(10, 10));
    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    JPanel pnlForm = new JPanel(new GridBagLayout());
    pnlForm.setBorder(BorderFactory.createTitledBorder(" Novo Lançamento de Abastecimento "));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(6, 6, 6, 6);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    gbc.gridx = 0; gbc.gridy = 0; pnlForm.add(new JLabel("Embarcaçao:"), gbc);
    gbc.gridx = 1; cbAbastEmbarcacao = new JComboBox<>(); pnlForm.add(cbAbastEmbarcacao, gbc);

    gbc.gridx = 2; gbc.gridy = 0; pnlForm.add(new JLabel("Data Abastecimento:"), gbc);
    gbc.gridx = 3;
    spAbastData = new JSpinner(new SpinnerDateModel());
    spAbastData.setEditor(new JSpinner.DateEditor(spAbastData, "dd/MM/yyyy"));
    pnlForm.add(spAbastData, gbc);

    gbc.gridx = 0; gbc.gridy = 1; pnlForm.add(new JLabel("Litros (L):"), gbc);
    gbc.gridx = 1; txtAbastLitros = new JTextField(10); pnlForm.add(txtAbastLitros, gbc);

    gbc.gridx = 2; gbc.gridy = 1; pnlForm.add(new JLabel("Valor Total (R$):"), gbc);
    gbc.gridx = 3; txtAbastValorTotal = new JTextField(10); pnlForm.add(txtAbastValorTotal, gbc);

    gbc.gridx = 0; gbc.gridy = 2; pnlForm.add(new JLabel("Posto / Fornecedor:"), gbc);
    gbc.gridx = 1; cbAbastFornecedor = new JComboBox<>(); pnlForm.add(cbAbastFornecedor, gbc);

    gbc.gridx = 3; gbc.gridy = 2;
    JButton btnSalvar = new JButton("Salvar Abastecimento");
    btnSalvar.setFont(new Font("SansSerif", Font.BOLD, 12));
    btnSalvar.addActionListener(e -> salvarAbastecimento());
    pnlForm.add(btnSalvar, gbc);

    panel.add(pnlForm, BorderLayout.NORTH);

    modelAbastecimentos = new DefaultTableModel(new String[]{"ID", "Embarcaçao", "Data", "Litros", "Valor Total (R$)", "Fornecedor"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    tblAbastecimentos = new JTable(modelAbastecimentos);
    ajustarLargurasAbastecimento();

    panel.add(new JScrollPane(tblAbastecimentos), BorderLayout.CENTER);

    // Painel de Ações de Abastecimento
    JPanel pnlAcoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
    JButton btnExcluir = new JButton("Excluir Abastecimento");
    btnExcluir.addActionListener(e -> excluirAbastecimento());
    pnlAcoes.add(btnExcluir);

    panel.add(pnlAcoes, BorderLayout.SOUTH);

    return panel;
}

private void ajustarLargurasAbastecimento() {
    tblAbastecimentos.getColumnModel().getColumn(0).setPreferredWidth(60);   // ID
    tblAbastecimentos.getColumnModel().getColumn(1).setPreferredWidth(200);  // Embarcação
    tblAbastecimentos.getColumnModel().getColumn(2).setPreferredWidth(120);  // Data
    tblAbastecimentos.getColumnModel().getColumn(3).setPreferredWidth(110);  // Litros
    tblAbastecimentos.getColumnModel().getColumn(4).setPreferredWidth(130);  // Valor Total
    tblAbastecimentos.getColumnModel().getColumn(5).setPreferredWidth(220);  // Fornecedor
}

private void salvarAbastecimento() {
    try {
        Embarcacao emb = (Embarcacao) cbAbastEmbarcacao.getSelectedItem();
        Fornecedor forn = (Fornecedor) cbAbastFornecedor.getSelectedItem();

        if (emb == null || forn == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma embarcaçao e um fornecedor.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        java.util.Date dateVal = (java.util.Date) spAbastData.getValue();
        java.time.LocalDate data = dateVal.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        double litros = Double.parseDouble(txtAbastLitros.getText().trim().replace(",", "."));
        double valorTotal = Double.parseDouble(txtAbastValorTotal.getText().trim().replace(",", "."));

        Abastecimento a = new Abastecimento(emb.getId(), data, litros, valorTotal, forn.getNome());
        String res = abastecimentoController.registrar(a);

        if ("OK".equals(res)) {
            JOptionPane.showMessageDialog(this, "Abastecimento registrado com sucesso!");
            txtAbastLitros.setText("");
            txtAbastValorTotal.setText("");
            carregarTabelaAbastecimentos();
        } else {
            JOptionPane.showMessageDialog(this, res, "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Verifique os valores informados para Litros e Valor Total.", "Erro de Digitaçao", JOptionPane.ERROR_MESSAGE);
    }
}

private void ajustarLargurasIncidentes() {
    tblIncidentes.getColumnModel().getColumn(0).setPreferredWidth(50);   // ID
    tblIncidentes.getColumnModel().getColumn(1).setPreferredWidth(170);  // Embarcação
    tblIncidentes.getColumnModel().getColumn(2).setPreferredWidth(140);  // Data
    tblIncidentes.getColumnModel().getColumn(3).setPreferredWidth(380);  // Descrição
    tblIncidentes.getColumnModel().getColumn(4).setPreferredWidth(90);   // Gravidade
    tblIncidentes.getColumnModel().getColumn(5).setPreferredWidth(100);  // Status
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

private void excluirAbastecimento() {
    int row = tblAbastecimentos.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Selecione um abastecimento na tabela para excluir.");
        return;
    }
    int idAbast = (int) modelAbastecimentos.getValueAt(row, 0);

    if (JOptionPane.showConfirmDialog(this, "Confirma a exclusao do abastecimento ID " + idAbast + "?", "Atençao", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
        if (abastecimentoDAO.excluir(idAbast)) {
            JOptionPane.showMessageDialog(this, "Abastecimento excluido com sucesso!");
            carregarTabelaAbastecimentos();
        } else {
            JOptionPane.showMessageDialog(this, "Falha ao excluir abastecimento.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}


// ==========================================
// ABA 3: REGISTRAR INCIDENTE (COM EXCLUSÃO)
// ==========================================
private JPanel criarPainelIncidente() {
    JPanel panel = new JPanel(new BorderLayout(10, 10));
    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // Formulário
    JPanel pnlForm = new JPanel(new GridBagLayout());
    pnlForm.setBorder(BorderFactory.createTitledBorder(" Novo Incidente Operacional "));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    gbc.gridx = 0; gbc.gridy = 0; pnlForm.add(new JLabel("Embarcaçao:"), gbc);
    gbc.gridx = 1; cbIncidEmbarcacao = new JComboBox<>(); pnlForm.add(cbIncidEmbarcacao, gbc);

    gbc.gridx = 2; gbc.gridy = 0; pnlForm.add(new JLabel("Data / Hora Ocorrência:"), gbc);
    gbc.gridx = 3;
    spIncidDataHora = new JSpinner(new SpinnerDateModel());
    spIncidDataHora.setEditor(new JSpinner.DateEditor(spIncidDataHora, "dd/MM/yyyy HH:mm"));
    pnlForm.add(spIncidDataHora, gbc);

    gbc.gridx = 0; gbc.gridy = 1; pnlForm.add(new JLabel("Gravidade:"), gbc);
    gbc.gridx = 1; cbIncidGravidade = new JComboBox<>(new String[]{"BAIXA", "MEDIA", "ALTA", "CRITICA"}); pnlForm.add(cbIncidGravidade, gbc);

    gbc.gridx = 2; gbc.gridy = 1; pnlForm.add(new JLabel("Cod. Viagem (Opcional):"), gbc);
    gbc.gridx = 3; txtIncidViagemId = new JTextField(10); pnlForm.add(txtIncidViagemId, gbc);

    gbc.gridx = 0; gbc.gridy = 2; pnlForm.add(new JLabel("Descrição:"), gbc);
    gbc.gridx = 1; gbc.gridwidth = 3;
    txtIncidDescricao = new JTextArea(2, 40);
    txtIncidDescricao.setLineWrap(true);
    txtIncidDescricao.setWrapStyleWord(true);
    pnlForm.add(new JScrollPane(txtIncidDescricao), gbc);

    gbc.gridx = 3; gbc.gridy = 3; gbc.gridwidth = 1;
    JButton btnSalvar = new JButton("Reportar Incidente ao Adm");
    btnSalvar.setFont(new Font("SansSerif", Font.BOLD, 12));
    btnSalvar.addActionListener(e -> salvarIncidente());
    pnlForm.add(btnSalvar, gbc);

    panel.add(pnlForm, BorderLayout.NORTH);

    // Tabela
    modelIncidentes = new DefaultTableModel(new String[]{"ID", "Embarcaçao", "Data Incidente", "Descrição", "Gravidade", "Status"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    tblIncidentes = new JTable(modelIncidentes);
    ajustarLargurasIncidentes();

// Reativa o import estilizando a coluna de Gravidade por cor
tblIncidentes.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        String grav = (value != null) ? value.toString() : "";
        if ("CRITICA".equalsIgnoreCase(grav) || "ALTA".equalsIgnoreCase(grav)) {
            c.setForeground(Color.RED);
            c.setFont(c.getFont().deriveFont(Font.BOLD));
        } else if ("MEDIA".equalsIgnoreCase(grav)) {
            c.setForeground(new Color(200, 100, 0));
        } else {
            c.setForeground(new Color(0, 120, 0));
        }
        return c;
    }
});

    JScrollPane scrollTabela = new JScrollPane(tblIncidentes);
    scrollTabela.setPreferredSize(new Dimension(800, 180));

    // Painel de Detalhes
    JPanel pnlDetalhes = new JPanel(new BorderLayout());
    pnlDetalhes.setBorder(BorderFactory.createTitledBorder(" Detalhes Completos do Registro Selecionado "));

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
                    "ID REGISTRO : " + modelIncidentes.getValueAt(row, 0) + "\n" +
                    "EMBARCAÇAO  : " + modelIncidentes.getValueAt(row, 1) + "\n" +
                    "DATA/HORA   : " + modelIncidentes.getValueAt(row, 2) + "\n" +
                    "GRAVIDADE   : " + modelIncidentes.getValueAt(row, 4) + "\n" +
                    "STATUS      : " + modelIncidentes.getValueAt(row, 5) + "\n" +
                    "----------------------------------------------------------------------\n" +
                    "DESCRIÇÃO COMPLETA DO INCIDENTE:\n" + modelIncidentes.getValueAt(row, 3)
                );
                txtDetalheIncidente.setCaretPosition(0);
            }
        }
    });

    JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollTabela, pnlDetalhes);
    splitPane.setResizeWeight(0.6);
    panel.add(splitPane, BorderLayout.CENTER);

    // Painel de Ações de Incidentes
    JPanel pnlAcoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
    JButton btnExcluir = new JButton("Excluir Incidente");
    btnExcluir.addActionListener(e -> excluirIncidente());
    pnlAcoes.add(btnExcluir);

    panel.add(pnlAcoes, BorderLayout.SOUTH);

    return panel;
}

private void excluirIncidente() {
    int row = tblIncidentes.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Selecione um incidente na tabela para excluir.");
        return;
    }
    int idIncidente = (int) modelIncidentes.getValueAt(row, 0);

    if (JOptionPane.showConfirmDialog(this, "Confirma a exclusao do incidente ID " + idIncidente + "?", "Atençao", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
        if (incidenteDAO.excluir(idIncidente)) {
            JOptionPane.showMessageDialog(this, "Incidente excluido com sucesso!");
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
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel pnlFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 5));
        pnlFiltros.setBorder(BorderFactory.createTitledBorder(" Filtros Rapidos "));

        pnlFiltros.add(new JLabel("Embarcaçao:"));
        cbConsultaEmbarcacao = new JComboBox<>();
        pnlFiltros.add(cbConsultaEmbarcacao);

        pnlFiltros.add(new JLabel("Comandante:"));
        cbConsultaComandante = new JComboBox<>();
        pnlFiltros.add(cbConsultaComandante);

        pnlFiltros.add(new JLabel("Destino:"));
        cbConsultaDestino = new JComboBox<>();
        pnlFiltros.add(cbConsultaDestino);

        JButton btnFiltrar = new JButton("Filtrar");
        btnFiltrar.addActionListener(e -> carregarTabelaConsulta());
        pnlFiltros.add(btnFiltrar);

        JButton btnLimpar = new JButton("Resetar Filtros");
        btnLimpar.addActionListener(e -> {
            cbConsultaEmbarcacao.setSelectedIndex(0);
            cbConsultaComandante.setSelectedIndex(0);
            cbConsultaDestino.setSelectedIndex(0);
            carregarTabelaConsulta();
        });
        pnlFiltros.add(btnLimpar);

        panel.add(pnlFiltros, BorderLayout.NORTH);

        modelConsulta = new DefaultTableModel(new String[]{"ID", "Embarcaçao", "Comandante", "Destino", "Partida", "Chegada", "Passageiros", "Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblConsulta = new JTable(modelConsulta);
        ajustarLargurasConsulta();

        panel.add(new JScrollPane(tblConsulta), BorderLayout.CENTER);

        return panel;
    }

    private void ajustarLargurasConsulta() {
        tblConsulta.getColumnModel().getColumn(0).setPreferredWidth(50);   // ID
        tblConsulta.getColumnModel().getColumn(1).setPreferredWidth(160);  // Embarcação
        tblConsulta.getColumnModel().getColumn(2).setPreferredWidth(160);  // Comandante
        tblConsulta.getColumnModel().getColumn(3).setPreferredWidth(160);  // Destino
        tblConsulta.getColumnModel().getColumn(4).setPreferredWidth(130);  // Partida
        tblConsulta.getColumnModel().getColumn(5).setPreferredWidth(130);  // Chegada
        tblConsulta.getColumnModel().getColumn(6).setPreferredWidth(90);   // Passageiros
        tblConsulta.getColumnModel().getColumn(7).setPreferredWidth(110);  // Status
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
            String chegadaStr = dto.getDataHoraChegada() != null ? dto.getDataHoraChegada().format(dateTimeFormatter) : "Em Transito";

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