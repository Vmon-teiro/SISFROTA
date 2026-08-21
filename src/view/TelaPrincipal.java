package view;

import model.Usuario;

import javax.swing.*;
import java.awt.*;

public class TelaPrincipal extends JFrame {

    private final Usuario usuarioLogado;

    public TelaPrincipal(Usuario usuario) {
        this.usuarioLogado = usuario;

        setTitle("Gestão Náutica - Painel Principal");
        setSize(700, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponentes();
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
        JPanel panelMenu = new JPanel(new GridLayout(2, 2, 15, 15));
        panelMenu.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        switch (usuarioLogado.getPerfil()) {
            case "ADMINISTRADOR":
                panelMenu.add(new JButton("Gerenciar Embarcações"));
                panelMenu.add(new JButton("Gerenciar Tripulação"));
                panelMenu.add(new JButton("Relatórios de Custos"));
                panelMenu.add(new JButton("Documentações e Vistorias"));
                break;

            case "OPERADOR":
                panelMenu.add(new JButton("Registrar Viagem"));
                panelMenu.add(new JButton("Registrar Abastecimento"));
                panelMenu.add(new JButton("Registrar Incidente"));
                panelMenu.add(new JButton("Consultar Horários"));
                break;

            case "TECNICO":
                panelMenu.add(new JButton("Ordens de Serviço / Manutenção"));
                panelMenu.add(new JButton("Alertas de Horímetro"));
                panelMenu.add(new JButton("Histórico de Motores"));
                panelMenu.add(new JButton("Agendar Revisão"));
                break;
        }

        add(panelMenu, BorderLayout.CENTER);
    }
}
