package org.example.sessao.sessaoDAO;

import org.example.armazenamento.JDBC.IDAO;
import org.example.armazenamento.JDBC.conexao.*;
import org.example.cinema.cinemaDAO.CinemaExistenteException;
import org.example.cinema.cinemaDAO.CinemaNaoEncontradoException;
import org.example.exceptions.IdExistenteException;
import org.example.filme.filmeDAO.FilmeDAO;
import org.example.filme.filmeDAO.FilmeNaoEncontradoException;
import org.example.sala.salaDAO.SalaDAO;
import org.example.sala.salaDAO.SalaNaoEncontradaException;
import org.example.sessao.Sessao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SessaoDAO implements IDAO<Sessao, Integer> {

    //Constantes
    private static final int ID_POSICAO_TABELA = 1;
    private static final int IDSALA_POSICAO_TABELA = 2;
    private static final int IDFILME_POSICAO_TABELA = 3;
    private static final int DATAHORA_POSICAO_TABELA = 4;
    private static final int DISPONIBILIDADE_POSICAO_TABELA = 5;

    //Códigos de erro
    static final int DUPLICATE_KEY_ERROR_CODE = 1062;

    public SessaoDAO(boolean ehPraCriarTabela) throws FalhaConexaoException {

        if(ehPraCriarTabela) criaTabela();
    }

    //Cria a tabela de sessaos caso ela não exista.
    @Override
    public void criaTabela() throws FalhaConexaoException {

        try {
            // Estabelecer conexao
            Connection conexao = Conexao.obtemConexao();

            // Faço a consulta
            Statement stmt = conexao.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS sessoes (" +
                    "id INT PRIMARY KEY, " +
                    "idSala VARCHAR(255) NOT NULL, " +
                    "idFilme INT NOT NULL, " +
                    "dataHora DATETIME NOT NULL, " +
                    "disponibilidade INT NOT NULL, " +
                    "FOREIGN KEY (idFilme) REFERENCES filmes(id), " +
                    "FOREIGN KEY (idSala) REFERENCES salas(nome) ON DELETE CASCADE ON UPDATE CASCADE)");

        } catch (SQLException e) {
            throw new Error(e.getMessage());
        }
    }

    //Obtem uma sessao a partir do seu id.
    @Override
    public Sessao obtem(Integer id) throws FalhaConexaoException, CinemaNaoEncontradoException,
            FilmeNaoEncontradoException, SalaNaoEncontradaException, SessaoNaoEncontradaException, IdExistenteException {

        FilmeDAO filmeDAO = new FilmeDAO(false);
        SalaDAO salaDAO = new SalaDAO(false);
        ResultSet resultado = null;
        try {
            // Estabelecer conexao
            Connection conexao = Conexao.obtemConexao();

            // Faço a consulta
            PreparedStatement stmt = conexao.prepareStatement("SELECT * FROM sessoes WHERE id = ?");
            stmt.setInt(1, id);
            resultado = stmt.executeQuery();

            if (resultado.next()) {
                // Obtenho os dados
                return new Sessao(resultado.getInt(ID_POSICAO_TABELA),
                        salaDAO.obtem(resultado.getString(IDSALA_POSICAO_TABELA)),
                        filmeDAO.obtem(resultado.getInt(IDFILME_POSICAO_TABELA)),
                        resultado.getTimestamp(DATAHORA_POSICAO_TABELA).toLocalDateTime(),
                        resultado.getInt(DISPONIBILIDADE_POSICAO_TABELA)
                );
            }

            // Se chegou aqui é porque não tem sessao com esse id
            throw new SessaoNaoEncontradaException(id);
        } catch (SQLException e) {
            throw new Error(e.getMessage());
        }
    }


    //Insere uma nova sessao no banco de dados.
    @Override
    public void insere(Sessao sessao) throws FalhaConexaoException, SessaoExistenteException {

        try {
            // Estabelecer conexao
            Connection conexao = Conexao.obtemConexao();

            // Faço a consulta
            PreparedStatement stmt = conexao.prepareStatement("INSERT INTO sessoes VALUES(?,?,?,?,?);");
            stmt.setInt(1, sessao.getId());
            stmt.setString(2, sessao.getSala().getNome());
            stmt.setInt(3, sessao.getFilme().getId());
            stmt.setTimestamp(4, java.sql.Timestamp.valueOf(sessao.getDataHora()));
            stmt.setInt(5, sessao.getDisponibilidade());
            stmt.execute();

        } catch (SQLException e) {
            if (e.getErrorCode() == DUPLICATE_KEY_ERROR_CODE) throw new SessaoExistenteException(sessao.getId());
            throw new Error(e.getMessage());
        }
    }

    //Atualiza os dados de uma sessao no banco de dados.
    @Override
    public void atualiza(Sessao sessao) throws FalhaConexaoException, SessaoNaoEncontradaException {

        try {
            // Estabelecer conexao
            Connection conexao = Conexao.obtemConexao();

            // Faço a consulta
            PreparedStatement stmt = conexao.prepareStatement("UPDATE sessoes SET idSala = ?" +
                    ", idFilme = ?, dataHora = ?, capacidade = ? WHERE id = ?;");
            stmt.setString(1, sessao.getSala().getNome());
            stmt.setInt(2, sessao.getFilme().getId());
            stmt.setTimestamp(3, java.sql.Timestamp.valueOf(sessao.getDataHora()));
            stmt.setInt(4, sessao.getDisponibilidade());
            stmt.setInt(5, sessao.getId());

            // Verifico a quantidade de registros alterados
            int nLinhasAlteradas = stmt.executeUpdate();

            // Se não alterou nenhuma linha é porque não tem sessao com esse id
            if (nLinhasAlteradas == 0) throw new SessaoNaoEncontradaException(sessao.getId());

        } catch (SQLException e) {
            throw new Error(e.getMessage());
        }
    }


    //Remove o sessao do banco de dados.
    @Override
    public void remove(Sessao sessao) throws FalhaConexaoException, SessaoNaoEncontradaException{

        try {
            // Estabelecer conexao
            Connection conexao = Conexao.obtemConexao();

            // Faço a consulta
            PreparedStatement stmt = conexao.prepareStatement("DELETE FROM sessoes WHERE id = ?;");
            stmt.setInt(1, sessao.getId());

            // Verifico a quantidade de registros alterados
            int nLinhasAlteradas = stmt.executeUpdate();

            // Se não alterou nenhuma linha é porque não tem sessao com esse id
            if (nLinhasAlteradas == 0) throw new SessaoNaoEncontradaException(sessao.getId());

        } catch (SQLException e) {
            throw new Error(e.getMessage());
        }
    }


    //Obtem a lista de todos as sessaos criadas.
    @Override
    public List<Sessao> obtemLista() throws FalhaConexaoException, CinemaNaoEncontradoException, FilmeNaoEncontradoException,
            SalaNaoEncontradaException, IdExistenteException {

        FilmeDAO filmeDAO = new FilmeDAO(false);
        SalaDAO salaDAO = new SalaDAO(false);

        try {
            // Estabelecer conexao
            Connection conexao = Conexao.obtemConexao();

            // Faço a consulta
            Statement stmt = conexao.createStatement();
            ResultSet resultado = stmt.executeQuery("SELECT * from sessoes ORDER BY dataHora;");

            // Crio a lista de sessaos.
            List<Sessao> listaSessaos = new ArrayList<>();

            while(resultado.next()) {
                // Obtenho os dados
                Sessao sessaoTmp = new Sessao(resultado.getInt(ID_POSICAO_TABELA),
                        salaDAO.obtem(resultado.getString(IDSALA_POSICAO_TABELA)),
                        filmeDAO.obtem(resultado.getInt(IDFILME_POSICAO_TABELA)),
                        resultado.getTimestamp(DATAHORA_POSICAO_TABELA).toLocalDateTime(),
                        resultado.getInt(DISPONIBILIDADE_POSICAO_TABELA));

                // Adiciono à lista de sessaos
                listaSessaos.add(sessaoTmp);
            }

            // Retorna a lista de sessaos preenchida
            return listaSessaos;

        } catch (SQLException e) {
            throw new Error(e.getMessage());
        }
    }


    //Obtem a lista de todos as sessaos de determinada sala.
    public List<Sessao> obtemListaSessaosDeUmaSala(int idCinema) throws FalhaConexaoException, CinemaNaoEncontradoException,
            FilmeNaoEncontradoException, SalaNaoEncontradaException, IdExistenteException {

        FilmeDAO filmeDAO = new FilmeDAO(false);
        SalaDAO salaDAO = new SalaDAO(false);

        try {
            // Estabelecer conexao
            Connection conexao = Conexao.obtemConexao();

            // Faço a consulta
            PreparedStatement stmt = conexao.prepareStatement("SELECT * FROM sessoes WHERE idCinema = ? ORDER BY dataHora");
            stmt.setInt(1, idCinema);
            ResultSet resultado = stmt.executeQuery();

            // Crio a lista de sessaos.
            List<Sessao> listaSessaos = new ArrayList<>();

            while(resultado.next()) {
                // Obtenho os dados
                Sessao sessaoTmp = new Sessao(resultado.getInt(ID_POSICAO_TABELA),
                        salaDAO.obtem(resultado.getString(IDSALA_POSICAO_TABELA)),
                        filmeDAO.obtem(resultado.getInt(IDFILME_POSICAO_TABELA)),
                        resultado.getTimestamp(DATAHORA_POSICAO_TABELA).toLocalDateTime(),
                        resultado.getInt(DISPONIBILIDADE_POSICAO_TABELA));

                // Adiciono à lista de sessaos
                listaSessaos.add(sessaoTmp);
            }

            // Retorna a lista de sessaos preenchida
            return listaSessaos;

        } catch (SQLException e) {
            throw new Error(e.getMessage());
        }
    }
}


