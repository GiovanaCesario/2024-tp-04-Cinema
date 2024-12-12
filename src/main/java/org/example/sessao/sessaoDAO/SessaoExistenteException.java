package org.example.sessao.sessaoDAO;

public class SessaoExistenteException extends Exception{

    public SessaoExistenteException(int id) {

        super("Sessao de ID: " + id + " ja existente no banco de dados.");
    }
}
