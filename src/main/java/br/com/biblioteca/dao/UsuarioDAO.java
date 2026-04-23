package br.com.biblioteca.dao;

import br.com.biblioteca.factory.UsuarioFactory;
import br.com.biblioteca.model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO implements IDao<Usuario> {

    @Override
    public boolean salvar(Usuario usuario, Connection conn) throws SQLException {
        String sql = "INSERT INTO usuarios (nome, tipo, saldo_devedor) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.obterTipo());
            stmt.setDouble(3, usuario.getSaldo().getSaldoDevedor());

            int linhasAfetadas = stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                usuario.setId(rs.getLong(1));
            }
            return linhasAfetadas > 0;
        }
    }

    @Override
    public Usuario buscarPorId(long id, Connection conn) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapearUsuario(rs);
            }
            return null;
        }
    }

    @Override
    public List<Usuario> listarTodos(Connection conn) throws SQLException {
        String sql = "SELECT * FROM usuarios";
        List<Usuario> lista = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(mapearUsuario(rs));
            }
            return lista;
        }
    }

    @Override
    public boolean atualizar(Usuario usuario, Connection conn) throws SQLException {
        String sql = "UPDATE usuarios SET nome = ?, tipo = ?, saldo_devedor = ? WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.obterTipo());
            stmt.setDouble(3, usuario.getSaldo().getSaldoDevedor());
            stmt.setLong(4, usuario.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deletar(long id, Connection conn) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public List<Usuario> buscarUsuarioPorNome(String nome, Connection conn) throws SQLException {
        if (nome == null || nome.isBlank()) {
            return new ArrayList<>();
        }
        String sql = "SELECT * FROM usuarios WHERE nome LIKE ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + nome + "%");
            List<Usuario> lista = new ArrayList<>();
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(mapearUsuario(rs));
            }
            return lista;
        }
    }

    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = UsuarioFactory.criarUsuario(rs.getString("tipo"), rs.getString("nome"));
        usuario.setId(rs.getLong("id"));
        usuario.getSaldo().setSaldoDevedor(rs.getDouble("saldo_devedor"));
        return usuario;
    }
}
