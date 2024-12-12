package org.example.filme.filmeDAO;

public class FilmeExistenteException extends Exception{

    public FilmeExistenteException(int id) {

        super("Filme de ID: " + id + " ja existente no banco de dados.");
    }
}
