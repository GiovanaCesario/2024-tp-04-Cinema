package org.example.cinema;

import org.example.armazenamento.JDBC.conexao.FalhaConexaoException;
import org.example.cinema.cinemaDAO.CinemaDAO;
import org.example.cinema.cinemaDAO.CinemaExistenteException;
import org.example.exceptions.IdExistenteException;

import java.util.stream.Collectors;

public class Cinepolis extends Cinema {

    public static Cinepolis instancia;

    /* Construtor */
    private Cinepolis(int id, String local) throws CinemaExistenteException, FalhaConexaoException {

        super(id, "Cinepolis", local);
        super.setValorVenda(20);   //Diferencia um cinema dos demais
    }

    /* Singleton */
    public static Cinepolis getInstancia(int id, String local, boolean ehPraInserirNoBD) throws CinemaExistenteException, FalhaConexaoException,
            IdExistenteException {

        if (instancia == null) {

            if(cinemas.stream().anyMatch(a -> a.getId() == id)) {
                throw new IdExistenteException(id);
            }

            instancia = new Cinepolis(id, local);

            //Criacao de um filme
            if(ehPraInserirNoBD) {
                cinemaDAO = new CinemaDAO(true);
                cinemaDAO.insere(instancia);
                cinemas.add(instancia);
            }
        }
        return instancia;
    }
}
