package br.com.biblioteca.service;

import br.com.biblioteca.config.BibliotecaConfig;
import br.com.biblioteca.dao.UsuarioDAO;
import br.com.biblioteca.model.Usuario;

import java.sql.SQLException;

public class PagamentoService {
    private UsuarioDAO usuarioDAO;

    public PagamentoService(UsuarioDAO usuarioDAO){
        this.usuarioDAO = usuarioDAO;
    }

    public void processarPagamentoTotal(Usuario usuario){

        double valorSaldoDevedor = usuario.getSaldo().getSaldoDevedor();

        if (valorSaldoDevedor > 0){
            System.out.println("[FINANCEIRO] - Saldo devededor de: "+ valorSaldoDevedor+ " processado para: " + usuario.getNome());
            usuario.getSaldo().quitarTotalmente();
            try {
                usuarioDAO.atualizar(usuario);
            } catch (SQLException e) {
                System.out.println("Erro ao acessar o banco " + e.getMessage());
            }
        }else {
            System.out.println("O usuário " + usuario.getNome() + " não tem pendências.");
        }
    }

    public void aplicarMultaAtraso(Usuario usuario,double valorMulta){
        if (valorMulta > 0) {
            usuario.getSaldo().aumentarDebito(valorMulta);
            try {
                usuarioDAO.atualizar(usuario);
            } catch (SQLException e) {
                System.out.println("Erro ao acessar o banco " + e.getMessage());
            }
            System.out.println("Multa de "+ valorMulta+ "R$ aplicada.");
        }else {
            System.err.println("ERRO: valor negativo.");
        }
    }

    public void aplicarTaxaEmprestimo(Usuario usuario){
        double taxa = BibliotecaConfig.CUSTO_FIXO_EMPRESTIMO;
        usuario.getSaldo().aumentarDebito(taxa);
        try {
            usuarioDAO.atualizar(usuario);
        } catch (SQLException e) {
            System.out.println("Erro ao acessar o banco " + e.getMessage());
        }
    }
}
