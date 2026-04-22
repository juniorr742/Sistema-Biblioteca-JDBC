package br.com.biblioteca.dao;
import br.com.biblioteca.model.RegistroEmprestimo;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmprestimosDAO implements IDao<RegistroEmprestimo> {

    @Override
    public boolean salvar(RegistroEmprestimo registroEmprestimo) throws SQLException{
        String sql = "INSERT INTO registros (id_usuario, id_livro, data_emprestimo, finalizado) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){

            stmt.setLong(1, registroEmprestimo.getIdUsuario());
            stmt.setLong(2, registroEmprestimo.getIdLivro());
            stmt.setDate(3, Date.valueOf(registroEmprestimo.getDataEmprestimo()));
            stmt.setBoolean(4, registroEmprestimo.isFinalizado());



            int linhasAfetadas = stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();

            if (rs.next()){
               registroEmprestimo.setIdTransacao(rs.getLong(1));
            }

            return linhasAfetadas > 0;
        }
    }

    @Override
    public RegistroEmprestimo buscarPorId(long id) throws SQLException{
         String sql = "SELECT * FROM registros WHERE id = ?";

         try (Connection conn = ConnectionFactory.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){
             stmt.setLong(1, id);

             ResultSet rs = stmt.executeQuery();

             if (rs.next()) {
                 return mapearEmprestimo(rs);
             }
             return null;
         }
    }

    @Override
    public List<RegistroEmprestimo> listarTodos() throws SQLException{
        String sql = "SELECT * FROM registros";
        List<RegistroEmprestimo> lista = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){

            ResultSet rs = stmt.executeQuery();

            while (rs.next()){
                lista.add(mapearEmprestimo(rs));
            }
            return lista;
        }
    }

    @Override
    public boolean atualizar(RegistroEmprestimo registroEmprestimo)throws SQLException{
        String sql = "UPDATE registros SET data_devolucao = ?, finalizado = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){

            if (registroEmprestimo.getDataDevolucao() != null) {
                stmt.setDate(1, Date.valueOf(registroEmprestimo.getDataDevolucao()));
            } else {
                stmt.setNull(1, Types.DATE);
            }
            stmt.setBoolean(2, registroEmprestimo.isFinalizado());
            stmt.setLong(3, registroEmprestimo.getIdTransacao());

            return stmt.executeUpdate() > 0;

            //Aqui resolvi não colocar opção de mudar id livro e nem de usuario, se foi cadastrado errado, o adimin deve finalizar a operação manualmente e fazer outra
        }
    }

    @Override
    public boolean deletar(long id) throws SQLException{
        String sql = "DELETE FROM registros WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setLong(1, id);

            return stmt.executeUpdate() > 0;
        }
    }

    public List<RegistroEmprestimo> buscarEmprestimosAtivos(long idUsuario)throws SQLException{
        String sql = "SELECT * FROM registros WHERE id = ? AND finalizado = false";

        try (Connection conn = ConnectionFactory.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setLong(1, idUsuario);

            ResultSet rs = stmt.executeQuery();
            List<RegistroEmprestimo> lista = new ArrayList<>();
            while (rs.next()){
                lista.add(mapearEmprestimo(rs));
            }
            return lista;
        }
    }

    private RegistroEmprestimo mapearEmprestimo(ResultSet rs) throws SQLException {
        RegistroEmprestimo registroEmprestimo = new RegistroEmprestimo(rs.getLong("id_usuario"), rs.getLong("id_livro"));

        registroEmprestimo.setIdTransacao(rs.getLong("id"));
        registroEmprestimo.setDataEmprestimo(rs.getDate("data_emprestimo").toLocalDate());
        //Date dataDevolucao = rs.getDate("data_devolucao");
        if (rs.getDate("data_devolucao") != null){
            registroEmprestimo.setDataDevolucao(rs.getDate("data_devolucao").toLocalDate());
        }
        registroEmprestimo.setFinalizado(rs.getBoolean("finalizado"));
        return registroEmprestimo;
    }


}
