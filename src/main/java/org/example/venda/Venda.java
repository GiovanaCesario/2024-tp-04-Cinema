package org.example.venda;

import org.example.armazenamento.JDBC.conexao.FalhaConexaoException;
import org.example.sessao.Sessao;
import org.example.cinema.Cinema;
import org.example.venda.vendaDAO.VendaDAO;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class Venda implements Serializable {

    public static VendaDAO vendaDAO;

    static {
        try {
            vendaDAO = new VendaDAO(false);
        } catch (FalhaConexaoException e) {
            System.err.println(e.getMessage());
        }
    }

    /* Atributos surpreendentes */
    private final int id;

    private LocalDate dataVenda;

    private Sessao sessao;

    private double valor;

    private Cinema cinema;

    /* Construtores */

    public Venda(int id, double valor, Sessao sessao, Cinema cinema) {

        this.id = id;
        this.dataVenda = LocalDate.now();
        this.valor = valor;
        this.sessao = sessao;
        this.cinema = cinema;
    }

    /* Sobrecarga: para a classe DAO */
    public Venda(int id, double valor, Sessao sessao, Cinema cinema, Date data){

        this.id = id;
        this.dataVenda = data.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        this.valor = valor;
        this.sessao = sessao;
        this.cinema = cinema;
    }

    /* Metodos surpreendentes */

    /**
     * Define o id de uma venda com base no cinema e seu indice nesse cinema, de forma
     * a nunca existir duas vendas com ids iguais, mesmo que em cinemas diferentes
     */
    public static int gerarIdUnico(int iVenda, Cinema cinema) {

        return (iVenda ^ cinema.hashCode()) & Integer.MAX_VALUE;
    }

    /* Metodos sobrescritos */

    @Override
    public String toString() {
        return "Data: " + this.dataVenda + "\t\tValor: " + this.valor;
    }

    /* Getters e Setters */

    public int getId() { return id; }

    public double getValor() { return valor; }

    public void setValor(double valor) { this.valor = valor; }

    public Sessao getSessao() { return sessao; }

    public void setSessao(Sessao sessao) { this.sessao = sessao; }

    public LocalDate getDataVenda() { return dataVenda; }

    public void setDataVenda(LocalDate dataVenda) { this.dataVenda = dataVenda; }

    public Cinema getCinema() { return cinema; }

    public void setCinema(Cinema cinema) { this.cinema = cinema; }
}
