package br.com.biblioteca.model;

import java.time.LocalDate;


public class RegistroEmprestimo {
    private long idTransacao;
    private long idUsuario;
    private long idLivro;
    private LocalDate dataEmprestimo;
    private LocalDate dataDevolucao;
    private boolean finalizado;

    public RegistroEmprestimo(long idUsuario, long idLivro){
        this.idUsuario = idUsuario;
        this.idLivro = idLivro;
        this.dataEmprestimo = LocalDate.now();
        this.finalizado = false;
    }

    public long getIdTransacao() {
        return idTransacao;
    }

    public LocalDate getDataDevolucao() {
        return dataDevolucao;
    }

    public void setIdTransacao(long idTransacao) {
        this.idTransacao = idTransacao;
    }

    public void setDataDevolucao(LocalDate dataDevolucao) {
        this.dataDevolucao = dataDevolucao;
    }

    public void setDataEmprestimo(LocalDate dataEmprestimo) {
        this.dataEmprestimo = dataEmprestimo;
    }

    public void setFinalizado(boolean finalizado) {
        this.finalizado = finalizado;
    }

    public long getIdUsuario() {
        return idUsuario;
    }

    public long getIdLivro() {
        return idLivro;
    }

    public boolean isFinalizado() {
        return finalizado;
    }

    public LocalDate getDataEmprestimo() {
        return dataEmprestimo;
    }

    public void finalizarEmprestimo(){
        this.dataDevolucao = LocalDate.now();
        this.finalizado = true;
    }

    @Override
    public String toString(){
        return String.format("Transação: %d | Usuário ID: %d | br.com.biblioteca.model.Livro ID: %d | Data: %s | Status: %s",
                idTransacao, idUsuario, idLivro, dataEmprestimo, (finalizado ? "Devolvido":"Ativo"));
    }
}
