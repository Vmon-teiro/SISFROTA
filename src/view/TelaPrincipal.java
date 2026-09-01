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
    private static final Color HEADER_DARK        = new Color(15, 23, 42);    // slate-900
    private static final Color HEADER_DARK_2      = new Color(30, 41, 59);    // slate-800
    private static final Color CARD_BG            = Color.WHITE;
    private static final Color CARD_BORDER        = new Color(226, 232, 240); // slate-200
    private static final Color CARD_BORDER_HOVER  = new Color(99, 102, 241);  // indigo-500
    private static final Color CARD_BG_HOVER      = new Color(248, 250, 252); // slate-50
    private static final Color TEXT_TITLE         = new Color(15, 23, 42);    // slate-900
    private static final Color TEXT_MUTED         = new Color(100, 116, 139); // slate-500
    private static final Color TEXT_ON_DARK       = new Color(226, 232, 240); // slate-200
    private static final Color ACCENT             = new Color(99, 102, 241);  // indigo-500
    private static final Color ACCENT_CYAN        = new Color(56, 189, 248);  // sky-400
    private static final Color DANGER             = new Color(239, 68, 68);   // red-500

    private static final Font FONT_BOLD           = new Font("Segoe UI", Font.BOLD, 12);

    private final Usuario usuarioLogado;
    private List<String> alertasAtivos = new ArrayList<>();
    private BotaoNotificacao btnNotificacoes;

    public enum TipoIcone { EMBARCACOES, MANUTENCOES, TRIPULACAO, RELATORIOS, OPERACIONAL, SERVICOS }

    public TelaPrincipal(Usuario usuario) {
        this.usuarioLogado = usuario;
        setTitle("Gestão Náutica - Painel Principal");
        setSize(1040, 720);
        setMinimumSize(new Dimension(850, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponentes();
        verificarEExibirAlertas();
    }

    private void verificarEExibirAlertas() {
    SwingUtilities.invokeLater(() -> {
        AlertaController alertaController = new AlertaController();
        // Passando o usuário logado para filtrar os alertas por perfil
        alertasAtivos = alertaController.verificarAlertasVencimento(usuarioLogado);

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

        JPanel pnlTopo = new JPanel();
        pnlTopo.setLayout(new BoxLayout(pnlTopo, BoxLayout.Y_AXIS));
        pnlTopo.add(criarHeaderRefinado());
        pnlTopo.add(criarPainelGraficosCustomizados());

        add(pnlTopo, BorderLayout.NORTH);
        add(criarPainelMenu(), BorderLayout.CENTER);
    }

    // ---------------------------------------------------------------
    // CABEÇALHO REFINADO
    // ---------------------------------------------------------------
    private JPanel criarHeaderRefinado() {
        JPanel panelHeader = new JPanel(new BorderLayout(20, 0)) {
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
        panelHeader.setBorder(BorderFactory.createEmptyBorder(18, 28, 18, 28));

        // LADO ESQUERDO: Marca Náutica + Título + Usuário
        JPanel pnlInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        pnlInfo.setOpaque(false);

        // Ícone do Leme/Âncora Vetorial
        JPanel pnlLogo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(56, 189, 248, 40));
                g2.fillOval(0, 0, 42, 42);
                g2.setColor(ACCENT_CYAN);
                g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                int cx = 21, cy = 21;
                g2.drawOval(cx - 10, cy - 10, 20, 20);
                g2.drawOval(cx - 4, cy - 4, 8, 8);
                for (int i = 0; i < 8; i++) {
                    g2.drawLine(cx, cy - 10, cx, cy - 16);
                    g2.rotate(Math.toRadians(45), cx, cy);
                }
                g2.dispose();
            }
        };
        pnlLogo.setPreferredSize(new Dimension(42, 42));
        pnlLogo.setOpaque(false);

        String nome = usuarioLogado.getNome();
        String perfilRaw = usuarioLogado.getPerfil() != null ? usuarioLogado.getPerfil().toUpperCase() : "USUÁRIO";

        JPanel pnlTitulos = new JPanel();
        pnlTitulos.setLayout(new BoxLayout(pnlTitulos, BoxLayout.Y_AXIS));
        pnlTitulos.setOpaque(false);

        JLabel lblSistema = new JLabel("SISTEMA DE GESTÃO NÁUTICA");
        lblSistema.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblSistema.setForeground(ACCENT_CYAN);

        JLabel lblUsuario = new JLabel("<html><span style='color:#F8FAFC; font-size:14pt; font-family:Segoe UI;'><b>" + nome + "</b></span></html>");

        pnlTitulos.add(lblSistema);
        pnlTitulos.add(Box.createVerticalStrut(2));
        pnlTitulos.add(lblUsuario);

        // Badge de Perfil Customizado
        JLabel lblBadge = new JLabel(perfilRaw) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(99, 102, 241, 50));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(ACCENT);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lblBadge.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lblBadge.setForeground(new Color(224, 231, 255));
        lblBadge.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));

        pnlInfo.add(pnlLogo);
        pnlInfo.add(pnlTitulos);
        pnlInfo.add(Box.createHorizontalStrut(8));
        pnlInfo.add(lblBadge);

        // LADO DIREITO: Notificações + Sair
        JPanel pnlAcoesTopo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        pnlAcoesTopo.setOpaque(false);

        btnNotificacoes = new BotaoNotificacao("Notificações");
        btnNotificacoes.setToolTipText("Clique para ver os alertas pendentes");
        btnNotificacoes.addActionListener(e -> exibirDialogoAlertas());

        JButton btnSair = criarBotaoSair();

        pnlAcoesTopo.add(btnNotificacoes);
        pnlAcoesTopo.add(btnSair);

        panelHeader.add(pnlInfo, BorderLayout.WEST);
        panelHeader.add(pnlAcoesTopo, BorderLayout.EAST);
        return panelHeader;
    }

    // ---------------------------------------------------------------
    // PAINEL DE GRÁFICOS DINÂMICOS / PERSONALIZADOS POR PERFIL
    // ---------------------------------------------------------------
    private JPanel criarPainelGraficosCustomizados() {
        JPanel pnlContainer = new JPanel(new GridLayout(1, 2, 20, 0));
        pnlContainer.setBackground(BG_APP);
        pnlContainer.setBorder(BorderFactory.createEmptyBorder(20, 32, 10, 32));

        String perfil = usuarioLogado.getPerfil() != null ? usuarioLogado.getPerfil().toUpperCase() : "";

        switch (perfil) {
            case "ADMINISTRADOR":
                pnlContainer.add(new GraficoRoscaPanel(
                        "Status da Frota Náutica",
                        new String[]{"Operacional", "Manutenção", "Inativo"},
                        new double[]{65, 25, 10},
                        new Color[]{new Color(16, 185, 129), new Color(245, 158, 11), new Color(239, 68, 68)},
                        "10 Embs"
                ));
                pnlContainer.add(new GraficoBarrasPanel(
                        "Despesas Operacionais ($)",
                        new String[]{"Jan", "Fev", "Mar", "Abr", "Mai"},
                        new double[]{12, 19, 15, 22, 18},
                        ACCENT
                ));
                break;

            case "OPERADOR":
                pnlContainer.add(new GraficoRoscaPanel(
                        "Status dos Incidentes",
                        new String[]{"Resolvidos", "Em Aberto"},
                        new double[]{80, 20},
                        new Color[]{new Color(16, 185, 129), new Color(239, 68, 68)},
                        "80% OK"
                ));
                pnlContainer.add(new GraficoBarrasPanel(
                        "Horas de Navegação (Semana)",
                        new String[]{"Seg", "Ter", "Qua", "Qui", "Sex"},
                        new double[]{8, 10, 6, 12, 9},
                        new Color(14, 165, 233)
                ));
                break;

            case "TECNICO":
                pnlContainer.add(new GraficoRoscaPanel(
                        "Ordens de Serviço (OS)",
                        new String[]{"Concluídas", "Em Progresso", "Urgentes"},
                        new double[]{55, 30, 15},
                        new Color[]{new Color(16, 185, 129), new Color(99, 102, 241), new Color(239, 68, 68)},
                        "12 OS"
                ));
                pnlContainer.add(new GraficoBarrasPanel(
                        "Tempo Médio de Reparo (Horas)",
                        new String[]{"Sem 1", "Sem 2", "Sem 3", "Sem 4"},
                        new double[]{4.5, 3.2, 5.0, 2.8},
                        new Color(168, 85, 247)
                ));
                break;

            default:
                pnlContainer.add(new GraficoRoscaPanel("Atividades", new String[]{"Ativas"}, new double[]{100}, new Color[]{ACCENT}, "100%"));
                pnlContainer.add(new GraficoBarrasPanel("Registro", new String[]{"Dias"}, new double[]{5}, ACCENT));
                break;
        }

        return pnlContainer;
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
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
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
        JPanel panelMenu = new JPanel(new GridLayout(0, colunas, 20, 20));
        panelMenu.setBackground(BG_APP);
        panelMenu.setBorder(BorderFactory.createEmptyBorder(15, 32, 28, 32));

        switch (perfil) {
            case "ADMINISTRADOR":
                panelMenu.add(criarCardMenu(TipoIcone.EMBARCACOES, "Gerenciar Embarcações", "Cadastro, frotas e especificações técnicas",
                        e -> new TelaGerenciarEmbarcacoes().setVisible(true)));
                panelMenu.add(criarCardMenu(TipoIcone.MANUTENCOES, "Manutenções e Preventivas", "Ordens de serviço, reparos e chamados",
                        e -> new TelaManutencoes().setVisible(true)));
                panelMenu.add(criarCardMenu(TipoIcone.TRIPULACAO, "Gerenciar Tripulação", "Tripulantes, habilitações e certidões",
                        e -> new TelaGerenciarTripulacao().setVisible(true)));
                panelMenu.add(criarCardMenu(TipoIcone.RELATORIOS, "Relatórios de Custos (PDF)", "Exportação de despesas operacionais",
                        e -> new TelaRelatorioCustos().setVisible(true)));
                break;

            case "OPERADOR":
                panelMenu.add(criarCardMenu(TipoIcone.OPERACIONAL, "Painel Operacional Integrado", "Lançamento de incidentes e diário de bordo",
                        e -> new TelaDashboardOperador().setVisible(true)));
                break;

            case "TECNICO":
                panelMenu.add(criarCardMenu(TipoIcone.SERVICOS, "Serviços Gerais", "Execução de ordens de serviço atribuídas",
                        e -> new TelaDashboardTecnico().setVisible(true)));
                break;

            default:
                JOptionPane.showMessageDialog(this, "Perfil de usuário não reconhecido.", "Erro de Permissão", JOptionPane.ERROR_MESSAGE);
                break;
        }

        JPanel pnlWrapper = new JPanel(new BorderLayout());
        pnlWrapper.setBackground(BG_APP);
        pnlWrapper.add(panelMenu, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(pnlWrapper);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(BG_APP);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private JButton criarCardMenu(TipoIcone icone, String titulo, String subtitulo, java.awt.event.ActionListener acao) {
        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                boolean hover = getModel().isRollover();
                int w = getWidth(), h = getHeight();
                RoundRectangle2D shape = new RoundRectangle2D.Float(0, 0, w - 2, h - 2, 16, 16);

                g2.setColor(new Color(15, 23, 42, hover ? 25 : 10));
                g2.fill(new RoundRectangle2D.Float(2, hover ? 5 : 3, w - 2, h - 2, 16, 16));

                g2.setColor(hover ? CARD_BG_HOVER : CARD_BG);
                g2.fill(shape);

                g2.setColor(hover ? CARD_BORDER_HOVER : CARD_BORDER);
                g2.setStroke(new BasicStroke(hover ? 1.8f : 1.0f));
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
        btn.setPreferredSize(new Dimension(340, 95));

        JPanel conteudo = new JPanel(new BorderLayout(16, 0));
        conteudo.setOpaque(false);
        conteudo.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        PainelIconeVetorial pnlIcone = new PainelIconeVetorial(icone);

        JLabel lblTexto = new JLabel("<html><body>"
                + "<span style='color:#0F172A; font-size:11.5pt; font-family:Segoe UI;'><b>" + titulo + "</b></span><br>"
                + "<span style='color:#64748B; font-size:8.5pt;'>" + subtitulo + "</span>"
                + "</body></html>");

        JLabel lblSeta = new JLabel("›");
        lblSeta.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblSeta.setForeground(new Color(148, 163, 184));

        conteudo.add(pnlIcone, BorderLayout.WEST);
        conteudo.add(lblTexto, BorderLayout.CENTER);
        conteudo.add(lblSeta, BorderLayout.EAST);

        btn.add(conteudo, BorderLayout.CENTER);
        btn.addActionListener(acao);

        return btn;
    }

    // ---------------------------------------------------------------
    // DESENHO VETORIAL DE ÍCONES
    // ---------------------------------------------------------------
    private static class PainelIconeVetorial extends JPanel {
        private final TipoIcone tipo;

        public PainelIconeVetorial(TipoIcone tipo) {
            this.tipo = tipo;
            setPreferredSize(new Dimension(48, 48));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color bg, fg;
            switch (tipo) {
                case EMBARCACOES: bg = new Color(224, 231, 255); fg = new Color(79, 70, 229); break;
                case MANUTENCOES: bg = new Color(254, 243, 199); fg = new Color(217, 119, 6); break;
                case TRIPULACAO:  bg = new Color(204, 251, 241); fg = new Color(13, 148, 136); break;
                case RELATORIOS:  bg = new Color(254, 226, 226); fg = new Color(225, 29, 72); break;
                case OPERACIONAL: bg = new Color(209, 250, 229); fg = new Color(5, 150, 105); break;
                case SERVICOS:
                default:          bg = new Color(243, 232, 255); fg = new Color(147, 51, 234); break;
            }

            g2.setColor(bg);
            g2.fillOval(0, 0, 48, 48);

            g2.setColor(fg);
            g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            int cx = 24, cy = 24;

            switch (tipo) {
                case EMBARCACOES:
                    Path2D boat = new Path2D.Float();
                    boat.moveTo(cx - 10, cy + 3); boat.lineTo(cx + 10, cy + 3);
                    boat.lineTo(cx + 6, cy + 9);  boat.lineTo(cx - 6, cy + 9);
                    boat.closePath();
                    g2.draw(boat);
                    g2.drawLine(cx, cy + 3, cx, cy - 8);
                    Path2D sail = new Path2D.Float();
                    sail.moveTo(cx, cy - 8); sail.lineTo(cx + 6, cy - 2); sail.lineTo(cx, cy - 2);
                    sail.closePath();
                    g2.fill(sail);
                    break;
                case MANUTENCOES:
                    g2.rotate(Math.toRadians(45), cx, cy);
                    g2.drawOval(cx - 4, cy - 10, 8, 8);
                    g2.fillRect(cx - 2, cy - 2, 4, 11);
                    break;
                case TRIPULACAO:
                    g2.drawOval(cx - 4, cy - 9, 8, 8);
                    g2.drawArc(cx - 8, cy, 16, 12, 0, 180);
                    break;
                case RELATORIOS:
                    g2.drawRoundRect(cx - 7, cy - 10, 14, 20, 3, 3);
                    g2.drawLine(cx - 4, cy - 4, cx + 4, cy - 4);
                    g2.drawLine(cx - 4, cy, cx + 4, cy);
                    g2.drawLine(cx - 4, cy + 4, cx + 1, cy + 4);
                    break;
                case OPERACIONAL:
                    g2.drawRoundRect(cx - 8, cy - 7, 16, 18, 3, 3);
                    g2.drawRoundRect(cx - 3, cy - 10, 6, 4, 2, 2);
                    g2.drawLine(cx - 4, cy, cx + 4, cy);
                    break;
                case SERVICOS:
                    g2.drawOval(cx - 5, cy - 5, 10, 10);
                    for (int i = 0; i < 8; i++) {
                        g2.drawLine(cx, cy - 5, cx, cy - 9);
                        g2.rotate(Math.toRadians(45), cx, cy);
                    }
                    break;
            }

            g2.dispose();
        }
    }

    // ---------------------------------------------------------------
    // GRÁFICO 1: GRÁFICO DE ROSCA 2D NATIVO
    // ---------------------------------------------------------------
    private static class GraficoRoscaPanel extends JPanel {
        private final String titulo;
        private final String[] categorias;
        private final double[] valores;
        private final Color[] cores;
        private final String centroTexto;

        public GraficoRoscaPanel(String titulo, String[] categorias, double[] valores, Color[] cores, String centroTexto) {
            this.titulo = titulo;
            this.categorias = categorias;
            this.valores = valores;
            this.cores = cores;
            this.centroTexto = centroTexto;
            setOpaque(false);
            setPreferredSize(new Dimension(300, 130));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();

            // Card Fundo
            g2.setColor(CARD_BG);
            g2.fillRoundRect(0, 0, w - 1, h - 1, 14, 14);
            g2.setColor(CARD_BORDER);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, w - 1, h - 1, 14, 14);

            // Título
            g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
            g2.setColor(TEXT_MUTED);
            g2.drawString(titulo.toUpperCase(), 14, 22);

            // Cálculo dos Arcos
            double total = 0;
            for (double v : valores) total += v;

            int diameter = 80;
            int x = 14;
            int y = 34;

            int startAngle = 90;
            for (int i = 0; i < valores.length; i++) {
                int angle = (int) Math.round((valores[i] / total) * 360);
                g2.setColor(cores[i]);
                g2.fillArc(x, y, diameter, diameter, startAngle, angle);
                startAngle += angle;
            }

            // Furo Central da Rosca
            int holeSize = 50;
            int hx = x + (diameter - holeSize) / 2;
            int hy = y + (diameter - holeSize) / 2;
            g2.setColor(CARD_BG);
            g2.fillOval(hx, hy, holeSize, holeSize);

            // Texto Central
            g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
            g2.setColor(TEXT_TITLE);
            FontMetrics fm = g2.getFontMetrics();
            int tx = hx + (holeSize - fm.stringWidth(centroTexto)) / 2;
            int ty = hy + ((holeSize - fm.getHeight()) / 2) + fm.getAscent();
            g2.drawString(centroTexto, tx, ty);

            // Legendas à Direita
            int lx = x + diameter + 18;
            int ly = y + 14;
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));

            for (int i = 0; i < categorias.length; i++) {
                g2.setColor(cores[i]);
                g2.fillRoundRect(lx, ly, 8, 8, 2, 2);
                g2.setColor(TEXT_TITLE);
                g2.drawString(categorias[i] + " (" + (int) valores[i] + "%)", lx + 12, ly + 8);
                ly += 18;
            }

            g2.dispose();
        }
    }

    // ---------------------------------------------------------------
    // GRÁFICO 2: GRÁFICO DE BARRAS 2D NATIVO
    // ---------------------------------------------------------------
    private static class GraficoBarrasPanel extends JPanel {
        private final String titulo;
        private final String[] labels;
        private final double[] valores;
        private final Color corBarra;

        public GraficoBarrasPanel(String titulo, String[] labels, double[] valores, Color corBarra) {
            this.titulo = titulo;
            this.labels = labels;
            this.valores = valores;
            this.corBarra = corBarra;
            setOpaque(false);
            setPreferredSize(new Dimension(300, 130));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();

            // Card Fundo
            g2.setColor(CARD_BG);
            g2.fillRoundRect(0, 0, w - 1, h - 1, 14, 14);
            g2.setColor(CARD_BORDER);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, w - 1, h - 1, 14, 14);

            // Título
            g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
            g2.setColor(TEXT_MUTED);
            g2.drawString(titulo.toUpperCase(), 14, 22);

            // Maior valor para escala
            double maxVal = 0;
            for (double v : valores) if (v > maxVal) maxVal = v;

            int barWidth = 18;
            int chartHeight = 55;
            int startX = 24;
            int startY = 100;
            int gap = (w - 40 - (labels.length * barWidth)) / (labels.length);

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 9));

            for (int i = 0; i < valores.length; i++) {
                int barH = (int) ((valores[i] / maxVal) * chartHeight);
                int bx = startX + i * (barWidth + gap);
                int by = startY - barH;

                // Barra Arredondada
                g2.setColor(corBarra);
                g2.fillRoundRect(bx, by, barWidth, barH, 4, 4);

                // Label X
                g2.setColor(TEXT_MUTED);
                g2.drawString(labels[i], bx + (barWidth / 2) - 8, startY + 14);
            }

            g2.dispose();
        }
    }

    // ---------------------------------------------------------------
    // BOTÃO DE NOTIFICAÇÃO
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
            setBorder(BorderFactory.createEmptyBorder(8, 36, 8, 20));
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

            int bx = 14;
            int by = (getHeight() - 16) / 2;
            g2.setColor(TEXT_ON_DARK);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawArc(bx + 4, by, 4, 4, 0, 180);

            Path2D bell = new Path2D.Float();
            bell.moveTo(bx + 1, by + 11);
            bell.curveTo(bx + 1, by + 6, bx + 3, by + 3, bx + 6, by + 3);
            bell.curveTo(bx + 9, by + 3, bx + 11, by + 6, bx + 11, by + 11);
            bell.lineTo(bx + 13, by + 12);
            bell.lineTo(bx - 1, by + 12);
            bell.closePath();
            g2.fill(bell);
            g2.fillOval(bx + 4, by + 13, 4, 3);

            super.paintComponent(g2);

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