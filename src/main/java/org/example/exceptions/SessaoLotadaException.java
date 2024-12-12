package org.example.exceptions;

public class SessaoLotadaException extends Exception {

    public SessaoLotadaException(int idSessao) {

        super("A sessao de ID: " + idSessao + " esta lotada");
    }
}
