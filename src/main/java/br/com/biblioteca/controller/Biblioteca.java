package br.com.biblioteca.controller;

import br.com.biblioteca.dao.EmprestimosDAO;
import br.com.biblioteca.dao.LivroDAO;
import br.com.biblioteca.dao.UsuarioDAO;
import br.com.biblioteca.model.Livro;
import br.com.biblioteca.model.RegistroEmprestimo;
import br.com.biblioteca.model.Usuario;
import br.com.biblioteca.service.EmprestimoService;
import br.com.biblioteca.service.PagamentoService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Biblioteca {
    //private List<Livro> acervo;
    //private List<Usuario> usuarios;
    //private List<RegistroEmprestimo> historicoEmprestimos;
    private PagamentoService pagamentoService;
    private EmprestimoService emprestimoService;
    private LivroDAO livroDAO;
    private UsuarioDAO usuarioDAO;
    private EmprestimosDAO emprestimosDAO;

    public Biblioteca(PagamentoService pagamentoService, EmprestimoService emprestimoService, LivroDAO livroDAO, UsuarioDAO usuarioDAO, EmprestimosDAO emprestimosDAO){
        //this.acervo = new ArrayList<>();
        this.livroDAO = livroDAO;
        //this.usuarios = new ArrayList<>();
        this.usuarioDAO = usuarioDAO;
        //this.historicoEmprestimos = new ArrayList<>();
        this.emprestimosDAO = emprestimosDAO;
        this.pagamentoService = pagamentoService;
        this.emprestimoService = emprestimoService;
    }

    public Usuario buscarUsuariosPorId(long id){
        try {
            return usuarioDAO.buscarPorId(id);
        } catch (SQLException e){
            System.out.println("Erro ao acessar o banco "+ e.getMessage() );
            return null;
        }
    }

    public Livro buscarLivroPorId(long id){
        try {
            return livroDAO.buscarPorId(id);
        } catch (SQLException e) {
            System.out.println("Erro ao acessar o banco "+ e.getMessage());
            return null;
        }
    }

    public void adicionarUsuario(Usuario novoUsuario){
        try {
            usuarioDAO.salvar(novoUsuario);
            System.out.println("Usuário adicionado.");
        } catch (SQLException e) {
            System.out.println("Erro ao acessar o banco" + e.getMessage());
        }
    }

    public void adicionarLivro(Livro livro){
        try {
            livroDAO.salvar(livro);
            System.out.println("Livro adicionado com sucesso");
        } catch (SQLException e) {
            System.out.println("Erro ao acessar o banco" + e.getMessage());
        }
    }

    public List<Usuario> listarTodosUsuarios(){
        try {
            return usuarioDAO.listarTodos();
        } catch (SQLException e) {
            System.out.println("Erro ao acessar banco "+ e.getMessage());
            return  null;
        }
    }

    public List<Livro> listarTodosLivros(){
        try {
            return livroDAO.listarTodos();
        } catch (SQLException e) {
            System.out.println("Erro ao acessar o banco " + e.getMessage());
            return null;
        }
    }

     public void listarHistorico(){
         System.out.println("\n=====HISTÓRICO DOS REGISTROS=====");
         try {
             emprestimosDAO.listarTodos();
         } catch (SQLException e) {
             System.out.println("Erro ao acessar o banco " + e.getMessage());
         }
     }

    public List<Livro> procurarLivroPorTitulo(String termoBusca){
        try {
            return livroDAO.buscarLivroPorTitulo(termoBusca);
        } catch (SQLException e) {
            System.out.println("Erro ao acessar o banco. " + e.getMessage());
            return null;
        }
    }

    public List<Usuario> procurarUsuarioPorNome(String termoBusca){
        try {
            return usuarioDAO.buscarUsuarioPorNome(termoBusca);
        } catch (SQLException e) {
            System.out.println("Erro ao acessar o banco. "+ e.getMessage());
            return null;
        }
    }

   public void devolverLivro(long idLivro, long idUsuario, int diasCorridos) {
       Usuario usuario = buscarUsuariosPorId(idUsuario);
       Livro livro = buscarLivroPorId(idLivro);
       if (usuario != null && livro != null) {
          try {
              List<RegistroEmprestimo> ativos = emprestimosDAO.buscarEmprestimosAtivos(idUsuario);
              RegistroEmprestimo registro = ativos.stream().filter(r -> r.getIdLivro() == idLivro)
                      .findFirst().orElse(null);
              if (registro == null){
                  System.out.println("ERRO: Nenhum empréstimo ativo encontrado para esse livro");
                  return;
              }

              emprestimoService.realizarDevolucao(usuario, livro, diasCorridos);

              registro.setDataDevolucao(LocalDate.now());
              registro.setFinalizado(true);
              emprestimosDAO.atualizar(registro);
          } catch (SQLException e) {
              System.out.println("Erro ao conectar com o banco " + e.getMessage());
          }
       } else {
           System.out.println("[AVISO] - Usuário ou livro não encontrado.");
       }

   }

    public void emprestarLivro(long idLivro, long idUsuario) {
        Livro livroEncontrado = buscarLivroPorId(idLivro);
        Usuario usuarioEncontrado = buscarUsuariosPorId(idUsuario);
        if (livroEncontrado == null || usuarioEncontrado == null) {
            System.out.println("ERRO: livro ou usuário não encontrado");
            return;
        }
        emprestimoService.emprestarLivro(usuarioEncontrado, livroEncontrado);
            if (!livroEncontrado.isDisponivel()){
                RegistroEmprestimo novoRegistro = new RegistroEmprestimo(idUsuario, idLivro);
                try {
                    emprestimosDAO.salvar(novoRegistro);
                    System.out.println("Hisórico gerado: Transação #"+ novoRegistro.getIdTransacao());
                } catch (SQLException e) {
                    System.out.println("Erro ao acessar o banco "+ e.getMessage());
                }
            }else {
                System.out.println("[AVISO] Erro ao realizar empréstimo");
            }

    }

    public void verificarSaldo(long id){
        Usuario usuarioEcontrado = buscarUsuariosPorId(id);

            if (usuarioEcontrado != null){

                System.out.println("Seu saldo devedor é: " + usuarioEcontrado.getSaldo().getSaldoDevedor());
            } else {
                System.out.println("Id não identificado");
            }
        }

    public void realizarPagamento(long id){
        Usuario usuarioEcontrado = buscarUsuariosPorId(id);
        if (usuarioEcontrado != null){
            pagamentoService.processarPagamentoTotal(usuarioEcontrado);
        } else {
            System.out.println("Erro: Usuário: " + id + "não encontrado.");
        }
    }
}

