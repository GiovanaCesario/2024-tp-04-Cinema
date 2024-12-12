package org.example.venda.vendaDAO;

public class VendaExistenteException extends Exception {

    public VendaExistenteException(int id) {

        super("Venda de ID: " + id + " ja existente no banco de dados.");
    }
}
