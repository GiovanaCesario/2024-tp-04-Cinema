package org.example.sessao.sessaoDAO;

public class SessaoNaoEncontradaException extends Exception {

    public SessaoNaoEncontradaException(int id) {

        super("Sessao de ID: " + id + " nao encontrada.");
    }
}
