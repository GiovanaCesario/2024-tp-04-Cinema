package org.example.armazenamento.JDBC;

import org.example.armazenamento.JDBC.conexao.FalhaConexaoException;
import org.example.cinema.cinemaDAO.CinemaExistenteException;
import org.example.cinema.cinemaDAO.CinemaNaoEncontradoException;
import org.example.exceptions.IdExistenteException;
import org.example.filme.filmeDAO.FilmeExistenteException;
import org.example.filme.filmeDAO.FilmeNaoEncontradoException;
import org.example.sala.salaDAO.SalaExistenteException;
import org.example.sala.salaDAO.SalaNaoEncontradaException;
import org.example.sessao.sessaoDAO.SessaoExistenteException;
import org.example.sessao.sessaoDAO.SessaoNaoEncontradaException;
import org.example.venda.vendaDAO.VendaExistenteException;
import org.example.venda.vendaDAO.VendaNaoEncontradaException;

import java.util.List;

public interface IDAO<T, P> {

    void criaTabela() throws FalhaConexaoException;

    T obtem(P chave) throws FalhaConexaoException, CinemaNaoEncontradoException, FilmeNaoEncontradoException, SalaNaoEncontradaException, VendaNaoEncontradaException, SessaoNaoEncontradaException, IdExistenteException;

    void insere(T objeto) throws FalhaConexaoException, CinemaExistenteException, FilmeExistenteException, SalaExistenteException, VendaExistenteException, SessaoExistenteException;

    void atualiza(T objeto) throws FalhaConexaoException, CinemaExistenteException, FilmeExistenteException, SalaNaoEncontradaException, VendaNaoEncontradaException, SessaoNaoEncontradaException;

    void remove(T objeto) throws FalhaConexaoException, CinemaNaoEncontradoException, FilmeNaoEncontradoException, SalaNaoEncontradaException, VendaNaoEncontradaException, SessaoNaoEncontradaException;

    List<T> obtemLista() throws FalhaConexaoException, CinemaNaoEncontradoException, FilmeNaoEncontradoException, SalaNaoEncontradaException, SessaoNaoEncontradaException, CinemaExistenteException, IdExistenteException;
}
