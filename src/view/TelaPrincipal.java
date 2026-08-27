package view;

import controller.AlertaController;
import model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TelaPrincipal extends JFrame {

    private final Usuario usuarioLogado;

    public TelaPrincipal(Usuario usuario) {
        this.usuarioLogado = usuario;
        setTitle("Gestão Náutica - Painel Principal");
        setSize(700, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initComponentes();
        verificarEExibirAlertas(); // Chamada dos alertas automáticos (RN03)
    }

    private void verificarEExibirAlertas() {
        SwingUtilities.invokeLater(() -> {
            AlertaController alertaController = new AlertaController();
            List<String> alertas = alertaController.verificarAlertasVencimento();

            if (!alertas.isEmpty()) {
                StringBuilder mensagem = new StringBuilder("Atenção! Existem pendências com vencimento próximo (15 dias):\n\n");
                for (String alerta : alertas) {
                    mensagem.append(alerta).append("\n");
                }
                JOptionPane.showMessageDialog(this, 
                    mensagem.toString(), 
                    "Alertas do Sistema (RN03)", 
                    JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    private void initComponentes() {
        setLayout(new BorderLayout());

        // Barra Superior com informações do Usuário
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(new Color(41, 128, 185));
        panelHeader.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel lblBemVindo = new JLabel("Usuário: " + usuarioLogado.getNome() + " | Perfil: " + usuarioLogado.getPerfil());
        lblBemVindo.setForeground(Color.WHITE);
        lblBemVindo.setFont(new Font("SansSerif", Font.BOLD, 14));

        JButton btnSair = new JButton("Sair / Logout");
        btnSair.addActionListener(e -> {
            new TelaLogin().setVisible(true);
            dispose();
        });

        panelHeader.add(lblBemVindo, BorderLayout.WEST);
        panelHeader.add(btnSair, BorderLayout.EAST);
        add(panelHeader, BorderLayout.NORTH);

        // Painel Central Dinâmico baseado no Perfil
        JPanel panelMenu = new JPanel(new GridLayout(0, 2, 15, 15));
        panelMenu.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        switch (usuarioLogado.getPerfil()) {
            case "ADMINISTRADOR":
               JButton btnDashboardAdmin = new JButton("Dashboard / Indicadores");
               btnDashboardAdmin.addActionListener(e -> new TelaDashboardAdmin().setVisible(true));
               panelMenu.add(btnDashboardAdmin);

               JButton btnEmbarcacoesAdmin = new JButton("Gerenciar Embarcações");
               btnEmbarcacoesAdmin.addActionListener(e -> new TelaEmbarcacoes().setVisible(true));
               panelMenu.add(btnEmbarcacoesAdmin);

               JButton btnManutencaoAdmin = new JButton("Manutenções e Preventivas");
               btnManutencaoAdmin.addActionListener(e -> new TelaManutencoes().setVisible(true));
               panelMenu.add(btnManutencaoAdmin);

               JButton btnTripulacaoAdmin = new JButton("Gerenciar Tripulação");
               btnTripulacaoAdmin.addActionListener(e -> new TelaTripulantes().setVisible(true));
               panelMenu.add(btnTripulacaoAdmin);
 
               JButton btnRelatoriosAdmin = new JButton("Relatórios de Custos (PDF)");
               btnRelatoriosAdmin.addActionListener(e -> new TelaRelatorios().setVisible(true));
               panelMenu.add(btnRelatoriosAdmin);
               break;

            case "OPERADOR":
                   JButton btnViagemOp = new JButton("Registrar Viagem");
                   btnViagemOp.addActionListener(e -> new TelaViagens().setVisible(true));
                   panelMenu.add(btnViagemOp);

                   JButton btnAbastecimentoOp = new JButton("Registrar Abastecimento");
                   btnAbastecimentoOp.addActionListener(e -> new TelaAbastecimento().setVisible(true));
                   panelMenu.add(btnAbastecimentoOp);

                   JButton btnIncidenteOp = new JButton("Registrar Incidente");
                   btnIncidenteOp.addActionListener(e -> 
                   JOptionPane.showMessageDialog(this, "Tela de Incidentes em desenvolvimento.", "Aviso", JOptionPane.INFORMATION_MESSAGE));
                   panelMenu.add(btnIncidenteOp);

                   JButton btnHorariosOp = new JButton("Consultar Horários");
                   btnHorariosOp.addActionListener(e -> 
                   JOptionPane.showMessageDialog(this, "Consulta de Horários em desenvolvimento.", "Aviso", JOptionPane.INFORMATION_MESSAGE));
                   panelMenu.add(btnHorariosOp);
                   break;

            case "TECNICO":
                JButton btnManutencaoTecnico = new JButton("Ordens de Serviço / Manutenção");
                btnManutencaoTecnico.addActionListener(e -> new TelaManutencoes().setVisible(true));
                panelMenu.add(btnManutencaoTecnico);

                JButton btnAlertas = new JButton("Alertas de Horímetro");
                btnAlertas.addActionListener(e -> 
                JOptionPane.showMessageDialog(this, "Tela de Alertas em desenvolvimento.", "Aviso", JOptionPane.INFORMATION_MESSAGE));
                panelMenu.add(btnAlertas);

                JButton btnHistorico = new JButton("Histórico de Motores");
                btnHistorico.addActionListener(e -> 
                JOptionPane.showMessageDialog(this, "Histórico em desenvolvimento.", "Aviso", JOptionPane.INFORMATION_MESSAGE));
                panelMenu.add(btnHistorico);

                JButton btnRevisao = new JButton("Agendar Revisão");
                btnRevisao.addActionListener(e -> new TelaManutencoes().setVisible(true));
                panelMenu.add(btnRevisao);
                break;
        }

        add(panelMenu, BorderLayout.CENTER);
    }
}