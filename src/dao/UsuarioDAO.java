package dao;

import model.Usuario;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    /**
     * Valida o e-mail e verifica a senha criptografada com BCrypt.
     */
    public Usuario autenticar(String email, String senhaDigitada) {
        // Busca o usuário apenas pelo email e status ativo
        String sql = "SELECT * FROM usuarios WHERE email = ? AND status = 'ATIVO'";

        try (Connection conn = ConexaoDAO.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String hashSenhaBanco = rs.getString("senha");

                    // Verifica se a senha digitada bate com o hash criptografado do banco
                    // Suporta fallback para senhas em texto puro durante transição
                    boolean senhaValida = false;
                    if (hashSenhaBanco.startsWith("$2a$") || hashSenhaBanco.startsWith("$2b$")) {
                        senhaValida = BCrypt.checkpw(senhaDigitada, hashSenhaBanco);
                    } else {
                        // Compara direto se o banco ainda tiver senhas antigas não criptografadas
                        senhaValida = senhaDigitada.equals(hashSenhaBanco);
                    }

                    if (senhaValida) {
                        Usuario usuario = new Usuario();
                        usuario.setId(rs.getInt("id"));
                        usuario.setNome(rs.getString("nome"));
                        usuario.setEmail(rs.getString("email"));
                        usuario.setSenha(hashSenhaBanco);
                        usuario.setPerfil(rs.getString("perfil"));
                        usuario.setStatus(rs.getString("status"));
                        return usuario;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao autenticar usuário: " + e.getMessage());
        }
        return null;
    }

    /**
     * Método utilitário para cadastrar ou atualizar a senha de um usuário com hash BCrypt.
     */
    public boolean cadastrarUsuario(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nome, email, senha, perfil, status) VALUES (?, ?, ?, ?, ?)";
        String senhaHash = BCrypt.hashpw(usuario.getSenha(), BCrypt.gensalt());

        try (Connection conn = ConexaoDAO.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, senhaHash);
            stmt.setString(4, usuario.getPerfil());
            stmt.setString(5, usuario.getStatus());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao cadastrar usuário: " + e.getMessage());
            return false;
        }
    }
}