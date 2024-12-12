package org.example.cinema;

import org.example.armazenamento.JDBC.conexao.FalhaConexaoException;
import org.example.cinema.cinemaDAO.CinemaDAO;
import org.example.cinema.cinemaDAO.CinemaExistenteException;
import org.example.exceptions.IdExistenteException;

import java.util.stream.Collectors;

public class Cinemark extends Cinema {

    public static Cinemark instancia;

    /* Construtor */
    private Cinemark(int id, String local) throws CinemaExistenteException, FalhaConexaoException {

        super(id, "Cinemark", local);
        super.setValorVenda(30);  //Diferencia um cinema dos demais
    }

    /* Singleton */
    public static Cinemark getInstancia(int id, String local, boolean ehPraInserirNoBD) throws CinemaExistenteException, FalhaConexaoException,
            IdExistenteException {

        if (instancia == null) {

            if(cinemas.stream().anyMatch(a -> a.getId() == id)) {
                throw new IdExistenteException(id);
            }

            instancia = new Cinemark(id, local);

            if(ehPraInserirNoBD) {
                cinemaDAO = new CinemaDAO(true);
                cinemaDAO.insere(instancia);
                cinemas.add(instancia);
            }
        }
        return instancia;
    }
}
