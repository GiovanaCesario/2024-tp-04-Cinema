package org.example.exceptions;

public class IdExistenteException extends Exception {

    public IdExistenteException(int id) {

        super("Cinema com ID: " + id + " ja existente");
    }
}
