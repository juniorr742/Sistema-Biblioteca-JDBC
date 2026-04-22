package br.com.biblioteca.service;

import br.com.biblioteca.dao.EmprestimosDAO;
import br.com.biblioteca.dao.LivroDAO;
import br.com.biblioteca.dao.UsuarioDAO;
import br.com.biblioteca.model.Livro;
import br.com.biblioteca.model.Usuario;

import java.sql.SQLException;
import java.util.List;

public class EmprestimoService {
    private ValidadorEmprestimo validador;
    private CalculadoraMulta calculadora;
    private PagamentoService pagamento;
    private LivroDAO livroDAO;
    private UsuarioDAO usuarioDAO;

    public EmprestimoService(ValidadorEmprestimo validador, CalculadoraMulta calculadora, PagamentoService pagamento, LivroDAO livroDAO, UsuarioDAO usuarioDAO){
        this.validador = validador;
        this.calculadora = calculadora;
        this.pagamento = pagamento;
        this.livroDAO = livroDAO;
        this.usuarioDAO = usuarioDAO;
    }


    public void emprestarLivro(Usuario usuario, Livro livro){
        if (!validador.podeEmprestar(usuario,livro)){
            System.out.println("Empréstimo cancelado, erro de validação");
        }else {
            pagamento.aplicarTaxaEmprestimo(usuario);
            livro.setDisponivel(false);
            try {
                livroDAO.atualizar(livro);
            } catch (SQLException e) {
                System.out.println("Erro ao atualizar livro no banco" + e.getMessage());
            }
            System.out.println("Livro " + livro.getTitulo() + " emprestado com sucesso!");
        }
    }

    public void realizarDevolucao(Usuario usuario, Livro livro, int diasCorridos){

        double valorMulta = calculadora.valorCalculado(diasCorridos);
        if (valorMulta > 0){
            pagamento.aplicarMultaAtraso(usuario, valorMulta);
            System.out.println("Livro devolvido com sucesso!");
        }else {
            System.out.println("Livro devolvido no prazo!");
        }
        usuario.removerLivro(livro);
        livro.setDisponivel(true);
        try {
            livroDAO.atualizar(livro);
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar livro no banco" + e.getMessage());
        }
        System.out.println("O livro "+ livro.getTitulo() + " foi devolvido com sucesso!");
    }
}
