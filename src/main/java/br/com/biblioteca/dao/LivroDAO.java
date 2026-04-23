package br.com.biblioteca.dao;

import br.com.biblioteca.model.Livro;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LivroDAO implements IDao<Livro> {

    @Override
    public boolean salvar(Livro livro, Connection conn) throws SQLException {
        String sql = "INSERT INTO livros (titulo, autor, disponivel) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, livro.getTitulo());
            stmt.setString(2, livro.getAutor());
            stmt.setBoolean(3, livro.isDisponivel());

            int linhasAfetadas = stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                livro.setId(rs.getLong(1));
            }
            return linhasAfetadas > 0;
        }
    }

    @Override
    public Livro buscarPorId(long id, Connection conn) throws SQLException {
        String sql = "SELECT * FROM livros WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapearLivro(rs);
            }
            return null;
        }
    }

    @Override
    public List<Livro> listarTodos(Connection conn) throws SQLException {
        String sql = "SELECT * FROM livros";
        List<Livro> livros = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                livros.add(mapearLivro(rs));
            }
            return livros;
        }
    }

    @Override
    public boolean atualizar(Livro livro, Connection conn) throws SQLException {
        String sql = "UPDATE livros SET titulo = ?, autor = ?, disponivel = ? WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, livro.getTitulo());
            stmt.setString(2, livro.getAutor());
            stmt.setBoolean(3, livro.isDisponivel());
            stmt.setLong(4, livro.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deletar(long id, Connection conn) throws SQLException {
        String sql = "DELETE FROM livros WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public List<Livro> buscarLivroPorTitulo(String titulo, Connection conn) throws SQLException {
        if (titulo == null || titulo.isBlank()) {
            return new ArrayList<>();
        }
        String sql = "SELECT * FROM livros WHERE titulo LIKE ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + titulo.trim() + "%");
            ResultSet rs = stmt.executeQuery();
            List<Livro> lista = new ArrayList<>();
            while (rs.next()) {
                lista.add(mapearLivro(rs));
            }
            return lista;
        }
    }

    private Livro mapearLivro(ResultSet rs) throws SQLException {
        Livro livro = new Livro(rs.getString("titulo"), rs.getString("autor"));
        livro.setId(rs.getLong("id"));
        livro.setDisponivel(rs.getBoolean("disponivel"));
        return livro;
    }
}
