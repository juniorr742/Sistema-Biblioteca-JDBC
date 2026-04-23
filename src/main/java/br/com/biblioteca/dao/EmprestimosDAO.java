package br.com.biblioteca.dao;

import br.com.biblioteca.model.RegistroEmprestimo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmprestimosDAO implements IDao<RegistroEmprestimo> {

    @Override
    public boolean salvar(RegistroEmprestimo registro, Connection conn) throws SQLException {
        String sql = "INSERT INTO registros (id_usuario, id_livro, data_emprestimo, finalizado) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, registro.getIdUsuario());
            stmt.setLong(2, registro.getIdLivro());
            stmt.setDate(3, Date.valueOf(registro.getDataEmprestimo()));
            stmt.setBoolean(4, registro.isFinalizado());

            int linhasAfetadas = stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                registro.setIdTransacao(rs.getLong(1));
            }
            return linhasAfetadas > 0;
        }
    }

    @Override
    public RegistroEmprestimo buscarPorId(long id, Connection conn) throws SQLException {
        String sql = "SELECT * FROM registros WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapearEmprestimo(rs);
            }
            return null;
        }
    }

    @Override
    public List<RegistroEmprestimo> listarTodos(Connection conn) throws SQLException {
        String sql = "SELECT * FROM registros";
        List<RegistroEmprestimo> lista = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(mapearEmprestimo(rs));
            }
            return lista;
        }
    }

    @Override
    public boolean atualizar(RegistroEmprestimo registro, Connection conn) throws SQLException {
        String sql = "UPDATE registros SET data_devolucao = ?, finalizado = ? WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (registro.getDataDevolucao() != null) {
                stmt.setDate(1, Date.valueOf(registro.getDataDevolucao()));
            } else {
                stmt.setNull(1, Types.DATE);
            }
            stmt.setBoolean(2, registro.isFinalizado());
            stmt.setLong(3, registro.getIdTransacao());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deletar(long id, Connection conn) throws SQLException {
        String sql = "DELETE FROM registros WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public List<RegistroEmprestimo> buscarEmprestimosAtivos(long idUsuario, Connection conn) throws SQLException {
        String sql = "SELECT * FROM registros WHERE id_usuario = ? AND finalizado = false";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idUsuario);
            ResultSet rs = stmt.executeQuery();
            List<RegistroEmprestimo> lista = new ArrayList<>();
            while (rs.next()) {
                lista.add(mapearEmprestimo(rs));
            }
            return lista;
        }
    }

    private RegistroEmprestimo mapearEmprestimo(ResultSet rs) throws SQLException {
        RegistroEmprestimo registro = new RegistroEmprestimo(rs.getLong("id_usuario"), rs.getLong("id_livro"));
        registro.setIdTransacao(rs.getLong("id"));
        registro.setDataEmprestimo(rs.getDate("data_emprestimo").toLocalDate());
        if (rs.getDate("data_devolucao") != null) {
            registro.setDataDevolucao(rs.getDate("data_devolucao").toLocalDate());
        }
        registro.setFinalizado(rs.getBoolean("finalizado"));
        return registro;
    }
}
