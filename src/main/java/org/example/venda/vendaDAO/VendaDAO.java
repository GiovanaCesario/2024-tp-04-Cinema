package org.example.venda.vendaDAO;

import org.example.armazenamento.JDBC.IDAO;
import org.example.armazenamento.JDBC.conexao.*;
import org.example.cinema.cinemaDAO.CinemaDAO;
import org.example.cinema.cinemaDAO.CinemaNaoEncontradoException;
import org.example.exceptions.IdExistenteException;
import org.example.filme.filmeDAO.FilmeNaoEncontradoException;
import org.example.sala.salaDAO.SalaNaoEncontradaException;
import org.example.sessao.sessaoDAO.SessaoDAO;
import org.example.sessao.sessaoDAO.SessaoNaoEncontradaException;
import org.example.venda.Venda;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class VendaDAO implements IDAO<Venda, Integer> {

    //Constantes
    private static final int ID_POSICAO_TABELA = 1;
    private static final int DATA_POSICAO_TABELA = 2;
    private static final int VALOR_POSICAO_TABELA = 3;
    private static final int IDSESSAO_POSICAO_TABELA = 4;
    private static final int IDCINEMA_POSICAO_TABELA = 5;

    //Códigos de erro
    static final int DUPLICATE_KEY_ERROR_CODE = 1062;

    public VendaDAO(boolean ehPraCriarTabela) throws FalhaConexaoException {

        if(ehPraCriarTabela) {
            criaTabela();
        }
    }

    //Cria a tabela de vendas caso ela não exista.
    @Override
    public void criaTabela() throws FalhaConexaoException {

        try {
            // Estabelecer conexao
            Connection conexao = Conexao.obtemConexao();

            // Faço a consulta
            Statement stmt = conexao.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS vendas (" +
                    "id INT PRIMARY KEY, " +
                    "data DATE NOT NULL, " +
                    "valor DECIMAL NOT NULL, " +
                    "idSessao INT NOT NULL, " +
                    "idCinema INT NOT NULL, " +
                    "FOREIGN KEY (idSessao) REFERENCES sessoes(id), " +
                    "FOREIGN KEY (idCinema) REFERENCES cinemas(id) ON DELETE CASCADE ON UPDATE CASCADE)");

        } catch (SQLException e) {
            throw new Error(e.getMessage());
        }
    }

    //Obtem uma venda a partir do seu nome.
    @Override
    public Venda obtem(Integer id) throws FalhaConexaoException, CinemaNaoEncontradoException, VendaNaoEncontradaException,
            SalaNaoEncontradaException, FilmeNaoEncontradoException, SessaoNaoEncontradaException {

        CinemaDAO cinemaDAO = new CinemaDAO(false);
        SessaoDAO sessaoDAO = new SessaoDAO(false);
        ResultSet resultado = null;
        try {
            // Estabelecer conexao
            Connection conexao = Conexao.obtemConexao();

            // Faço a consulta
            PreparedStatement stmt = conexao.prepareStatement("SELECT * FROM vendas WHERE id = ?");
            stmt.setInt(1, id);
            resultado = stmt.executeQuery();

            if (resultado.next()) {
                // Obtenho os dados
                return new Venda(resultado.getInt(ID_POSICAO_TABELA),
                        resultado.getDouble(VALOR_POSICAO_TABELA),
                        sessaoDAO.obtem(resultado.getInt(IDSESSAO_POSICAO_TABELA)),
                        cinemaDAO.obtem(resultado.getInt(IDCINEMA_POSICAO_TABELA)),
                        resultado.getTimestamp(DATA_POSICAO_TABELA)
                );
            }

            // Se chegou aqui é porque não tem venda com esse nome
            throw new VendaNaoEncontradaException(id);
        } catch (SQLException | IdExistenteException e) {
            throw new Error(e.getMessage());
        }
    }


    //Insere uma nova venda no banco de dados.
    @Override
    public void insere(Venda venda) throws FalhaConexaoException, VendaExistenteException {

        try {
            // Estabelecer conexao
            Connection conexao = Conexao.obtemConexao();

            // Faço a consulta
            PreparedStatement stmt = conexao.prepareStatement("INSERT INTO vendas VALUES(?,?,?,?,?);");
            stmt.setInt(1, venda.getId());
            stmt.setDate(2, java.sql.Date.valueOf(venda.getDataVenda()));
            stmt.setDouble(3, venda.getValor());
            stmt.setInt(4, venda.getSessao().getId());
            stmt.setInt(5, venda.getCinema().getId());
            stmt.execute();

        } catch (SQLException e) {
            if (e.getErrorCode() == DUPLICATE_KEY_ERROR_CODE) throw new VendaExistenteException(venda.getId());
            throw new Error(e.getMessage());
        }
    }

    //Atualiza os dados de uma venda no banco de dados.
    @Override
    public void atualiza(Venda venda) throws FalhaConexaoException, VendaNaoEncontradaException {

        try {
            // Estabelecer conexao
            Connection conexao = Conexao.obtemConexao();

            // Faço a consulta
            PreparedStatement stmt = conexao.prepareStatement("UPDATE vendas SET data = ?" +
                    ", valor = ?, idSessao = ?, idCinema = ? WHERE id = ?;");
            stmt.setDate(2, java.sql.Date.valueOf(venda.getDataVenda()));
            stmt.setDouble(3, venda.getValor());
            stmt.setInt(4, venda.getSessao().getId());
            stmt.setInt(5, venda.getCinema().getId());
            stmt.setInt(1, venda.getId());

            // Verifico a quantidade de registros alterados
            int nLinhasAlteradas = stmt.executeUpdate();

            // Se não alterou nenhuma linha é porque não tem venda com esse id
            if (nLinhasAlteradas == 0) throw new VendaNaoEncontradaException(venda.getId());

        } catch (SQLException e) {
            throw new Error(e.getMessage());
        }
    }


    //Remove o venda do banco de dados.
    @Override
    public void remove(Venda venda) throws FalhaConexaoException, VendaNaoEncontradaException{

        try {
            // Estabelecer conexao
            Connection conexao = Conexao.obtemConexao();

            // Faço a consulta
            PreparedStatement stmt = conexao.prepareStatement("DELETE FROM vendas WHERE id = ?;");
            stmt.setInt(1, venda.getId());

            // Verifico a quantidade de registros alterados
            int nLinhasAlteradas = stmt.executeUpdate();

            // Se não alterou nenhuma linha é porque não tem venda com esse id
            if (nLinhasAlteradas == 0) throw new VendaNaoEncontradaException(venda.getId());

        } catch (SQLException e) {
            throw new Error(e.getMessage());
        }
    }


    //Obtem a lista de todos as vendas criadas.
    @Override
    public List<Venda> obtemLista() throws FalhaConexaoException, CinemaNaoEncontradoException, SalaNaoEncontradaException,
            FilmeNaoEncontradoException, SessaoNaoEncontradaException, IdExistenteException {

        SessaoDAO sessaoDAO = new SessaoDAO(false);
        CinemaDAO cinemaDAO = new CinemaDAO(false);
        try {
            // Estabelecer conexao
            Connection conexao = Conexao.obtemConexao();

            // Faço a consulta
            Statement stmt = conexao.createStatement();
            ResultSet resultado = stmt.executeQuery("SELECT * from vendas ORDER BY data;");

            // Crio a lista de vendas.
            List<Venda> listaVendas = new ArrayList<>();

            while(resultado.next()) {
                // Obtenho os dados
                Venda vendaTmp = new Venda(resultado.getInt(ID_POSICAO_TABELA),
                        resultado.getDouble(VALOR_POSICAO_TABELA),
                        sessaoDAO.obtem(resultado.getInt(IDSESSAO_POSICAO_TABELA)),
                        cinemaDAO.obtem(resultado.getInt(IDCINEMA_POSICAO_TABELA)),
                        resultado.getTimestamp(DATA_POSICAO_TABELA));

                // Adiciono à lista de vendas
                listaVendas.add(vendaTmp);
            }

            // Retorna a lista de vendas preenchida
            return listaVendas;

        } catch (SQLException e) {
            throw new Error(e.getMessage());
        }
    }


    //Obtem a lista de todos as vendas de determinado cinema.
    public List<Venda> obtemListaVendasDeUmCinema(int idCinema) throws FalhaConexaoException, CinemaNaoEncontradoException,
            SalaNaoEncontradaException, FilmeNaoEncontradoException, SessaoNaoEncontradaException, IdExistenteException {

        SessaoDAO sessaoDAO = new SessaoDAO(false);
        CinemaDAO cinemaDAO = new CinemaDAO(false);
        try {
            // Estabelecer conexao
            Connection conexao = Conexao.obtemConexao();

            // Faço a consulta
            PreparedStatement stmt = conexao.prepareStatement("SELECT * FROM vendas WHERE idCinema = ? ORDER BY data");
            stmt.setInt(1, idCinema);
            ResultSet resultado = stmt.executeQuery();

            // Crio a lista de vendas.
            List<Venda> listaVendas = new ArrayList<>();

            while(resultado.next()) {
                // Obtenho os dados
                Venda vendaTmp = new Venda(resultado.getInt(ID_POSICAO_TABELA),
                        resultado.getDouble(VALOR_POSICAO_TABELA),
                        sessaoDAO.obtem(resultado.getInt(IDSESSAO_POSICAO_TABELA)),
                        cinemaDAO.obtem(resultado.getInt(IDCINEMA_POSICAO_TABELA)),
                        resultado.getTimestamp(DATA_POSICAO_TABELA));

                // Adiciono à lista de vendas
                listaVendas.add(vendaTmp);
            }

            // Retorna a lista de vendas preenchida
            return listaVendas;

        } catch (SQLException e) {
            throw new Error(e.getMessage());
        }
    }
}

