package view;

import controller.AlertaController;
import model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public class TelaPrincipal extends JFrame {

    // ---------------------------------------------------------------
    // PALETA DE CORES (Design Tokens)
    // ---------------------------------------------------------------
    private static final Color BG_APP             = new Color(241, 245, 249); // slate-100
    private static final Color HEADER_DARK        = new Color(15, 23, 42);   // slate-900
    private static final Color HEADER_DARK_2      = new Color(30, 41, 59);   // slate-800
    private static final Color CARD_BG            = Color.WHITE;
    private static final Color CARD_BORDER        = new Color(226, 232, 240); // slate-200
    private static final Color CARD_BORDER_HOVER  = new Color(99, 102, 241);  // indigo-500
    private static final Color CARD_BG_HOVER      = new Color(238, 242, 255); // indigo-50
    private static final Color TEXT_MUTED         = new Color(100, 116, 139); // slate-500
    private static final Color TEXT_ON_DARK       = new Color(226, 232, 240); // slate-200
    private static final Color ACCENT_SOFT        = new Color(224, 231, 255); // indigo-100
    private static final Color DANGER             = new Color(239, 68, 68);   // red-500

    private static final Font FONT_BOLD           = new Font("Segoe UI", Font.BOLD, 12);

    private final Usuario usuarioLogado;
    private List<String> alertasAtivos = new ArrayList<>();
    private BotaoNotificacao btnNotificacoes;

    public TelaPrincipal(Usuario usuario) {
        this.usuarioLogado = usuario;
        setTitle("Gestão Náutica - Painel Principal");
        setSize(960, 620);
        setMinimumSize(new Dimension(760, 480));
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
        getContentPane().setBackground(BG_APP);

        add(criarHeader(), BorderLayout.NORTH);
        add(criarPainelMenu(), BorderLayout.CENTER);
    }

    // ---------------------------------------------------------------
    // HEADER
    // ---------------------------------------------------------------
    private JPanel criarHeader() {
        JPanel panelHeader = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, HEADER_DARK, getWidth(), getHeight(), HEADER_DARK_2);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        panelHeader.setOpaque(false);
        panelHeader.setBorder(BorderFactory.createEmptyBorder(18, 28, 18, 24));

        String nome = usuarioLogado.getNome();
        String perfilRaw = usuarioLogado.getPerfil() != null ? usuarioLogado.getPerfil() : "";

        JLabel lblBemVindo = new JLabel("<html>"
                + "<span style='color:#94A3B8; font-size:9pt; letter-spacing:1px;'>BEM-VINDO(A)</span><br>"
                + "<span style='color:#F8FAFC; font-size:15pt; font-family:Segoe UI;'><b>" + nome + "</b></span>"
                + "&nbsp;&nbsp;<span style='color:#38BDF8; font-size:9.5pt; font-weight:bold;'>" + perfilRaw.toUpperCase() + "</span>"
                + "</html>");

        JPanel pnlAcoesTopo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        pnlAcoesTopo.setOpaque(false);

        btnNotificacoes = new BotaoNotificacao("Notificações");
        btnNotificacoes.setToolTipText("Clique para ver os alertas pendentes");
        btnNotificacoes.addActionListener(e -> exibirDialogoAlertas());

        JButton btnSair = criarBotaoSair();

        pnlAcoesTopo.add(btnNotificacoes);
        pnlAcoesTopo.add(btnSair);

        panelHeader.add(lblBemVindo, BorderLayout.WEST);
        panelHeader.add(pnlAcoesTopo, BorderLayout.EAST);
        return panelHeader;
    }

    private JButton criarBotaoSair() {
        JButton btn = new JButton("Sair") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean hover = getModel().isRollover();
                g2.setColor(hover ? DANGER : Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                setForeground(hover ? Color.WHITE : DANGER);
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BOLD);
        btn.setForeground(DANGER);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(9, 20, 9, 20));
        btn.addActionListener(e -> {
            new TelaLogin().setVisible(true);
            dispose();
        });
        return btn;
    }

    // ---------------------------------------------------------------
    // PAINEL CENTRAL / GRID DE CARDS
    // ---------------------------------------------------------------
    private JComponent criarPainelMenu() {
        String perfil = usuarioLogado.getPerfil() != null ? usuarioLogado.getPerfil().toUpperCase() : "";

        int colunas = perfil.equals("ADMINISTRADOR") ? 2 : 1;
        JPanel panelMenu = new JPanel(new GridLayout(0, colunas, 22, 22));
        panelMenu.setBackground(BG_APP);
        panelMenu.setBorder(BorderFactory.createEmptyBorder(32, 40, 32, 40));

        switch (perfil) {
            case "ADMINISTRADOR":
                panelMenu.add(criarCardMenu("⛴", "Gerenciar Embarcações", "Cadastro e controle de frotas",
                        e -> new TelaGerenciarEmbarcacoes().setVisible(true)));
                panelMenu.add(criarCardMenu("🔧", "Manutenções e Preventivas", "Ordens de serviço e chamados",
                        e -> new TelaManutencoes().setVisible(true)));
                panelMenu.add(criarCardMenu("👥", "Gerenciar Tripulação", "Tripulantes e habilitações",
                        e -> new TelaGerenciarTripulacao().setVisible(true)));
                panelMenu.add(criarCardMenu("📄", "Relatórios de Custos (PDF)", "Exportação de despesas operacionais",
                        e -> new TelaRelatorioCustos().setVisible(true)));
                break;

            case "OPERADOR":
                panelMenu.add(criarCardMenu("📋", "Painel Operacional Integrado", "Lançamento de incidentes e diário de bordo",
                        e -> new TelaDashboardOperador().setVisible(true)));
                break;

            case "TECNICO":
                panelMenu.add(criarCardMenu("🛠", "Serviços Gerais", "Execução de ordens de serviço atribuídas",
                        e -> new TelaDashboardTecnico().setVisible(true)));
                break;

            default:
                JOptionPane.showMessageDialog(this, "Perfil de usuário não reconhecido.", "Erro de Permissão", JOptionPane.ERROR_MESSAGE);
                break;
        }

        // SOLUÇÃO 3: Conteiner para ancorar o grid no topo e impedir esticamento ao maximizar
        JPanel pnlWrapper = new JPanel(new BorderLayout());
        pnlWrapper.setBackground(BG_APP);
        pnlWrapper.add(panelMenu, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(pnlWrapper);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(BG_APP);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private JButton criarCardMenu(String icone, String titulo, String subtitulo, java.awt.event.ActionListener acao) {
        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                boolean hover = getModel().isRollover();
                int w = getWidth(), h = getHeight();
                RoundRectangle2D shape = new RoundRectangle2D.Float(0, 0, w - 4, h - 4, 18, 18);

                g2.setColor(new Color(15, 23, 42, hover ? 28 : 14));
                g2.fill(new RoundRectangle2D.Float(3, hover ? 6 : 4, w - 4, h - 4, 18, 18));

                g2.setColor(hover ? CARD_BG_HOVER : CARD_BG);
                g2.fill(shape);

                g2.setColor(hover ? CARD_BORDER_HOVER : CARD_BORDER);
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(shape);

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
        btn.setPreferredSize(new Dimension(320, 105)); // Altura fixa e proporcional

        JPanel conteudo = new JPanel(new BorderLayout(16, 0));
        conteudo.setOpaque(false);
        conteudo.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        // SOLUÇÃO 1: Mantém o circulo perfeitamente simétrico e centralizado
        JLabel lblIcone = new JLabel(icone, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int size = Math.min(getWidth(), getHeight());
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;

                g2.setColor(ACCENT_SOFT);
                g2.fillOval(x, y, size, size);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        lblIcone.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        lblIcone.setPreferredSize(new Dimension(48, 48));
        lblIcone.setOpaque(false);

        // Wrapper FlowLayout para o icone nao ser esticado pelo BorderLayout
        JPanel pnlIconeWrapper = new JPanel(new GridBagLayout());
        pnlIconeWrapper.setOpaque(false);
        pnlIconeWrapper.add(lblIcone);

        JLabel lblTexto = new JLabel("<html><body>"
                + "<span style='color:#0F172A; font-size:12pt; font-family:Segoe UI;'><b>" + titulo + "</b></span><br>"
                + "<span style='color:#64748B; font-size:9pt;'>" + subtitulo + "</span>"
                + "</body></html>");

        JLabel lblSeta = new JLabel("›");
        lblSeta.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblSeta.setForeground(TEXT_MUTED);

        conteudo.add(pnlIconeWrapper, BorderLayout.WEST);
        conteudo.add(lblTexto, BorderLayout.CENTER);
        conteudo.add(lblSeta, BorderLayout.EAST);

        btn.add(conteudo, BorderLayout.CENTER);
        btn.addActionListener(acao);

        return btn;
    }

    // ---------------------------------------------------------------
    // COMPONENTE CUSTOMIZADO: BOTÃO DE NOTIFICAÇÃO COM BADGE E VETOR 2D
    // ---------------------------------------------------------------
    private static class BotaoNotificacao extends JButton {
        private boolean temNotificacao = false;
        private int quantidade = 0;

        public BotaoNotificacao(String texto) {
            super(texto);
            setFont(FONT_BOLD);
            setForeground(TEXT_ON_DARK);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            // Espaçamento à esquerda (38px) reservado para o desenho do sininho em Java 2D
            setBorder(BorderFactory.createEmptyBorder(9, 38, 9, 22));
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

            boolean hover = getModel().isRollover();
            g2.setColor(hover ? new Color(71, 85, 105) : new Color(51, 65, 85));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

            // SOLUÇÃO 2: Desenho Vetorial do Sino em Java 2D
            int bx = 15;
            int by = (getHeight() - 16) / 2;
            g2.setColor(TEXT_ON_DARK);

            // Alça superior do sino
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawArc(bx + 4, by, 4, 4, 0, 180);

            // Corpo do sino
            Path2D bell = new Path2D.Float();
            bell.moveTo(bx + 1, by + 11);
            bell.curveTo(bx + 1, by + 6, bx + 3, by + 3, bx + 6, by + 3);
            bell.curveTo(bx + 9, by + 3, bx + 11, by + 6, bx + 11, by + 11);
            bell.lineTo(bx + 13, by + 12);
            bell.lineTo(bx - 1, by + 12);
            bell.closePath();
            g2.fill(bell);

            // Badalo (bolinha de baixo)
            g2.fillOval(bx + 4, by + 13, 4, 3);

            super.paintComponent(g2);

            // Desenho do Badge de Notificação (Bolinha Vermelha)
            if (temNotificacao) {
                int raio = 16;
                int x = getWidth() - raio - 4;
                int y = 4;

                g2.setColor(DANGER);
                g2.fillOval(x, y, raio, raio);

                g2.setColor(new Color(30, 41, 59));
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(x, y, raio, raio);

                if (quantidade > 0) {
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 9));
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