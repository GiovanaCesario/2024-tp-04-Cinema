package org.example.cinema.cinemaDAO;

public class CinemaExistenteException extends Exception{

    public CinemaExistenteException(int id) {

        super("Cinema de ID: " + id + " ja existente no banco de dados.");
    }
}
