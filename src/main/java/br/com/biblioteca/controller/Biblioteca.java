package br.com.biblioteca.controller;

import br.com.biblioteca.dao.ConnectionFactory;
import br.com.biblioteca.dao.EmprestimosDAO;
import br.com.biblioteca.dao.LivroDAO;
import br.com.biblioteca.dao.UsuarioDAO;
import br.com.biblioteca.model.Livro;
import br.com.biblioteca.model.RegistroEmprestimo;
import br.com.biblioteca.model.Usuario;
import br.com.biblioteca.service.EmprestimoService;
import br.com.biblioteca.service.PagamentoService;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Biblioteca {
    private PagamentoService pagamentoService;
    private EmprestimoService emprestimoService;
    private LivroDAO livroDAO;
    private UsuarioDAO usuarioDAO;
    private EmprestimosDAO emprestimosDAO;

    public Biblioteca(PagamentoService pagamentoService, EmprestimoService emprestimoService, LivroDAO livroDAO, UsuarioDAO usuarioDAO, EmprestimosDAO emprestimosDAO) {
        this.livroDAO = livroDAO;
        this.usuarioDAO = usuarioDAO;
        this.emprestimosDAO = emprestimosDAO;
        this.pagamentoService = pagamentoService;
        this.emprestimoService = emprestimoService;
    }

    public Usuario buscarUsuariosPorId(long id) {
        try (Connection conn = ConnectionFactory.getConnection()) {
            return usuarioDAO.buscarPorId(id, conn);
        } catch (SQLException e) {
            System.out.println("Erro ao acessar o banco " + e.getMessage());
            return null;
        }
    }

    public Livro buscarLivroPorId(long id) {
        try (Connection conn = ConnectionFactory.getConnection()) {
            return livroDAO.buscarPorId(id, conn);
        } catch (SQLException e) {
            System.out.println("Erro ao acessar o banco " + e.getMessage());
            return null;
        }
    }

    public void adicionarUsuario(Usuario novoUsuario) {
        try (Connection conn = ConnectionFactory.getConnection()) {
            usuarioDAO.salvar(novoUsuario, conn);
            System.out.println("Usuário adicionado.");
        } catch (SQLException e) {
            System.out.println("Erro ao acessar o banco " + e.getMessage());
        }
    }

    public void adicionarLivro(Livro livro) {
        try (Connection conn = ConnectionFactory.getConnection()) {
            livroDAO.salvar(livro, conn);
            System.out.println("Livro adicionado com sucesso");
        } catch (SQLException e) {
            System.out.println("Erro ao acessar o banco " + e.getMessage());
        }
    }

    public List<Usuario> listarTodosUsuarios() {
        try (Connection conn = ConnectionFactory.getConnection()) {
            return usuarioDAO.listarTodos(conn);
        } catch (SQLException e) {
            System.out.println("Erro ao acessar banco " + e.getMessage());
            return null;
        }
    }

    public List<Livro> listarTodosLivros() {
        try (Connection conn = ConnectionFactory.getConnection()) {
            return livroDAO.listarTodos(conn);
        } catch (SQLException e) {
            System.out.println("Erro ao acessar o banco " + e.getMessage());
            return null;
        }
    }

    public void listarHistorico() {
        System.out.println("\n=====HISTÓRICO DOS REGISTROS=====");
        try (Connection conn = ConnectionFactory.getConnection()) {
            emprestimosDAO.listarTodos(conn).forEach(System.out::println);
        } catch (SQLException e) {
            System.out.println("Erro ao acessar o banco " + e.getMessage());
        }
    }

    public List<Livro> procurarLivroPorTitulo(String termoBusca) {
        try (Connection conn = ConnectionFactory.getConnection()) {
            return livroDAO.buscarLivroPorTitulo(termoBusca, conn);
        } catch (SQLException e) {
            System.out.println("Erro ao acessar o banco. " + e.getMessage());
            return null;
        }
    }

    public List<Usuario> procurarUsuarioPorNome(String termoBusca) {
        try (Connection conn = ConnectionFactory.getConnection()) {
            return usuarioDAO.buscarUsuarioPorNome(termoBusca, conn);
        } catch (SQLException e) {
            System.out.println("Erro ao acessar o banco. " + e.getMessage());
            return null;
        }
    }

    public List<Livro> listarLivrosEmprestadosPorUsuario(long idUsuario) {
        try (Connection conn = ConnectionFactory.getConnection()) {
            List<RegistroEmprestimo> ativos = emprestimosDAO.buscarEmprestimosAtivos(idUsuario, conn);
            List<Livro> livros = new ArrayList<>();
            for (RegistroEmprestimo r : ativos) {
                Livro l = livroDAO.buscarPorId(r.getIdLivro(), conn);
                if (l != null) livros.add(l);
            }
            return livros;
        } catch (SQLException e) {
            System.out.println("Erro ao acessar o banco " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public void emprestarLivro(long idLivro, long idUsuario) {
        try (Connection conn = ConnectionFactory.getConnection()) {
            conn.setAutoCommit(false);
            try {
                Livro livro = livroDAO.buscarPorId(idLivro, conn);
                Usuario usuario = usuarioDAO.buscarPorId(idUsuario, conn);
                if (livro == null || usuario == null) {
                    System.out.println("ERRO: livro ou usuário não encontrado");
                    conn.rollback();
                    return;
                }

                boolean sucesso = emprestimoService.emprestarLivro(usuario, livro);
                if (!sucesso) {
                    conn.rollback();
                    return;
                }

                livroDAO.atualizar(livro, conn);
                usuarioDAO.atualizar(usuario, conn);

                RegistroEmprestimo novoRegistro = new RegistroEmprestimo(idUsuario, idLivro);
                emprestimosDAO.salvar(novoRegistro, conn);

                conn.commit();
                System.out.println("Histórico gerado: Transação #" + novoRegistro.getIdTransacao());
            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Erro ao realizar empréstimo: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.out.println("Erro ao acessar o banco " + e.getMessage());
        }
    }

    public void devolverLivro(long idLivro, long idUsuario, int diasCorridos) {
        try (Connection conn = ConnectionFactory.getConnection()) {
            conn.setAutoCommit(false);
            try {
                Usuario usuario = usuarioDAO.buscarPorId(idUsuario, conn);
                Livro livro = livroDAO.buscarPorId(idLivro, conn);
                if (usuario == null || livro == null) {
                    System.out.println("[AVISO] - Usuário ou livro não encontrado.");
                    conn.rollback();
                    return;
                }

                List<RegistroEmprestimo> ativos = emprestimosDAO.buscarEmprestimosAtivos(idUsuario, conn);
                RegistroEmprestimo registro = ativos.stream()
                        .filter(r -> r.getIdLivro() == idLivro)
                        .findFirst().orElse(null);
                if (registro == null) {
                    System.out.println("ERRO: Nenhum empréstimo ativo encontrado para esse livro");
                    conn.rollback();
                    return;
                }

                emprestimoService.realizarDevolucao(usuario, livro, diasCorridos);

                livroDAO.atualizar(livro, conn);
                usuarioDAO.atualizar(usuario, conn);

                registro.setDataDevolucao(LocalDate.now());
                registro.setFinalizado(true);
                emprestimosDAO.atualizar(registro, conn);

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Erro ao conectar com o banco " + e.getMessage());
            }
        } catch (SQLException e) {
            System.out.println("Erro ao acessar o banco " + e.getMessage());
        }
    }

    public void verificarSaldo(long id) {
        Usuario usuario = buscarUsuariosPorId(id);
        if (usuario != null) {
            System.out.println("Seu saldo devedor é: " + usuario.getSaldo().getSaldoDevedor());
        } else {
            System.out.println("Id não identificado");
        }
    }

    public void realizarPagamento(long id) {
        try (Connection conn = ConnectionFactory.getConnection()) {
            conn.setAutoCommit(false);
            try {
                Usuario usuario = usuarioDAO.buscarPorId(id, conn);
                if (usuario == null) {
                    System.out.println("Erro: Usuário " + id + " não encontrado.");
                    conn.rollback();
                    return;
                }
                pagamentoService.processarPagamentoTotal(usuario);
                usuarioDAO.atualizar(usuario, conn);
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Erro ao acessar o banco " + e.getMessage());
            }
        } catch (SQLException e) {
            System.out.println("Erro ao acessar o banco " + e.getMessage());
        }
    }
}
