package br.com.biblioteca.dao;

import java.sql.SQLException;
import java.util.List;

public interface IDao<T> {

    boolean salvar(T objeto) throws SQLException;

    T buscarPorId(long id) throws SQLException;

    List<T> listarTodos() throws SQLException;

    boolean atualizar(T objeto) throws SQLException;

    boolean deletar(long id) throws SQLException;
}
