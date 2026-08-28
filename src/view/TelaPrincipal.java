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
        setSize(800, 500);
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
                    mensagem.append("• ").append(alerta).append("\n");
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
        panelHeader.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));

        JLabel lblBemVindo = new JLabel("Usuário: " + usuarioLogado.getNome() + "  |  Perfil: " + usuarioLogado.getPerfil());
        lblBemVindo.setForeground(Color.WHITE);
        lblBemVindo.setFont(new Font("SansSerif", Font.BOLD, 14));

        JButton btnSair = new JButton("Sair / Logout");
        btnSair.setFocusPainted(false);
        btnSair.addActionListener(e -> {
            new TelaLogin().setVisible(true);
            dispose();
        });

        panelHeader.add(lblBemVindo, BorderLayout.WEST);
        panelHeader.add(btnSair, BorderLayout.EAST);
        add(panelHeader, BorderLayout.NORTH);

        // Painel Central Dinâmico baseado no Perfil
        JPanel panelMenu = new JPanel(new GridLayout(0, 2, 15, 15));
        panelMenu.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        String perfil = usuarioLogado.getPerfil() != null ? usuarioLogado.getPerfil().toUpperCase() : "";

        switch (perfil) {
            case "ADMINISTRADOR":
                panelMenu.add(criarBotaoMenu("Gerenciar Embarcações", e -> new TelaGerenciarEmbarcacoes().setVisible(true)));
                panelMenu.add(criarBotaoMenu("Manutenções e Preventivas", e -> new TelaManutencoes().setVisible(true)));
                panelMenu.add(criarBotaoMenu("Gerenciar Tripulação", e -> new TelaGerenciarTripulacao().setVisible(true)));
                panelMenu.add(criarBotaoMenu("Relatórios de Custos (PDF)", e -> new TelaRelatorioCustos().setVisible(true)));
                break;

            case "OPERADOR":
                panelMenu.add(criarBotaoMenu("Painel Operacional Integrado", e -> new TelaDashboardOperador().setVisible(true)));
                break;

            case "TECNICO":
                panelMenu.add(criarBotaoMenu("Serviços Gerais", e -> new TelaDashboardTecnico().setVisible(true)));
                break;
                
            default:
                JOptionPane.showMessageDialog(this, "Perfil de usuário não reconhecido.", "Erro de Permissão", JOptionPane.ERROR_MESSAGE);
                break;
        }

        add(panelMenu, BorderLayout.CENTER);
    }

    private JButton criarBotaoMenu(String texto, java.awt.event.ActionListener acao) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.addActionListener(acao);
        return btn;
    }
}