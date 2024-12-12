package org.example.venda.vendaDAO;

public class VendaNaoEncontradaException extends Exception {

    public VendaNaoEncontradaException(int id) {

        super("Venda de ID: " + id + " nao encontrada.");
    }
}
