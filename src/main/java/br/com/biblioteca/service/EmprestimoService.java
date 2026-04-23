package br.com.biblioteca.service;

import br.com.biblioteca.model.Livro;
import br.com.biblioteca.model.Usuario;

public class EmprestimoService {
    private ValidadorEmprestimo validador;
    private CalculadoraMulta calculadora;
    private PagamentoService pagamento;

    public EmprestimoService(ValidadorEmprestimo validador, CalculadoraMulta calculadora, PagamentoService pagamento) {
        this.validador = validador;
        this.calculadora = calculadora;
        this.pagamento = pagamento;
    }

    public boolean emprestarLivro(Usuario usuario, Livro livro) {
        if (!validador.podeEmprestar(usuario, livro)) {
            System.out.println("Empréstimo cancelado, erro de validação");
            return false;
        }
        pagamento.aplicarTaxaEmprestimo(usuario);
        livro.setDisponivel(false);
        System.out.println("Livro " + livro.getTitulo() + " emprestado com sucesso!");
        return true;
    }

    public void realizarDevolucao(Usuario usuario, Livro livro, int diasCorridos) {
        double valorMulta = calculadora.valorCalculado(diasCorridos);
        if (valorMulta > 0) {
            pagamento.aplicarMultaAtraso(usuario, valorMulta);
            System.out.println("Livro devolvido com atraso!");
        } else {
            System.out.println("Livro devolvido no prazo!");
        }
        livro.setDisponivel(true);
    }
}
