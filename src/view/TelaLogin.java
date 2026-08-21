package view;

import controller.UsuarioController;
import model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class TelaLogin extends JFrame {

    private JTextField txtEmail;
    private JPasswordField txtSenha;
    private final UsuarioController controller;

    public TelaLogin() {
        controller = new UsuarioController();
        setTitle("Gestão Náutica - Login");
        setSize(380, 220);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        initComponentes();
    }

    private void initComponentes() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("E-mail:"));
        txtEmail = new JTextField("");
        panel.add(txtEmail);

        panel.add(new JLabel("Senha:"));
        txtSenha = new JPasswordField("");
        panel.add(txtSenha);

        JButton btnEntrar = new JButton("Entrar");
        btnEntrar.addActionListener(this::efetuarLogin);
        
        JButton btnSair = new JButton("Sair");
        btnSair.addActionListener(e -> System.exit(0));

        panel.add(btnEntrar);
        panel.add(btnSair);

        add(panel);
    }

    private void efetuarLogin(ActionEvent e) {
        String email = txtEmail.getText();
        String senha = new String(txtSenha.getPassword());

        Usuario usuario = controller.autenticar(email, senha);

        if (usuario != null) {
            JOptionPane.showMessageDialog(this,
                    "Login efetuado com sucesso!\nUsuário: " + usuario.getNome() + "\nPerfil: " + usuario.getPerfil(),
                    "Acesso Permitido", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "E-mail ou senha incorretos!",
                    "Erro de Autenticação", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaLogin().setVisible(true));
    }
}