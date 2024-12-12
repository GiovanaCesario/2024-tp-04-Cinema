package org.example.cinema;

import org.example.armazenamento.JDBC.conexao.FalhaConexaoException;
import org.example.armazenamento.serializacao.Serializacao;
import org.example.cinema.cinemaDAO.CinemaDAO;
import org.example.cinema.cinemaDAO.CinemaExistenteException;
import org.example.exceptions.NomeDuplicadoException;
import org.example.exceptions.SessaoLotadaException;
import org.example.sala.Sala;
import org.example.sala.salaDAO.SalaDAO;
import org.example.sala.salaDAO.SalaExistenteException;
import org.example.sessao.Sessao;
import org.example.venda.Venda;
import org.example.venda.vendaDAO.VendaDAO;
import org.example.venda.vendaDAO.VendaExistenteException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Cinema implements Serializable {

    public static List<Cinema> cinemas = new ArrayList<>();

    public static CinemaDAO cinemaDAO;

    static {
        try {
            cinemaDAO = new CinemaDAO(false);
        } catch (FalhaConexaoException e) {
            System.err.println(e.getMessage());
        }
    }

    /* Atributos */
    private final int id;

    private String nome;

    private String local;

    private double valorVenda = 0;  //Diferencia um cinema dos demais

    private List<Sala> salas;

    private List<Venda> vendas;

    /* Construtor */
    public Cinema(int id, String nome, String local) throws CinemaExistenteException, FalhaConexaoException {

        this.id = id;
        this.nome = nome;
        this.local = local;
        this.salas = new ArrayList<>();
        this.vendas = new ArrayList<>();
    }

    /* Metodos */

    public void criarSala(String nome, int capacidade) throws NomeDuplicadoException, FalhaConexaoException, SalaExistenteException {

        /* Verifica se ja existe uma sala como o nome fornecido */
        if(salas.stream().anyMatch(a -> a.getNome().equalsIgnoreCase(nome))) {

            throw new NomeDuplicadoException(nome, this.nome);
        }

        Sala sala = new Sala(nome, capacidade, this);

        salas.add(sala);

        Sala.salaDAO = new SalaDAO(true);
        Sala.salaDAO.insere(sala);
    }

    public void listarSalas() {

        if(salas.isEmpty()) {
            System.out.println("Não há salas no " + this.nome);
            return;
        }

        salas.forEach(System.out::println);
    }

    public void listarCinemas() {

        if(cinemas.isEmpty()) {
            System.out.println("Nao há cinemas");
            return;
        }

        cinemas.forEach(System.out::println);
    }

    public void vender(Sessao sessao) throws SessaoLotadaException, FalhaConexaoException, VendaExistenteException {

        if(sessao.getDisponibilidade() <= 0) {
            throw new SessaoLotadaException(sessao.getId());
        }

        Venda venda = new Venda(Venda.gerarIdUnico(vendas.size() + 1, this), this.valorVenda, sessao, this);

        vendas.add(venda);
        sessao.setDisponibilidade(sessao.getDisponibilidade() - 1);

        Venda.vendaDAO = new VendaDAO(true);
        Venda.vendaDAO.insere(venda);
    }

    public void listaExtradoVendas() {

        vendas.forEach(System.out::println);
        System.out.println("Receita : " + vendas.stream().mapToDouble(Venda::getValor).sum());
    }

    /* Metodos sobrescritos */

    @Override
    public String toString() {
        return "ID: " + this.id + "\t\tNome: " + this.nome + "\t\tLocal: " + this.local;
    }

    /* Getters e Setters */

    public int getId() { return id; }

    public String getNome() { return nome; }

    public void setNome(String nome) { this.nome = nome;  }

    public String getLocal() { return local; }

    public void setLocal(String local) { this.local = local; }

    public double getValorVenda() { return valorVenda; }

    public void setValorVenda(double valorVenda) { this.valorVenda = valorVenda; }

    public List<Sala> getSalas() { return salas; }

    public void setSalas(List<Sala> salas) { this.salas = salas; }

    public List<Venda> getVendas() { return vendas; }

    public void setVendas(List<Venda> vendas) { this.vendas = vendas; }

}
