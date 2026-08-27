package view;

import controller.ConsultaHorariosController;
import dto.ConsultaHorarioDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TelaConsultaHorarios extends JFrame {

    private JTable tabela;
    private DefaultTableModel model;

    private JComboBox<String> cbEmbarcacao;
    private JComboBox<String> cbComandante;
    private JComboBox<String> cbDestino;

    private final ConsultaHorariosController controller = new ConsultaHorariosController();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public TelaConsultaHorarios() {
        setTitle("Consulta de Horários e Saídas de Viagens");
        setSize(980, 520);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Painel de Filtros Selecionáveis
        JPanel panelTopo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelTopo.setBorder(BorderFactory.createTitledBorder(" Selecionar Filtros "));

        panelTopo.add(new JLabel("Embarcação:"));
        cbEmbarcacao = new JComboBox<>();
        panelTopo.add(cbEmbarcacao);

        panelTopo.add(new JLabel("Comandante:"));
        cbComandante = new JComboBox<>();
        panelTopo.add(cbComandante);

        panelTopo.add(new JLabel("Destino:"));
        cbDestino = new JComboBox<>();
        panelTopo.add(cbDestino);

        JButton btnFiltrar = new JButton("Filtrar");
        btnFiltrar.addActionListener(e -> carregarTabela());
        panelTopo.add(btnFiltrar);

        JButton btnLimpar = new JButton("Limpar Filtros");
        btnLimpar.addActionListener(e -> limparEFiltrar());
        panelTopo.add(btnLimpar);

        add(panelTopo, BorderLayout.NORTH);

        // Tabela
        String[] colunas = {"ID", "Embarcação", "Comandante", "Destino", "Partida", "Chegada", "Passageiros", "Status"};
        model = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabela = new JTable(model);
        tabela.setRowHeight(22);

        JScrollPane scrollPane = new JScrollPane(tabela);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);

        carregarOpcoesFiltros();
        carregarTabela();
    }

    private void carregarOpcoesFiltros() {
        cbEmbarcacao.removeAllItems();
        cbComandante.removeAllItems();
        cbDestino.removeAllItems();

        controller.obterEmbarcacoes().forEach(cbEmbarcacao::addItem);
        controller.obterComandantes().forEach(cbComandante::addItem);
        controller.obterDestinos().forEach(cbDestino::addItem);
    }

    private void limparEFiltrar() {
        carregarOpcoesFiltros();
        cbEmbarcacao.setSelectedIndex(0);
        cbComandante.setSelectedIndex(0);
        cbDestino.setSelectedIndex(0);
        carregarTabela();
    }

    private void carregarTabela() {
        model.setRowCount(0);

        String selEmbarcacao = (String) cbEmbarcacao.getSelectedItem();
        String selComandante = (String) cbComandante.getSelectedItem();
        String selDestino = (String) cbDestino.getSelectedItem();

        List<ConsultaHorarioDTO> viagens = controller.buscarHorarios(selEmbarcacao, selComandante, selDestino);

        for (ConsultaHorarioDTO v : viagens) {
            String partidaStr = v.getDataHoraPartida() != null ? v.getDataHoraPartida().format(formatter) : "-";
            String chegadaStr = v.getDataHoraChegada() != null ? v.getDataHoraChegada().format(formatter) : "Em Trânsito";

            model.addRow(new Object[]{
                v.getIdViagem(),
                v.getEmbarcacao(),
                v.getComandante(),
                v.getRotaDestino(),
                partidaStr,
                chegadaStr,
                v.getQuantidadePassageiros(),
                v.getStatus()
            });
        }
    }
}