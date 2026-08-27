package view;

import controller.ViagemController;
import dao.EmbarcacaoDAO;
import dao.RotaDAO;
import dao.TripulanteDAO;
import dao.ViagemDAO;
import model.Embarcacao;
import model.Tripulante;
import model.Viagem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class TelaViagens extends JFrame {

    private JComboBox<Embarcacao> cbEmbarcacoes;
    private JComboBox<Tripulante> cbComandantes;
    private JComboBox<String> cbDestinos;
    private JTextField txtPassageiros;
    private JSpinner spDataPartida;
    private JSpinner spHorarioPartida;
    private JTable tabelaViagens;
    private DefaultTableModel modelTabela;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final ViagemController controller = new ViagemController();
    private final ViagemDAO viagemDAO = new ViagemDAO();
    private final RotaDAO rotaDAO = new RotaDAO();

    public TelaViagens() {
        setTitle("Registro e Controle de Viagens (RF05)");
        setSize(920, 580);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBorder(BorderFactory.createTitledBorder("Nova Viagem"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Linha 0: Embarcação e Comandante
        gbc.gridx = 0; gbc.gridy = 0; pnlForm.add(new JLabel("Embarcação:"), gbc);
        gbc.gridx = 1; cbEmbarcacoes = new JComboBox<>(); pnlForm.add(cbEmbarcacoes, gbc);

        gbc.gridx = 2; gbc.gridy = 0; pnlForm.add(new JLabel("Comandante:"), gbc);
        gbc.gridx = 3; cbComandantes = new JComboBox<>(); pnlForm.add(cbComandantes, gbc);

        // Linha 1: Rota e Passageiros
        gbc.gridx = 0; gbc.gridy = 1; pnlForm.add(new JLabel("Rota / Destino:"), gbc);
        gbc.gridx = 1; cbDestinos = new JComboBox<>(); pnlForm.add(cbDestinos, gbc);

        gbc.gridx = 2; gbc.gridy = 1; pnlForm.add(new JLabel("Passageiros:"), gbc);
        gbc.gridx = 3; txtPassageiros = new JTextField(10); pnlForm.add(txtPassageiros, gbc);

        // Linha 2: Data da Partida e Horário com seletores numéricos/data
        gbc.gridx = 0; gbc.gridy = 2; pnlForm.add(new JLabel("Data da Partida:"), gbc);
        gbc.gridx = 1;
        spDataPartida = new JSpinner(new SpinnerDateModel());
        spDataPartida.setEditor(new JSpinner.DateEditor(spDataPartida, "dd/MM/yyyy"));
        pnlForm.add(spDataPartida, gbc);

        gbc.gridx = 2; gbc.gridy = 2; pnlForm.add(new JLabel("Horário:"), gbc);
        gbc.gridx = 3;
        spHorarioPartida = new JSpinner(new SpinnerDateModel());
        spHorarioPartida.setEditor(new JSpinner.DateEditor(spHorarioPartida, "HH:mm"));
        pnlForm.add(spHorarioPartida, gbc);

        // Linha 3: Botão Salvar
        gbc.gridx = 3; gbc.gridy = 3;
        JButton btnSalvar = new JButton("Registrar Viagem");
        btnSalvar.addActionListener(e -> salvarViagem());
        pnlForm.add(btnSalvar, gbc);

        add(pnlForm, BorderLayout.NORTH);

        // Tabela Central
        modelTabela = new DefaultTableModel(new String[]{"ID", "Embarcação", "Comandante", "Destino", "Passageiros", "Partida", "Chegada", "Status"}, 0);
        tabelaViagens = new JTable(modelTabela);
        add(new JScrollPane(tabelaViagens), BorderLayout.CENTER);

        // Ações da Tabela
        JPanel pnlAcoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnConcluir = new JButton("Concluir Viagem Selecionada");
        JButton btnCancelar = new JButton("Cancelar Viagem");

        btnConcluir.addActionListener(e -> alterarStatusViagem(true));
        btnCancelar.addActionListener(e -> alterarStatusViagem(false));

        pnlAcoes.add(btnConcluir);
        pnlAcoes.add(btnCancelar);
        add(pnlAcoes, BorderLayout.SOUTH);

        carregarCombos();
        atualizarTabela();
    }

    private void carregarCombos() {
        cbEmbarcacoes.removeAllItems();
        cbComandantes.removeAllItems();
        cbDestinos.removeAllItems();

        new EmbarcacaoDAO().listarTodas().forEach(cbEmbarcacoes::addItem);
        new TripulanteDAO().listarTodos().forEach(cbComandantes::addItem);
        rotaDAO.listarTodas().forEach(cbDestinos::addItem);
    }

    private void atualizarTabela() {
        modelTabela.setRowCount(0);
        List<Viagem> lista = viagemDAO.listarTodas();
        for (Viagem v : lista) {
            modelTabela.addRow(new Object[]{
                v.getId(),
                v.getNomeEmbarcacao(),
                v.getNomeComandante(),
                v.getRotaDestino(),
                v.getQuantidadePassageiros(),
                v.getDataHoraPartida() != null ? v.getDataHoraPartida().format(formatter) : "-",
                v.getDataHoraChegada() != null ? v.getDataHoraChegada().format(formatter) : "-",
                v.getStatus()
            });
        }
    }

    private void salvarViagem() {
        try {
            Embarcacao emb = (Embarcacao) cbEmbarcacoes.getSelectedItem();
            Tripulante trip = (Tripulante) cbComandantes.getSelectedItem();
            String destino = (String) cbDestinos.getSelectedItem();

            if (emb == null || trip == null || destino == null || destino.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha todos os campos obrigatórios.");
                return;
            }

            int passageiros = Integer.parseInt(txtPassageiros.getText().trim());

            // Converte os valores dos seletores para LocalDateTime
            Date dateData = (Date) spDataPartida.getValue();
            Date dateHora = (Date) spHorarioPartida.getValue();

            LocalDate localDate = dateData.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalTime localTime = dateHora.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
            LocalDateTime partida = LocalDateTime.of(localDate, localTime);

            Viagem v = new Viagem(emb.getId(), trip.getId(), destino, partida, passageiros);

            String resultado = controller.registrarViagem(v, emb);

            if ("OK".equals(resultado)) {
                JOptionPane.showMessageDialog(this, "Viagem registrada com sucesso!");
                txtPassageiros.setText("");
                spDataPartida.setValue(new Date());
                spHorarioPartida.setValue(new Date());
                atualizarTabela();
            } else {
                JOptionPane.showMessageDialog(this, resultado, "Alerta de Regra de Negócio", JOptionPane.WARNING_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Informe um número inteiro válido para os passageiros.", "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao processar os campos da viagem.", "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void alterarStatusViagem(boolean concluir) {
        int linha = tabelaViagens.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma viagem na tabela.");
            return;
        }

        int idViagem = (int) modelTabela.getValueAt(linha, 0);
        String statusAtual = (String) modelTabela.getValueAt(linha, 7);

        if (!"EM_ANDAMENTO".equalsIgnoreCase(statusAtual)) {
            JOptionPane.showMessageDialog(this, "Apenas viagens 'EM_ANDAMENTO' podem ser alteradas.");
            return;
        }

        boolean ok = concluir ? viagemDAO.finalizarViagem(idViagem) : viagemDAO.cancelarViagem(idViagem);

        if (ok) {
            JOptionPane.showMessageDialog(this, "Status da viagem atualizado!");
            atualizarTabela();
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar a viagem no banco.");
        }
    }
}