package view;

import dao.AbastecimentoDAO;
import dao.EmbarcacaoDAO;
import model.Abastecimento;
import model.Embarcacao;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TelaAbastecimento extends JFrame {

    private JComboBox<Embarcacao> cbEmbarcacoes;
    private JTextField txtData;
    private JTextField txtLitros;
    private JTextField txtValorTotal;
    private JTextField txtFornecedor;

    private final AbastecimentoDAO abastecimentoDAO = new AbastecimentoDAO();
    private final EmbarcacaoDAO embarcacaoDAO = new EmbarcacaoDAO();

    public TelaAbastecimento() {
        setTitle("Registrar Abastecimento - Operador (RF11)");
        setSize(450, 320);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        formPanel.add(new JLabel("Embarcação:"));
        cbEmbarcacoes = new JComboBox<>();
        carregarEmbarcacoes();
        formPanel.add(cbEmbarcacoes);

        formPanel.add(new JLabel("Data (dd/MM/yyyy):"));
        txtData = new JTextField(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        formPanel.add(txtData);

        formPanel.add(new JLabel("Litros Abastecidos:"));
        txtLitros = new JTextField();
        formPanel.add(txtLitros);

        formPanel.add(new JLabel("Valor Total (R$):"));
        txtValorTotal = new JTextField();
        formPanel.add(txtValorTotal);

        formPanel.add(new JLabel("Posto / Fornecedor:"));
        txtFornecedor = new JTextField();
        formPanel.add(txtFornecedor);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSalvar = new JButton("Salvar Abastecimento");
        btnSalvar.addActionListener(e -> salvar());
        buttonPanel.add(btnSalvar);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void carregarEmbarcacoes() {
        List<Embarcacao> lista = embarcacaoDAO.listarTodas();
        for (Embarcacao e : lista) {
            cbEmbarcacoes.addItem(e);
        }
    }

    private void salvar() {
        try {
            Embarcacao emb = (Embarcacao) cbEmbarcacoes.getSelectedItem();
            if (emb == null) {
                JOptionPane.showMessageDialog(this, "Selecione uma embarcação.");
                return;
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate data = LocalDate.parse(txtData.getText().trim(), formatter);
            double litros = Double.parseDouble(txtLitros.getText().trim().replace(",", "."));
            double valorTotal = Double.parseDouble(txtValorTotal.getText().trim().replace(",", "."));
            String fornecedor = txtFornecedor.getText().trim();

            if (fornecedor.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Informe o fornecedor.");
                return;
            }

            Abastecimento abastecimento = new Abastecimento(emb.getId(), data, litros, valorTotal, fornecedor);
            boolean sucesso = abastecimentoDAO.salvar(abastecimento);

            if (sucesso) {
                JOptionPane.showMessageDialog(this, "Abastecimento registrado com sucesso!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao salvar no banco de dados.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Verifique os dados informados: " + ex.getMessage(), "Erro de Validação", JOptionPane.WARNING_MESSAGE);
        }
    }
}
