package view;

import controller.RelatorioController;
import controller.RelatorioController.CustoEmbarcacaoDTO;
import service.RelatorioPDFService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

public class TelaRelatorios extends JFrame {

    private final RelatorioController controller;
    private JTable tabela;
    private DefaultTableModel tableModel;
    private JLabel lblTotalGeral;

    public TelaRelatorios() {
        this.controller = new RelatorioController();
        setTitle("Gestão Náutica - Relatório de Custos de Manutenção");
        setSize(700, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponentes();
        carregarDados();
    }

    private void initComponentes() {
        setLayout(new BorderLayout(10, 10));

        // Cabeçalho
        JPanel panelHeader = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelHeader.setBackground(new Color(41, 128, 185));
        JLabel lblTitulo = new JLabel("Consolidado Financeiro de Manutenções por Embarcação");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 14));
        panelHeader.add(lblTitulo);
        add(panelHeader, BorderLayout.NORTH);

        // Tabela Central
        String[] colunas = {"Embarcação", "Manutenções Concluídas", "Custo Acumulado (R$)"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabela = new JTable(tableModel);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        // Rodapé com Totalizadores e Ações
        JPanel panelFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        lblTotalGeral = new JLabel("Custo Total Geral: R$ 0,00");
        lblTotalGeral.setFont(new Font("SansSerif", Font.BOLD, 13));
        
        JButton btnGerarPdf = new JButton("Gerar e Abrir PDF");
        btnGerarPdf.addActionListener(e -> gerarEAbrirPDF());

        JButton btnFechar = new JButton("Fechar");
        btnFechar.addActionListener(e -> dispose());

        panelFooter.add(lblTotalGeral);
        panelFooter.add(btnGerarPdf);
        panelFooter.add(btnFechar);
        add(panelFooter, BorderLayout.SOUTH);
    }

    private void carregarDados() {
        tableModel.setRowCount(0);
        List<CustoEmbarcacaoDTO> dados = controller.obterConsolidadoCustos();
        double totalGeral = 0;

        for (CustoEmbarcacaoDTO item : dados) {
            tableModel.addRow(new Object[]{
                item.getNomeEmbarcacao(),
                item.getTotalManutencoes(),
                String.format("R$ %.2f", item.getCustoTotal())
            });
            totalGeral += item.getCustoTotal();
        }

        lblTotalGeral.setText(String.format("Custo Total Geral: R$ %.2f", totalGeral));
    }

    private void gerarEAbrirPDF() {
        try {
            List<CustoEmbarcacaoDTO> dados = controller.obterConsolidadoCustos();
            double totalGeral = dados.stream().mapToDouble(CustoEmbarcacaoDTO::getCustoTotal).sum();
            
            File pdf = RelatorioPDFService.gerarPDF(dados, totalGeral);
            
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(pdf);
            }
            
            JOptionPane.showMessageDialog(this, "PDF gerado e aberto com sucesso!\nSalvo em: " + pdf.getAbsolutePath());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao gerar PDF: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}