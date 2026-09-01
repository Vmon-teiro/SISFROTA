package view;

import controller.UsuarioController;
import model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Path2D;

public class TelaLogin extends JFrame {

    // Paleta de Cores (Design Tokens)
    private static final Color BG_APP        = new Color(241, 245, 249); // slate-100
    private static final Color CARD_BG       = Color.WHITE;
    private static final Color TEXT_TITLE    = new Color(15, 23, 42);    // slate-900
    private static final Color TEXT_MUTED    = new Color(100, 116, 139); // slate-500
    private static final Color ACCENT        = new Color(99, 102, 241);  // indigo-500
    private static final Color ACCENT_HOVER  = new Color(79, 70, 229);  // indigo-600
    private static final Color INPUT_BORDER = new Color(203, 213, 225); // slate-300
    private static final Font FONT_BASE      = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_BOLD      = new Font("Segoe UI", Font.BOLD, 13);

    private JTextField txtEmail;
    private JPasswordField txtSenha;
    private final UsuarioController controller;

    public TelaLogin() {
        controller = new UsuarioController();
        setTitle("Gestão Náutica - Autenticação");
        setSize(420, 530);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        initComponentes();
    }

    private void initComponentes() {
        JPanel pnlFundo = new JPanel(new GridBagLayout());
        pnlFundo.setBackground(BG_APP);

        // Card Principal
        JPanel pnlCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Sombra suave
                g2.setColor(new Color(15, 23, 42, 15));
                g2.fillRoundRect(2, 4, getWidth() - 4, getHeight() - 4, 20, 20);

                // Fundo Branco
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 4, 20, 20);

                g2.dispose();
            }
        };
        pnlCard.setOpaque(false);
        pnlCard.setPreferredSize(new Dimension(340, 420));
        pnlCard.setLayout(new BoxLayout(pnlCard, BoxLayout.Y_AXIS));
        pnlCard.setBorder(BorderFactory.createEmptyBorder(30, 35, 35, 35));

        // Ícone da Âncora
        JLabel lblIcone = new JLabel("⚓", SwingConstants.CENTER);
        lblIcone.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 42));
        lblIcone.setForeground(ACCENT);
        lblIcone.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblIcone.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));

        JLabel lblTitulo = new JLabel("Gestão Náutica");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(TEXT_TITLE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitulo = new JLabel("Acesso restrito ao sistema");
        lblSubtitulo.setFont(FONT_BASE);
        lblSubtitulo.setForeground(TEXT_MUTED);
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Campos de entrada
        txtEmail = criarCampoTexto();
        JPanel pnlSenhaContainer = criarCampoSenhaComOlho();

        txtSenha.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    efetuarLogin(null);
                }
            }
        });

        // Botões de ação
        JButton btnEntrar = criarBotaoPrimario("Entrar no Sistema");
        btnEntrar.addActionListener(this::efetuarLogin);

        JButton btnSair = criarBotaoSecundario("Encerrar");
        btnSair.addActionListener(e -> System.exit(0));

        // Montagem do Card
        pnlCard.add(lblIcone);
        pnlCard.add(Box.createVerticalStrut(5));
        pnlCard.add(lblTitulo);
        pnlCard.add(Box.createVerticalStrut(4));
        pnlCard.add(lblSubtitulo);
        pnlCard.add(Box.createVerticalStrut(30));

        pnlCard.add(criarLabelCampo("E-mail corporativo"));
        pnlCard.add(Box.createVerticalStrut(5));
        pnlCard.add(txtEmail);
        pnlCard.add(Box.createVerticalStrut(15));

        pnlCard.add(criarLabelCampo("Senha de acesso"));
        pnlCard.add(Box.createVerticalStrut(5));
        pnlCard.add(pnlSenhaContainer);
        pnlCard.add(Box.createVerticalStrut(28));

        pnlCard.add(btnEntrar);
        pnlCard.add(Box.createVerticalStrut(10));
        pnlCard.add(btnSair);

        pnlFundo.add(pnlCard);
        add(pnlFundo);
    }

    private JLabel criarLabelCampo(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setForeground(TEXT_MUTED);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setMaximumSize(new Dimension(Short.MAX_VALUE, label.getPreferredSize().height));
        return label;
    }

    private JTextField criarCampoTexto() {
        JTextField campo = new JTextField();
        estilizarCampoFormulario(campo);
        return campo;
    }

    private JPanel criarCampoSenhaComOlho() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(new Color(248, 250, 252));
        painel.setBorder(BorderFactory.createLineBorder(INPUT_BORDER, 1));
        painel.setMaximumSize(new Dimension(Short.MAX_VALUE, 38));
        painel.setPreferredSize(new Dimension(200, 38));

        txtSenha = new JPasswordField();
        txtSenha.setFont(FONT_BASE);
        txtSenha.setForeground(TEXT_TITLE);
        txtSenha.setBackground(new Color(248, 250, 252));
        txtSenha.setCaretColor(ACCENT);
        txtSenha.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 5));

        char echoCharPadrao = txtSenha.getEchoChar();

        JToggleButton btnOlho = new JToggleButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                int cx = w / 2;
                int cy = h / 2;

                g2.setColor(isSelected() ? ACCENT : TEXT_MUTED);
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                // Desenho do contorno do olho (amêndoa)
                Path2D eye = new Path2D.Float();
                eye.moveTo(cx - 9, cy);
                eye.quadTo(cx, cy - 6, cx + 9, cy);
                eye.quadTo(cx, cy + 6, cx - 9, cy);
                g2.draw(eye);

                // Pupila central
                g2.fillOval(cx - 2, cy - 2, 5, 5);

                // Risco diagonal quando a senha estiver ocultada (Estilo Nubank)
                if (!isSelected()) {
                    g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawLine(cx - 9, cy + 7, cx + 9, cy - 7);
                }

                g2.dispose();
            }
        };

        btnOlho.setPreferredSize(new Dimension(38, 38));
        btnOlho.setFocusPainted(false);
        btnOlho.setContentAreaFilled(false);
        btnOlho.setBorderPainted(false);
        btnOlho.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnOlho.setToolTipText("Mostrar/Ocultar Senha");

        btnOlho.addActionListener(e -> {
            if (btnOlho.isSelected()) {
                txtSenha.setEchoChar((char) 0); // Exibe a senha
            } else {
                txtSenha.setEchoChar(echoCharPadrao); // Oculta a senha
            }
            btnOlho.repaint();
        });

        painel.add(txtSenha, BorderLayout.CENTER);
        painel.add(btnOlho, BorderLayout.EAST);

        return painel;
    }

    private void estilizarCampoFormulario(JTextField campo) {
        campo.setFont(FONT_BASE);
        campo.setForeground(TEXT_TITLE);
        campo.setBackground(new Color(248, 250, 252));
        campo.setCaretColor(ACCENT);
        campo.setMaximumSize(new Dimension(Short.MAX_VALUE, 38));
        campo.setPreferredSize(new Dimension(200, 38));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(INPUT_BORDER, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }

    private JButton criarBotaoPrimario(String texto) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? ACCENT_HOVER : ACCENT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BOLD);
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));
        btn.setPreferredSize(new Dimension(200, 40));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        return btn;
    }

    private JButton criarBotaoSecundario(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(FONT_BASE);
        btn.setForeground(TEXT_MUTED);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Short.MAX_VALUE, 35));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        return btn;
    }

    private void efetuarLogin(ActionEvent e) {
        String email = txtEmail.getText();
        String senha = new String(txtSenha.getPassword());

        if (email.trim().isEmpty() || senha.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, preencha todos os campos.",
                    "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Usuario usuario = controller.autenticar(email, senha);

        if (usuario != null) {
            new TelaPrincipal(usuario).setVisible(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "E-mail ou senha incorretos!",
                    "Erro de Autenticação", JOptionPane.ERROR_MESSAGE);
            txtSenha.setText("");
            txtSenha.requestFocus();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaLogin().setVisible(true));
    }
}