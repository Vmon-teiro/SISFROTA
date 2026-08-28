package view;

import controller.AlertaController;
import model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TelaPrincipal extends JFrame {

    private final Usuario usuarioLogado;
    private List<String> alertasAtivos = new ArrayList<>();
    private BotaoNotificacao btnNotificacoes;

    public TelaPrincipal(Usuario usuario) {
        this.usuarioLogado = usuario;
        setTitle("Gestão Náutica - Painel Principal");
        setSize(900, 580);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initComponentes();
        verificarEExibirAlertas();
    }

    private void verificarEExibirAlertas() {
        SwingUtilities.invokeLater(() -> {
            AlertaController alertaController = new AlertaController();
            alertasAtivos = alertaController.verificarAlertasVencimento();

            if (!alertasAtivos.isEmpty()) {
                // Ativa a bolinha vermelha no botão de notificações do topo
                btnNotificacoes.setNotificacao(true, alertasAtivos.size());
                
                exibirDialogoAlertas();
            } else {
                btnNotificacoes.setNotificacao(false, 0);
            }
        });
    }

    private void exibirDialogoAlertas() {
        if (alertasAtivos.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Nenhuma pendência ou alerta no momento.", 
                "Central de Notificações", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder mensagem = new StringBuilder("Atenção! Existem pendências com vencimento próximo (15 dias):\n\n");
        for (String alerta : alertasAtivos) {
            mensagem.append("• ").append(alerta).append("\n");
        }
        JOptionPane.showMessageDialog(this, 
            mensagem.toString(), 
            "Alertas do Sistema (RN03)", 
            JOptionPane.WARNING_MESSAGE);
    }

    private void initComponentes() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(245, 247, 250));

        // --- BARRA SUPERIOR (HEADER) ---
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(new Color(30, 41, 59)); // Azul Escuro Slate Moderno
        panelHeader.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        // Informações do Usuário
        JLabel lblBemVindo = new JLabel("<html><span style='color: #94A3B8; font-size: 10pt;'>Bem-vindo(a),</span><br>" +
                "<span style='color: #FFFFFF; font-size: 13pt;'><b>" + usuarioLogado.getNome() + "</b></span> " +
                "<span style='color: #38BDF8; font-size: 10pt;'>(" + usuarioLogado.getPerfil() + ")</span></html>");

        // Painel de Ações do Header (Notificações + Logout)
        JPanel pnlAcoesTopo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        pnlAcoesTopo.setOpaque(false);

        btnNotificacoes = new BotaoNotificacao("🔔 Notifications");
        btnNotificacoes.setToolTipText("Clique para ver os alertas pendentes");
        btnNotificacoes.addActionListener(e -> exibirDialogoAlertas());

        JButton btnSair = new JButton("Sair");
        btnSair.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnSair.setForeground(new Color(239, 68, 68));
        btnSair.setBackground(Color.WHITE);
        btnSair.setFocusPainted(false);
        btnSair.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSair.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btnSair.addActionListener(e -> {
            new TelaLogin().setVisible(true);
            dispose();
        });

        pnlAcoesTopo.add(btnNotificacoes);
        pnlAcoesTopo.add(btnSair);

        panelHeader.add(lblBemVindo, BorderLayout.WEST);
        panelHeader.add(pnlAcoesTopo, BorderLayout.EAST);
        add(panelHeader, BorderLayout.NORTH);

        // --- PAINEL CENTRAL (CARDS DE MENU) ---
        String perfil = usuarioLogado.getPerfil() != null ? usuarioLogado.getPerfil().toUpperCase() : "";
        
        // Ajusta o Grid dinamicamente conforme o número de botões
        int colunas = perfil.equals("ADMINISTRADOR") ? 2 : 1;
        JPanel panelMenu = new JPanel(new GridLayout(0, colunas, 20, 20));
        panelMenu.setOpaque(false);
        panelMenu.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        switch (perfil) {
            case "ADMINISTRADOR":
                panelMenu.add(criarCardMenu("Gerenciar Embarcações", "Cadastro e controle de frotas", e -> new TelaGerenciarEmbarcacoes().setVisible(true)));
                panelMenu.add(criarCardMenu("Manutenções e Preventivas", "Ordens de serviço e chamados", e -> new TelaManutencoes().setVisible(true)));
                panelMenu.add(criarCardMenu("Gerenciar Tripulação", "Tripulantes e habilitações", e -> new TelaGerenciarTripulacao().setVisible(true)));
                panelMenu.add(criarCardMenu("Relatórios de Custos (PDF)", "Exportação de despesas operacionais", e -> new TelaRelatorioCustos().setVisible(true)));
                break;

            case "OPERADOR":
                panelMenu.add(criarCardMenu("Painel Operacional Integrado", "Lançamento de incidentes e diário de bordo", e -> new TelaDashboardOperador().setVisible(true)));
                break;

            case "TECNICO":
                panelMenu.add(criarCardMenu("Serviços Gerais", "Execução de ordens de serviço atribuídas", e -> new TelaDashboardTecnico().setVisible(true)));
                break;

            default:
                JOptionPane.showMessageDialog(this, "Perfil de usuário não reconhecido.", "Erro de Permissão", JOptionPane.ERROR_MESSAGE);
                break;
        }

        add(panelMenu, BorderLayout.CENTER);
    }

    private JButton criarCardMenu(String titulo, String subtitulo, java.awt.event.ActionListener acao) {
        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Fundo do Card
                if (getModel().isRollover()) {
                    g2.setColor(new Color(238, 242, 255)); // Azul claro no hover
                } else {
                    g2.setColor(Color.WHITE);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);

                // Borda suave
                g2.setColor(getModel().isRollover() ? new Color(99, 102, 241) : new Color(226, 232, 240));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);

                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setLayout(new BorderLayout());
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lblConteudo = new JLabel("<html><body style='padding: 15px;'>" +
                "<h3 style='color: #1E293B; margin: 0; font-size: 13pt;'>" + titulo + "</h3>" +
                "<p style='color: #64748B; margin-top: 5px; font-size: 9pt;'>" + subtitulo + "</p>" +
                "</body></html>");
        
        btn.add(lblConteudo, BorderLayout.CENTER);
        btn.addActionListener(acao);

        return btn;
    }

    // --- COMPONENTE CUSTOMIZADO: BOTÃO DE NOTIFICAÇÃO COM BOLINHA VERMELHA ---
    private static class BotaoNotificacao extends JButton {
        private boolean temNotificacao = false;
        private int quantidade = 0;

        public BotaoNotificacao(String texto) {
            super(texto);
            setFont(new Font("SansSerif", Font.BOLD, 12));
            setForeground(Color.WHITE);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 20));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        public void setNotificacao(boolean ativa, int qtd) {
            this.temNotificacao = ativa;
            this.quantidade = qtd;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Fundo do Botão
            g2.setColor(new Color(51, 65, 85));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

            super.paintComponent(g2);

            // Desenha a Bolinha Vermelha (Badge) se houver notificação
            if (temNotificacao) {
                int raio = 18;
                int x = getWidth() - raio - 2;
                int y = 2;

                // Círculo Vermelho
                g2.setColor(new Color(239, 68, 68));
                g2.fillOval(x, y, raio, raio);

                // Borda Branca de Destaque
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawOval(x, y, raio, raio);

                // Número de alertas dentro da bolinha
                if (quantidade > 0) {
                    g2.setFont(new Font("SansSerif", Font.BOLD, 10));
                    FontMetrics fm = g2.getFontMetrics();
                    String txt = quantidade > 9 ? "9+" : String.valueOf(quantidade);
                    int txtX = x + (raio - fm.stringWidth(txt)) / 2;
                    int txtY = y + ((raio - fm.getHeight()) / 2) + fm.getAscent();
                    g2.drawString(txt, txtX, txtY);
                }
            }

            g2.dispose();
        }
    }
}