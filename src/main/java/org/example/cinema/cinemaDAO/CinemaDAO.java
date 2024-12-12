package org.example.cinema.cinemaDAO;

import org.example.armazenamento.JDBC.IDAO;
import org.example.armazenamento.JDBC.conexao.*;
import org.example.cinema.Cinema;
import org.example.cinema.Cinemark;
import org.example.cinema.Cinepolis;
import org.example.exceptions.IdExistenteException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CinemaDAO implements IDAO<Cinema, Integer> {

    //Constantes
    private static final int ID_POSICAO_TABELA = 1;
    private static final int NOME_POSICAO_TABELA = 2;
    private static final int LOCAL_POSICAO_TABELA = 3;
    private static final int VALORVENDA_POSICAO_TABELA = 4;


    //Códigos de erro
    static final int DUPLICATE_KEY_ERROR_CODE = 1062;

    //Construtor
    public CinemaDAO(boolean ehPraCriarTabela) throws FalhaConexaoException {

        if(ehPraCriarTabela) {
            criaTabela();
        }
    }

    //Cria a tabela de cinemas caso ela não exista.
    @Override
    public void criaTabela() throws FalhaConexaoException {

        try {
            // Estabelecer conexao
            Connection conexao = Conexao.obtemConexao();

            // Faço a consulta
            Statement stmt = conexao.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS cinemas (" +
                    "id int PRIMARY KEY," +
                    "nome VARCHAR(255) NOT NULL, " +
                    "local VARCHAR(255) NOT NULL, " +
                    "valorVenda DECIMAL NOT NULL)");
        } catch (SQLException e) {
            throw new Error(e.getMessage());
        }
    }

    //Obtem um cinema a partir do seu id.
    @Override
    public Cinema obtem(Integer id) throws FalhaConexaoException, CinemaNaoEncontradoException, IdExistenteException {

        try {
            // Estabelecer conexao
            Connection conexao = Conexao.obtemConexao();

            // Faço a consulta
            PreparedStatement stmt = conexao.prepareStatement("SELECT * FROM cinemas WHERE id = ?");
            stmt.setInt(1, id);
            ResultSet resultado = stmt.executeQuery();

            // Obtenho os dados
            if (resultado.next()) {

                if (resultado.getString(NOME_POSICAO_TABELA).equals("Cinemark")) {

                    return Cinemark.getInstancia(resultado.getInt(ID_POSICAO_TABELA),
                            resultado.getString(LOCAL_POSICAO_TABELA),
                            false
                    );
                } else if(resultado.getString(NOME_POSICAO_TABELA).equals("Cinepolis")) {

                    return Cinepolis.getInstancia(resultado.getInt(ID_POSICAO_TABELA),
                            resultado.getString(LOCAL_POSICAO_TABELA),
                            false

                    );
                }
            }

            // Se chegou aqui é porque não tem cinema com esse id
            throw new CinemaNaoEncontradoException(id);
        } catch (SQLException | CinemaExistenteException e) {
            throw new Error(e.getMessage());
        }
    }

    //Insere um novo cinema do banco de dados.
    @Override
    public void insere(Cinema cinema) throws FalhaConexaoException, CinemaExistenteException {

        try {
            // Estabelecer conexao
            Connection conexao = Conexao.obtemConexao();

            // Faço a consulta
            PreparedStatement stmt = conexao.prepareStatement("INSERT INTO cinemas VALUES(?,?,?,?);");
            stmt.setInt(1, cinema.getId());
            stmt.setString(2, cinema.getNome());
            stmt.setString(3, cinema.getLocal());
            stmt.setDouble(4, cinema.getValorVenda());
            stmt.execute();

        } catch (SQLException e) {
            if (e.getErrorCode() == DUPLICATE_KEY_ERROR_CODE) throw new CinemaExistenteException(cinema.getId());
            throw new Error(e.getMessage());
        }
    }

    //Atualiza os dados de um cinema no banco de dados.
    @Override
    public void atualiza(Cinema cinema) throws FalhaConexaoException, CinemaExistenteException {

        try {
            // Estabelecer conexao
            Connection conexao = Conexao.obtemConexao();

            // Faço a consulta
            PreparedStatement stmt = conexao.prepareStatement("UPDATE cinemas SET nome = ?, local = ?, valorVenda = ? WHERE id = ?;");
            stmt.setString(1, cinema.getNome());
            stmt.setString(2, cinema.getLocal());
            stmt.setDouble(3, cinema.getValorVenda());
            stmt.setInt(4, cinema.getId());

            // Verifico a quantidade de registros alterados
            int nLinhasAlteradas = stmt.executeUpdate();

            // Se não alterou nenhuma linha é porque não tem cinema com esse Id
            if (nLinhasAlteradas == 0) throw new CinemaExistenteException(cinema.getId());

        } catch (SQLException e) {
            throw new Error(e.getMessage());
        }
    }

    // Remove um cinema do banco de dados.
    @Override
    public void remove(Cinema cinema) throws FalhaConexaoException, CinemaNaoEncontradoException {

        try {
            // Estabelecer conexao
            Connection conexao = Conexao.obtemConexao();

            // Faço a consulta
            PreparedStatement stmt = conexao.prepareStatement("DELETE FROM cinemas WHERE id = ?;");
            stmt.setInt(1, cinema.getId());

            // Verifico a quantidade de registros alterados
            int nLinhasAlteradas = stmt.executeUpdate();

            // Se não alterou nenhuma linha é porque não tem cinema com esse cnpj
            if (nLinhasAlteradas == 0) throw new CinemaNaoEncontradoException(cinema.getId());

        } catch (SQLException e) {
            throw new Error(e.getMessage());
        }
    }

    // Obtem a lista de todos os cinemas.
    @Override
    public List<Cinema> obtemLista() throws FalhaConexaoException, CinemaExistenteException, IdExistenteException {

        try {
            // Estabelecer conexao
            Connection conexao = Conexao.obtemConexao();

            // Faço a consulta
            Statement stmt = conexao.createStatement();
            ResultSet resultado = stmt.executeQuery("SELECT * from cinemas ORDER BY nome;");

            // Crio a lista de cinemas.
            List<Cinema> listaCinemas = new ArrayList<>();

            // Obtenho os dados
            while (resultado.next()) {

                Cinema cinemaTmp = null;

                if (resultado.getString(NOME_POSICAO_TABELA).equals("Cinemark")) {

                    cinemaTmp = Cinemark.getInstancia(resultado.getInt(ID_POSICAO_TABELA),
                            resultado.getString(LOCAL_POSICAO_TABELA),
                            false
                    );
                } else if(resultado.getString(NOME_POSICAO_TABELA).equals("Cinepolis")) {

                    cinemaTmp = Cinepolis.getInstancia(resultado.getInt(ID_POSICAO_TABELA),
                            resultado.getString(LOCAL_POSICAO_TABELA),
                            false
                    );
                }

                listaCinemas.add(cinemaTmp);
            }

            // Retorna a lista de cinemas preenchida
            return listaCinemas;

        } catch (SQLException e) {
            throw new Error(e.getMessage());
        }
    }
}

