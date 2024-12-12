package org.example.sala.salaDAO;

import org.example.armazenamento.JDBC.IDAO;
import org.example.armazenamento.JDBC.conexao.*;
import org.example.cinema.cinemaDAO.CinemaDAO;
import org.example.cinema.cinemaDAO.CinemaExistenteException;
import org.example.cinema.cinemaDAO.CinemaNaoEncontradoException;
import org.example.exceptions.IdExistenteException;
import org.example.sala.Sala;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SalaDAO implements IDAO<Sala, String> {

    //Constantes
    private static final int NOME_POSICAO_TABELA = 1;
    private static final int CAPACIDADE_POSICAO_TABELA = 2;
    private static final int IDCINEMA_POSICAO_TABELA = 3;

    //Códigos de erro
    static final int DUPLICATE_KEY_ERROR_CODE = 1062;

    public SalaDAO(boolean ehPraCriarTabela) throws FalhaConexaoException {

        if(ehPraCriarTabela) {
            criaTabela();
        }
    }

    //Cria a tabela de salas caso ela não exista.
    @Override
    public void criaTabela() throws FalhaConexaoException {

        try {
            // Estabelecer conexao
            Connection conexao = Conexao.obtemConexao();

            // Faço a consulta
            Statement stmt = conexao.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS salas (" +
                    "nome VARCHAR(255) PRIMARY KEY, " +
                    "capacidade INT NOT NULL, " +
                    "idCinema INT NOT NULL, " +
                    "FOREIGN KEY (idCinema) REFERENCES cinemas(id) ON DELETE CASCADE ON UPDATE CASCADE)");

        } catch (SQLException e) {
            throw new Error(e.getMessage());
        }
    }


    //Obtem uma sala a partir do seu nome.
    @Override
    public Sala obtem(String nome) throws FalhaConexaoException, SalaNaoEncontradaException, CinemaNaoEncontradoException, IdExistenteException {

        CinemaDAO cinemaDAO = new CinemaDAO(false);
        ResultSet resultado = null;
        try {
            // Estabelecer conexao
            Connection conexao = Conexao.obtemConexao();

            // Faço a consulta
            PreparedStatement stmt = conexao.prepareStatement("SELECT * FROM salas WHERE nome = ?");
            stmt.setString(1, nome);
            resultado = stmt.executeQuery();

            if (resultado.next()) {
                // Obtenho os dados
                return new Sala(resultado.getString(NOME_POSICAO_TABELA),
                        resultado.getInt(CAPACIDADE_POSICAO_TABELA),
                        cinemaDAO.obtem(resultado.getInt(IDCINEMA_POSICAO_TABELA))
                );
            }

            // Se chegou aqui é porque não tem sala com esse nome
            throw new SalaNaoEncontradaException(nome);
        } catch (SQLException e) {
            throw new Error(e.getMessage());
        }
    }


    //Insere uma nova sala no banco de dados.
    @Override
    public void insere(Sala sala) throws FalhaConexaoException, SalaExistenteException {

        try {
            // Estabelecer conexao
            Connection conexao = Conexao.obtemConexao();

            // Faço a consulta
            PreparedStatement stmt = conexao.prepareStatement("INSERT INTO salas VALUES(?,?,?);");
            stmt.setString(1, sala.getNome());
            stmt.setInt(2, sala.getCapacidade());
            stmt.setInt(3, sala.getCinema().getId());
            stmt.execute();

        } catch (SQLException e) {
            if (e.getErrorCode() == DUPLICATE_KEY_ERROR_CODE) throw new SalaExistenteException(sala.getNome());
            throw new Error(e.getMessage());
        }
    }


    //Atualiza os dados de uma sala no banco de dados.
    @Override
    public void atualiza(Sala sala) throws FalhaConexaoException, SalaNaoEncontradaException {

        try {
            // Estabelecer conexao
            Connection conexao = Conexao.obtemConexao();

            // Faço a consulta
            PreparedStatement stmt = conexao.prepareStatement("UPDATE salas SET capacidade = ?, idCinema = ? WHERE nome = ?;");
            stmt.setInt(1, sala.getCapacidade());
            stmt.setInt(3, sala.getCinema().getId());
            stmt.setString(4, sala.getNome());

            // Verifico a quantidade de registros alterados
            int nLinhasAlteradas = stmt.executeUpdate();

            // Se não alterou nenhuma linha é porque não tem sala com esse nome
            if (nLinhasAlteradas == 0) throw new SalaNaoEncontradaException(sala.getNome());

        } catch (SQLException e) {
            throw new Error(e.getMessage());
        }
    }


    //Remove o sala do banco de dados.
    @Override
    public void remove(Sala sala) throws FalhaConexaoException, SalaNaoEncontradaException{

        try {
            // Estabelecer conexao
            Connection conexao = Conexao.obtemConexao();

            // Faço a consulta
            PreparedStatement stmt = conexao.prepareStatement("DELETE FROM salas WHERE nome = ?;");
            stmt.setString(1, sala.getNome());

            // Verifico a quantidade de registros alterados
            int nLinhasAlteradas = stmt.executeUpdate();

            // Se não alterou nenhuma linha é porque não tem sala com esse numero
            if (nLinhasAlteradas == 0) throw new SalaNaoEncontradaException(sala.getNome());

        } catch (SQLException e) {
            throw new Error(e.getMessage());
        }
    }


    //Obtem a lista de todos as salas criadas.
    @Override
    public List<Sala> obtemLista() throws FalhaConexaoException, CinemaNaoEncontradoException {

        CinemaDAO cinemaDAO = new CinemaDAO(false);
        try {
            // Estabelecer conexao
            Connection conexao = Conexao.obtemConexao();

            // Faço a consulta
            Statement stmt = conexao.createStatement();
            ResultSet resultado = stmt.executeQuery("SELECT * from salas ORDER BY nome;");

            // Crio a lista de salas.
            List<Sala> listaSalas = new ArrayList<>();

            while(resultado.next()) {
                // Obtenho os dados
                Sala salaTmp = new Sala(resultado.getString(NOME_POSICAO_TABELA),
                        resultado.getInt(CAPACIDADE_POSICAO_TABELA),
                        cinemaDAO.obtem(resultado.getInt(IDCINEMA_POSICAO_TABELA)));
                // Adiciono à lista de salas
                listaSalas.add(salaTmp);
            }

            // Retorna a lista de salas preenchida
            return listaSalas;

        } catch (SQLException | IdExistenteException e) {
            throw new Error(e.getMessage());
        }
    }

    //Obtem a lista de todos as salas de determinado cinema.
    public List<Sala> obtemListaSalasDeUmCinema(int idCinema) throws FalhaConexaoException, CinemaNaoEncontradoException, IdExistenteException {

        CinemaDAO cinemaDAO = new CinemaDAO(false);
        try {
            // Estabelecer conexao
            Connection conexao = Conexao.obtemConexao();

            // Faço a consulta
            PreparedStatement stmt = conexao.prepareStatement("SELECT * FROM salas WHERE idCinema = ? ORDER BY nome");
            stmt.setInt(1, idCinema);
            ResultSet resultado = stmt.executeQuery();

            // Crio a lista de salas.
            List<Sala> listaSalas = new ArrayList<>();

            while(resultado.next()) {
                // Obtenho os dados
                Sala salaTmp = new Sala(resultado.getString(NOME_POSICAO_TABELA),
                        resultado.getInt(CAPACIDADE_POSICAO_TABELA),
                        cinemaDAO.obtem(resultado.getInt(IDCINEMA_POSICAO_TABELA)));
                // Adiciono à lista de salas
                listaSalas.add(salaTmp);
            }

            // Retorna a lista de salas preenchida
            return listaSalas;

        } catch (SQLException e) {
            throw new Error(e.getMessage());
        }
    }
}