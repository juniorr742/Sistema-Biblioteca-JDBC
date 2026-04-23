package br.com.biblioteca.factory;

import br.com.biblioteca.model.Usuario;
import br.com.biblioteca.model.Aluno;
import br.com.biblioteca.model.Professor;

public class UsuarioFactory {
    public static Usuario criarUsuario(String tipo, String nome) {
        if (tipo == null || tipo.isBlank()) {
            return null;
        }
        switch (tipo.trim().toLowerCase()) {
            case "aluno":
                return new Aluno(nome);
            case "professor":
                return new Professor(nome);
            default:
                return null;
        }
    }
}
