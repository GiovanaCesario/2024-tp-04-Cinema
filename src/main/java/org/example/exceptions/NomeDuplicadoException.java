package org.example.exceptions;

public class NomeDuplicadoException extends Exception {

    public NomeDuplicadoException(String nomeSala, String nomeCinema) {

        super("A sala de nome " + nomeSala + " ja existe no " + nomeCinema);
    }
}
