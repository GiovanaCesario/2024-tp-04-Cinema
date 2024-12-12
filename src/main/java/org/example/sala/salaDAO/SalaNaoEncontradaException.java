package org.example.sala.salaDAO;

public class SalaNaoEncontradaException extends Exception{

    public SalaNaoEncontradaException(String nomeSala) {

        super("Sala de nome: " + nomeSala + " nao encontrada.");
    }
}
