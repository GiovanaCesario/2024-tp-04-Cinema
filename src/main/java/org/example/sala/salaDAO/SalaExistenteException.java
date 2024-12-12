package org.example.sala.salaDAO;

public class SalaExistenteException extends Exception{

    public SalaExistenteException(String nomeSala) {

        super("Sala de nome: " + nomeSala + " ja existente no banco de dados.");
    }
}
