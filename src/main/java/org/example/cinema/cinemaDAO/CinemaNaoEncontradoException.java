package org.example.cinema.cinemaDAO;

public class CinemaNaoEncontradoException extends Exception{

    public CinemaNaoEncontradoException(int id) {

        super("Cinema de ID: " + id + " não encontrado.");
    }
}
