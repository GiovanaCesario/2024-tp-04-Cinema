package org.example.filme.filmeDAO;

public class FilmeNaoEncontradoException extends Exception{

    public FilmeNaoEncontradoException(int id) {

        super("Filme de ID: " + id + " não encontrado.");
    }
}
