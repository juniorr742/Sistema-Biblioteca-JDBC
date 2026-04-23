package br.com.biblioteca.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface IDao<T> {
    boolean salvar(T objeto, Connection conn) throws SQLException;
    T buscarPorId(long id, Connection conn) throws SQLException;
    List<T> listarTodos(Connection conn) throws SQLException;
    boolean atualizar(T objeto, Connection conn) throws SQLException;
    boolean deletar(long id, Connection conn) throws SQLException;
}
