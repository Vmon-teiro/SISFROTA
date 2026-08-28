package dao;

import model.Embarcacao;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmbarcacaoDAO {

    public boolean salvar(Embarcacao emb) {
        String sql = "INSERT INTO embarcacoes (nome, modelo, capacidade_passageiros, capacidade_carga_ton, ano_fabricacao, horimetro_horas, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexaoDAO.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, emb.getNome());
            stmt.setString(2, emb.getModelo());
            stmt.setInt(3, emb.getCapacidadePassageiros());
            stmt.setDouble(4, emb.getCapacidadeCargaTon());
            stmt.setInt(5, emb.getAnoFabricacao());
            stmt.setInt(6, emb.getHorimetroHoras());
            stmt.setString(7, emb.getStatus() != null ? emb.getStatus() : "ATIVA");

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean atualizar(Embarcacao emb) {
        String sql = "UPDATE embarcacoes SET nome = ?, modelo = ?, capacidade_passageiros = ?, " +
                     "capacidade_carga_ton = ?, ano_fabricacao = ?, horimetro_horas = ?, status = ? WHERE id = ?";
        try (Connection conn = ConexaoDAO.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, emb.getNome());
            stmt.setString(2, emb.getModelo());
            stmt.setInt(3, emb.getCapacidadePassageiros());
            stmt.setDouble(4, emb.getCapacidadeCargaTon());
            stmt.setInt(5, emb.getAnoFabricacao());
            stmt.setInt(6, emb.getHorimetroHoras());
            stmt.setString(7, emb.getStatus());
            stmt.setInt(8, emb.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Embarcacao> listarTodas() {
        List<Embarcacao> lista = new ArrayList<>();
        String sql = "SELECT * FROM embarcacoes ORDER BY nome ASC";

        try (Connection conn = ConexaoDAO.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Embarcacao emb = new Embarcacao();
                emb.setId(rs.getInt("id"));
                emb.setNome(rs.getString("nome"));
                emb.setModelo(rs.getString("modelo"));
                emb.setCapacidadePassageiros(rs.getInt("capacidade_passageiros"));
                emb.setCapacidadeCargaTon(rs.getDouble("capacidade_carga_ton"));
                emb.setAnoFabricacao(rs.getInt("ano_fabricacao"));
                emb.setHorimetroHoras(rs.getInt("horimetro_horas"));
                emb.setStatus(rs.getString("status"));
                lista.add(emb);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean excluir(int id) {
        String sql = "DELETE FROM embarcacoes WHERE id = ?";
        try (Connection conn = ConexaoDAO.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}