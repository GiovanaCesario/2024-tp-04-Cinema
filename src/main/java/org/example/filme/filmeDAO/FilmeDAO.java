package org.example.filme.filmeDAO;

import org.example.armazenamento.JDBC.IDAO;
import org.example.armazenamento.JDBC.conexao.Conexao;
import org.example.armazenamento.JDBC.conexao.FalhaConexaoException;
import org.example.armazenamento.serializacao.Serializacao;
import org.example.cinema.cinemaDAO.CinemaExistenteException;
import org.example.exceptions.IdExistenteException;
import org.example.filme.Filme;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class FilmeDAO implements IDAO<Filme, Integer>{

    //Constantes
    private static final int ID_POSICAO_TABELA = 1;
    private static final int NOME_POSICAO_TABELA = 2;
    private static final int DURACAO_POSICAO_TABELA = 3;
    private static final int MEDIAAVALIACAO_POSICAO_TABELA = 4;

    //Códigos de erro
    static final int DUPLICATE_KEY_ERROR_CODE = 1062;

    public FilmeDAO(boolean ehPraCriarTabela) throws FalhaConexaoException {

        if(ehPraCriarTabela) {
            criaTabela();
        }
    }

    //Cria a tabela de filmes caso ela não exista.
    @Override
    public void criaTabela() throws FalhaConexaoException {

        try {
            // Estabelecer conexao
            Connection conexao = Conexao.obtemConexao();

            // Faço a consulta
            Statement stmt = conexao.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS filmes (id int PRIMARY KEY," +
                    "nome VARCHAR(255) NOT NULL, " +
                    "duracao_s BIGINT NOT NULL, " +
                    "mediaAvaliacao INT NOT NULL)");
        } catch (SQLException e) {
            throw new Error(e.getMessage());
        }
    }

    //Obtem um filme a partir do seu id.
    @Override
    public Filme obtem(Integer id) throws FalhaConexaoException, FilmeNaoEncontradoException {

        try {
            // Estabelecer conexao
            Connection conexao = Conexao.obtemConexao();

            // Faço a consulta
            PreparedStatement stmt = conexao.prepareStatement("SELECT * FROM filmes WHERE id = ?");
            stmt.setInt(1, id);
            ResultSet resultado = stmt.executeQuery();

            // Obtenho os dados
            if (resultado.next()) {

                return new Filme(resultado.getString(NOME_POSICAO_TABELA),
                        resultado.getLong(DURACAO_POSICAO_TABELA),
                        resultado.getInt(MEDIAAVALIACAO_POSICAO_TABELA)
                );
            }

            // Se chegou aqui é porque não tem filme com esse id
            throw new FilmeNaoEncontradoException(id);
        } catch (SQLException e) {
            throw new Error(e.getMessage());
        }
    }

    //Insere um novo filme do banco de dados.
    @Override
    public void insere(Filme filme) throws FalhaConexaoException, FilmeExistenteException {

        try {
            // Estabelecer conexao
            Connection conexao = Conexao.obtemConexao();

            // Faço a consulta
            PreparedStatement stmt = conexao.prepareStatement("INSERT INTO filmes VALUES(?,?,?,?);");
            stmt.setInt(1, filme.getId());
            stmt.setString(2, filme.getNome());
            stmt.setLong(3, filme.getDuracao_s());
            stmt.setInt(4, filme.calculaMediaAvaliacoes());
            stmt.execute();

        } catch (SQLException e) {
            if (e.getErrorCode() == DUPLICATE_KEY_ERROR_CODE) throw new FilmeExistenteException(filme.getId());
            throw new Error(e.getMessage());
        }
    }

    //Atualiza os dados de um filme no banco de dados.
    @Override
    public void atualiza(Filme filme) throws FalhaConexaoException, FilmeExistenteException {

        try {
            // Estabelecer conexao
            Connection conexao = Conexao.obtemConexao();

            // Faço a consulta
            PreparedStatement stmt = conexao.prepareStatement("UPDATE filmes SET nome = ?, duracao_s = ?, mediaAvaliacao = ? WHERE id = ?;");
            stmt.setString(1, filme.getNome());
            stmt.setLong(2, filme.getDuracao_s());
            stmt.setInt(3, filme.calculaMediaAvaliacoes());
            stmt.setInt(4, filme.getId());

            // Verifico a quantidade de registros alterados
            int nLinhasAlteradas = stmt.executeUpdate();

            // Se não alterou nenhuma linha é porque não tem filme com esse Id
            if (nLinhasAlteradas == 0) throw new FilmeExistenteException(filme.getId());

        } catch (SQLException e) {
            throw new Error(e.getMessage());
        }
    }

    // Remove um filme do banco de dados.
    @Override
    public void remove(Filme filme) throws FalhaConexaoException, FilmeNaoEncontradoException {

        try {
            // Estabelecer conexao
            Connection conexao = Conexao.obtemConexao();

            // Faço a consulta
            PreparedStatement stmt = conexao.prepareStatement("DELETE FROM filmes WHERE id = ?;");
            stmt.setInt(1, filme.getId());

            // Verifico a quantidade de registros alterados
            int nLinhasAlteradas = stmt.executeUpdate();

            // Se não alterou nenhuma linha é porque não tem filme com esse cnpj
            if (nLinhasAlteradas == 0) throw new FilmeNaoEncontradoException(filme.getId());

        } catch (SQLException e) {
            throw new Error(e.getMessage());
        }
    }

    // Obtem a lista de todos os filmes.
    @Override
    public List<Filme> obtemLista() throws FalhaConexaoException {

        try {
            // Estabelecer conexao
            Connection conexao = Conexao.obtemConexao();

            // Faço a consulta
            Statement stmt = conexao.createStatement();
            ResultSet resultado = stmt.executeQuery("SELECT * from filmes ORDER BY nome;");

            // Crio a lista de filmes.
            List<Filme> listaFilmes = new ArrayList<>();

            // Obtenho os dados
            while (resultado.next()) {

                Filme filmeTmp = new Filme(resultado.getString(NOME_POSICAO_TABELA),
                        resultado.getLong(DURACAO_POSICAO_TABELA),
                        resultado.getInt(MEDIAAVALIACAO_POSICAO_TABELA));

                listaFilmes.add(filmeTmp);
            }

            // Retorna a lista de filmes preenchida
            return listaFilmes;

        } catch (SQLException e) {
            throw new Error(e.getMessage());
        }
    }
}

