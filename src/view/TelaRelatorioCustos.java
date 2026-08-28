package view;

import dao.RelatorioDAO;
import service.EmailService;
import service.GeradorPDFService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class TelaRelatorioCustos extends JFrame {

    private final RelatorioDAO dao = new RelatorioDAO();
    private JTable tblCustos;
    private DefaultTableModel tableModel;
    private List<Object[]> dadosCarregados;

    public TelaRelatorioCustos() {
        setTitle("Relatório e Indicadores de Custos (ADM)");
        setSize(900, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        add(criarTabela(), BorderLayout.CENTER);
        add(criarBarraAcoes(), BorderLayout.SOUTH);

        carregarDados();
    }

    private JScrollPane criarTabela() {
        tableModel = new DefaultTableModel(new String[]{"ID", "Embarcação", "Manutenção (R$)", "Abastecimento (R$)", "Custo Total (R$)"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblCustos = new JTable(tableModel);
        return new JScrollPane(tblCustos);
    }

    private JPanel criarBarraAcoes() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));

        JButton btnBaixarPDF = new JButton("Gerar e Baixar PDF (Downloads)");
        btnBaixarPDF.setBackground(new Color(41, 128, 185));
        btnBaixarPDF.setForeground(Color.WHITE);

        JButton btnEnviarEmail = new JButton("Enviar por E-mail");
        btnEnviarEmail.setBackground(new Color(39, 174, 96));
        btnEnviarEmail.setForeground(Color.WHITE);

        btnBaixarPDF.addActionListener(e -> baixarPDF());
        btnEnviarEmail.addActionListener(e -> enviarEmail());

        panel.add(btnBaixarPDF);
        panel.add(btnEnviarEmail);
        return panel;
    }

    private void carregarDados() {
        tableModel.setRowCount(0);
        dadosCarregados = dao.obterResumoCustosPorEmbarcacao();
        NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));

        for (Object[] row : dadosCarregados) {
            tableModel.addRow(new Object[]{
                row[0], row[1],
                nf.format((double) row[2]),
                nf.format((double) row[3]),
                nf.format((double) row[4])
            });
        }
    }

    private void baixarPDF() {
        try {
            String caminhoPDF = GeradorPDFService.gerarRelatorioCustos(dadosCarregados);
            JOptionPane.showMessageDialog(this, "PDF gerado com sucesso na pasta Downloads!\nCaminho: " + caminhoPDF);
            Desktop.getDesktop().open(new File(caminhoPDF));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao gerar PDF: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void enviarEmail() {
        String emailDestino = JOptionPane.showInputDialog(this, "Informe o e-mail do destinatário:", "Enviar Relatório", JOptionPane.QUESTION_MESSAGE);
        if (emailDestino != null && !emailDestino.trim().isEmpty()) {
            try {
                String caminhoPDF = GeradorPDFService.gerarRelatorioCustos(dadosCarregados);
                boolean enviado = EmailService.enviarRelatorioPorEmail(emailDestino.trim(), caminhoPDF);
                if (enviado) {
                    JOptionPane.showMessageDialog(this, "E-mail enviado com sucesso para " + emailDestino);
                } else {
                    JOptionPane.showMessageDialog(this, "Falha ao enviar e-mail. Verifique as credenciais SMTP no EmailService.", "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro na operação: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
