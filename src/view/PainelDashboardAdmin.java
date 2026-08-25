package view;

import controller.DashboardAdminController;
import dao.EmbarcacaoDAO;
import dto.CustoConsolidadoDTO;
import model.Embarcacao;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class PainelDashboardAdmin extends JPanel {

    private final DashboardAdminController controller = new DashboardAdminController();
    private final EmbarcacaoDAO embarcacaoDAO = new EmbarcacaoDAO();

    private JComboBox<Embarcacao> cbEmbarcacoes;
    private JLabel lblManutencaoVal;
    private JLabel lblAbastecimentoVal;
    private JLabel lblCustoTotalVal;

    public PainelDashboardAdmin() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Painel Superior: Filtro de Embarcações
        JPanel pnlFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pnlFiltros.add(new JLabel("Selecionar Embarcação:"));

        cbEmbarcacoes = new JComboBox<>();
        carregarEmbarcacoes();
        cbEmbarcacoes.addActionListener(e -> atualizarValores());
        pnlFiltros.add(cbEmbarcacoes);

        // Painel Central: Cards KPI
        JPanel pnlCards = new JPanel(new GridLayout(1, 3, 15, 0));
        pnlCards.add(criarCardKPI("Custos com Manutenção", "R$ 0,00", new Color(220, 53, 69)));
        pnlCards.add(criarCardKPI("Custos com Combustível", "R$ 0,00", new Color(255, 193, 7)));
        pnlCards.add(criarCardKPI("Custo Operacional Total", "R$ 0,00", new Color(40, 167, 69)));

        add(pnlFiltros, BorderLayout.NORTH);
        add(pnlCards, BorderLayout.CENTER);

        // Carrega os dados iniciais da primeira embarcação
        if (cbEmbarcacoes.getItemCount() > 0) {
            atualizarValores();
        }
    }

    private void carregarEmbarcacoes() {
        cbEmbarcacoes.removeAllItems();
        List<Embarcacao> lista = embarcacaoDAO.listarTodas();
        for (Embarcacao emb : lista) {
            cbEmbarcacoes.addItem(emb);
        }
    }

    private JPanel criarCardKPI(String titulo, String valorInicial, Color corBorda) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 5, 0, 0, corBorda),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblTitulo.setForeground(Color.GRAY);

        JLabel lblValor = new JLabel(valorInicial);
        lblValor.setFont(new Font("SansSerif", Font.BOLD, 22));

        if (titulo.contains("Manutenção")) lblManutencaoVal = lblValor;
        else if (titulo.contains("Combustível")) lblAbastecimentoVal = lblValor;
        else if (titulo.contains("Total")) lblCustoTotalVal = lblValor;

        card.add(lblTitulo, BorderLayout.NORTH);
        card.add(lblValor, BorderLayout.CENTER);

        return card;
    }

    private void atualizarValores() {
        Embarcacao selecionada = (Embarcacao) cbEmbarcacoes.getSelectedItem();
        if (selecionada != null) {
            // Período de consulta: ano corrente
            LocalDate inicio = LocalDate.of(LocalDate.now().getYear(), 1, 1);
            LocalDate fim = LocalDate.now();

            CustoConsolidadoDTO dto = controller.obterResumoFinanceiro(
                selecionada.getId(),
                selecionada.getNome(),
                inicio,
                fim
            );

            lblManutencaoVal.setText(String.format("R$ %.2f", dto.getTotalManutencao()));
            lblAbastecimentoVal.setText(String.format("R$ %.2f", dto.getTotalAbastecimento()));
            lblCustoTotalVal.setText(String.format("R$ %.2f", dto.getTotalGeral()));
        }
    }
}