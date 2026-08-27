package view;

import controller.AbastecimentoController;
import dao.AbastecimentoDAO;
import dao.EmbarcacaoDAO;
import dao.FornecedorDAO;
import model.Abastecimento;
import model.Embarcacao;
import model.Fornecedor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class TelaAbastecimento extends JFrame {

    private JComboBox<Embarcacao> cbEmbarcacoes;
    private JSpinner spData;
    private JTextField txtLitros;
    private JTextField txtValorTotal;
    private JComboBox<Fornecedor> cbFornecedores;
    private JTable tabelaAbastecimentos;
    private DefaultTableModel modelTabela;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final AbastecimentoController controller = new AbastecimentoController();
    private final AbastecimentoDAO abastecimentoDAO = new AbastecimentoDAO();

    public TelaAbastecimento() {
        setTitle("Registrar Abastecimento (RF11)");
        setSize(850, 520);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Painel Superior (Formulário)
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBorder(BorderFactory.createTitledBorder("Novo Registro de Abastecimento"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Linha 0: Embarcação e Data (JSpinner)
        gbc.gridx = 0; gbc.gridy = 0; pnlForm.add(new JLabel("Embarcação:"), gbc);
        gbc.gridx = 1; cbEmbarcacoes = new JComboBox<>(); pnlForm.add(cbEmbarcacoes, gbc);

        gbc.gridx = 2; gbc.gridy = 0; pnlForm.add(new JLabel("Data do Abastecimento:"), gbc);
        gbc.gridx = 3;
        spData = new JSpinner(new SpinnerDateModel());
        spData.setEditor(new JSpinner.DateEditor(spData, "dd/MM/yyyy"));
        pnlForm.add(spData, gbc);

        // Linha 1: Litros e Valor Total
        gbc.gridx = 0; gbc.gridy = 1; pnlForm.add(new JLabel("Litros Abastecidos:"), gbc);
        gbc.gridx = 1; txtLitros = new JTextField(10); pnlForm.add(txtLitros, gbc);

        gbc.gridx = 2; gbc.gridy = 1; pnlForm.add(new JLabel("Valor Total (R$):"), gbc);
        gbc.gridx = 3; txtValorTotal = new JTextField(10); pnlForm.add(txtValorTotal, gbc);

        // Linha 2: Posto / Fornecedor e Botão Salvar
        gbc.gridx = 0; gbc.gridy = 2; pnlForm.add(new JLabel("Posto / Fornecedor:"), gbc);
        gbc.gridx = 1; cbFornecedores = new JComboBox<>(); pnlForm.add(cbFornecedores, gbc);

        gbc.gridx = 3; gbc.gridy = 2;
        JButton btnSalvar = new JButton("Salvar Abastecimento");
        btnSalvar.addActionListener(e -> salvar());
        pnlForm.add(btnSalvar, gbc);

        add(pnlForm, BorderLayout.NORTH);

        // Tabela Central (Histórico)
        modelTabela = new DefaultTableModel(new String[]{"ID", "Embarcação", "Data", "Litros", "Valor Total (R$)", "Posto / Fornecedor"}, 0);
        tabelaAbastecimentos = new JTable(modelTabela);
        add(new JScrollPane(tabelaAbastecimentos), BorderLayout.CENTER);

        carregarCombos();
        atualizarTabela();
    }

    private void carregarCombos() {
        cbEmbarcacoes.removeAllItems();
        cbFornecedores.removeAllItems();

        new EmbarcacaoDAO().listarTodas().forEach(cbEmbarcacoes::addItem);
        new FornecedorDAO().listarTodos().forEach(cbFornecedores::addItem);
    }

    private void atualizarTabela() {
        modelTabela.setRowCount(0);
        List<AbastecimentoDAO.AbastecimentoDTO> lista = abastecimentoDAO.listarTodos();
        for (AbastecimentoDAO.AbastecimentoDTO a : lista) {
            modelTabela.addRow(new Object[]{
                a.getId(),
                a.getNomeEmbarcacao(),
                a.getData().format(dateFormatter),
                String.format("%.2f L", a.getLitros()),
                String.format("R$ %.2f", a.getValorTotal()),
                a.getFornecedor()
            });
        }
    }

    private void salvar() {
        try {
            Embarcacao emb = (Embarcacao) cbEmbarcacoes.getSelectedItem();
            Fornecedor forn = (Fornecedor) cbFornecedores.getSelectedItem();

            if (emb == null || forn == null) {
                JOptionPane.showMessageDialog(this, "Selecione uma embarcação e um fornecedor.");
                return;
            }

            Date dateVal = (Date) spData.getValue();
            LocalDate data = dateVal.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            double litros = Double.parseDouble(txtLitros.getText().trim().replace(",", "."));
            double valorTotal = Double.parseDouble(txtValorTotal.getText().trim().replace(",", "."));

            Abastecimento abastecimento = new Abastecimento(emb.getId(), data, litros, valorTotal, forn.getNome());
            String resultado = controller.registrar(abastecimento);

            if ("OK".equals(resultado)) {
                JOptionPane.showMessageDialog(this, "Abastecimento registrado com sucesso!");
                txtLitros.setText("");
                txtValorTotal.setText("");
                spData.setValue(new Date());
                atualizarTabela();
            } else {
                JOptionPane.showMessageDialog(this, resultado, "Alerta", JOptionPane.WARNING_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Informe valores numéricos válidos para Litros e Valor Total.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao processar dados: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}