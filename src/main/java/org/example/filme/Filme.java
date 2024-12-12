package org.example.filme;


import org.example.armazenamento.JDBC.conexao.FalhaConexaoException;
import org.example.exceptions.AvaliacaoInvalidaException;
import org.example.filme.filmeDAO.FilmeDAO;
import org.example.filme.filmeDAO.FilmeExistenteException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Filme implements Serializable {

    public static FilmeDAO filmeDAO;

    static {
        try {
            filmeDAO = new FilmeDAO(false);
        } catch (FalhaConexaoException e) {
            System.err.println(e.getMessage());
        }
    }

    /* Atributos */
    private final int id;

    private String nome;

    private long duracao_s;

    private List<Integer> avaliacoes;

    /* Construtores */

    private Filme(String nome, long duracao_s) {

        this.id = Filme.gerarIdUnico(nome, duracao_s);
        this.nome = nome;
        this.duracao_s = duracao_s;
        this.avaliacoes = new ArrayList<>();
    }

    /* Sobrecarga: para a classe DAO */
    public Filme(String nome, long duracao_s, int mediaAvaliacao) {

        this.id = Filme.gerarIdUnico(nome, duracao_s);
        this.nome = nome;
        this.duracao_s = duracao_s;
        this.avaliacoes = new ArrayList<>();
        this.avaliacoes.add((int) mediaAvaliacao);
    }

    public static Filme criarFilme(String nome, long duracao_s) throws FilmeExistenteException, FalhaConexaoException {

        filmeDAO = new FilmeDAO(true);

        Filme filme = new Filme(nome,duracao_s);
        filmeDAO.insere(filme);

        return filme;
    }

    /* Metodos supreendentes */

    public void adicionarAvaliacao(int avaliacao) throws AvaliacaoInvalidaException, FilmeExistenteException, FalhaConexaoException {

        if (avaliacao < 0 || avaliacao > 5) {

            throw new AvaliacaoInvalidaException(avaliacao);
        }
        avaliacoes.add(avaliacao);
        filmeDAO.atualiza(this);
    }

    public int calculaMediaAvaliacoes() {

        return (int) avaliacoes.stream().mapToInt(Integer::intValue).average().orElse(0.0);
    }

    /**
     * Define o id de um filme com base nos seus atributos, de forma
     * a nunca existir dois filmes com IDs iguais
     */
    public static int gerarIdUnico(String nome, long duracao_s) {

        return (int) ((nome.hashCode() ^ duracao_s) & Integer.MAX_VALUE);
    }

    /* Getters e Setters */

    public int getId() { return id; }

    public String getNome() { return nome; }

    public void setNome(String nome) { this.nome = nome; }

    public long getDuracao_s() { return duracao_s; }

    public void setDuracao_s(long duracao_s) { this.duracao_s = duracao_s; }

    public List<Integer> getAvaliacoes() { return avaliacoes; }

    public void setAvaliacoes(List<Integer> avaliacoes) { this.avaliacoes = avaliacoes; }
}
