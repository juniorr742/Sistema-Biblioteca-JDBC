package br.com.biblioteca.dao;

import br.com.biblioteca.model.Livro;

import java.sql.*;
import java.util.ArrayList;

public class LivroDAO implements IDao<Livro> {

    @Override
    public boolean salvar(Livro livro) throws SQLException{
        String sql = "INSERT INTO livros (titulo, autor, disponivel) VALUES (?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){

            stmt.setString(1, livro.getTitulo());
            stmt.setString(2, livro.getAutor());
            stmt.setBoolean(3, livro.isDisponivel());

            int linhasAfetadas = stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();

            if (rs.next()){
                livro.setId(rs.getLong(1));
            }

            return linhasAfetadas > 0;
        }
    }

    public Livro buscarLivroPorId(long id) throws SQLException{
        String sql = "SELECT * FROM livros WHERE id = ?";

        try(Connection conn = ConnectionFactory.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setLong(1, id);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()){
                return mapearLivro(rs);
            }
            return null;
        }
    }

    private Livro mapearLivro(ResultSet rs) throws SQLException {
        Livro livro = new Livro(
                rs.getString("titulo"),
                rs.getString("autor")
        );
        livro.setId(rs.getLong("id"));
        livro.setDisponivel(rs.getBoolean("disponivel"));
        return livro;
    }
}
