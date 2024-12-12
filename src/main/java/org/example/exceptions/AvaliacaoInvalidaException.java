package org.example.exceptions;

public class AvaliacaoInvalidaException extends Exception {

    public AvaliacaoInvalidaException(int avaliacao) {

        super("A avaliacao " +avaliacao+ " nao corresponde aos parametros: > 0 e < 5");
    }
}
