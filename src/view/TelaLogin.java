package view;

import controller.UsuarioController;
import model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

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

        // CORREÇÃO: Ícone da Âncora com respiro no topo e alinhamento centralizado
        JLabel lblIcone = new JLabel("⚓", SwingConstants.CENTER);
        lblIcone.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 42));
        lblIcone.setForeground(ACCENT);
        lblIcone.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Margem superior de 10px para evitar o corte do topo da âncora
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
        txtSenha = criarCampoSenha();

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
        pnlCard.add(txtSenha);
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

    private JPasswordField criarCampoSenha() {
        JPasswordField campo = new JPasswordField();
        estilizarCampoFormulario(campo);
        return campo;
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