package br.com.biblioteca.view;

import br.com.biblioteca.controller.Biblioteca;
import br.com.biblioteca.dao.EmprestimosDAO;
import br.com.biblioteca.dao.LivroDAO;
import br.com.biblioteca.dao.UsuarioDAO;
import br.com.biblioteca.service.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        UsuarioDAO usuarioDAO = new UsuarioDAO();
        PagamentoService pagamentoService = new PagamentoService(usuarioDAO);
        ValidadorEmprestimo validadorEmprestimo = new ValidadorEmprestimo();
        CalculadoraMulta calculadoraMulta = new CalculadoraMulta();
        EmprestimosDAO emprestimosDAO = new EmprestimosDAO();
        LivroDAO livroDAO = new LivroDAO();
        EmprestimoService emprestimoService = new EmprestimoService(validadorEmprestimo, calculadoraMulta, pagamentoService, livroDAO, usuarioDAO);

        Biblioteca biblioteca = new Biblioteca(pagamentoService, emprestimoService, livroDAO, usuarioDAO, emprestimosDAO);

        MenuUsuario menuUsuario = new MenuUsuario(biblioteca, sc);
        MenuLivro menuLivro = new MenuLivro(biblioteca, sc);
        MenuFinanceiro menuFinanceiro = new MenuFinanceiro(biblioteca, sc);
        MenuPrincipal menuPrincipal = new MenuPrincipal(biblioteca,sc,menuUsuario,menuLivro,menuFinanceiro);

        menuPrincipal.iniciar();
    }
}
