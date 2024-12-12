package org.example.exceptions;

public class SalaOcupadaException extends Exception {

    public SalaOcupadaException(String nomeSala) {

        super("A sala " + nomeSala + " esta ocupada nesse horario");
    }
}
