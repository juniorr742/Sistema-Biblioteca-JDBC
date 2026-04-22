package br.com.biblioteca.factory;

import br.com.biblioteca.model.Usuario;
import br.com.biblioteca.model.Aluno;
import br.com.biblioteca.model.Professor;

public class UsuarioFactory {
    public static Usuario criarUsuario(String tipo, String nome) {
        if (tipo == null || tipo.isBlank()) {
            throw new RuntimeException("Tipo não pode ser vazio.");
        }
        switch (tipo.trim()) {
            case "Aluno":
                return new Aluno(nome);
            case "Professor":
                return new Professor(nome);
            default:
                throw new RuntimeException("Tipo desconhecido: " + tipo);
        }
    }
}
