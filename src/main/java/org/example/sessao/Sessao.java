package org.example.sessao;

import org.example.armazenamento.JDBC.conexao.FalhaConexaoException;
import org.example.armazenamento.serializacao.Serializacao;
import org.example.filme.Filme;
import org.example.sala.Sala;
import org.example.sessao.sessaoDAO.SessaoDAO;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Sessao implements Serializable {

    public static SessaoDAO sessaoDAO;

    static {
        try {
            sessaoDAO = new SessaoDAO(false);
        } catch (FalhaConexaoException e) {
            System.err.println(e.getMessage());
        }
    }

    /* Atributos */
    private final int id;

    private Sala sala;

    private Filme filme;

    private LocalDateTime dataHora;

    private LocalDateTime dataHorafim;

    private int disponibilidade;

    /* Construtores */

    public Sessao(int id, Sala sala, Filme filme, LocalDateTime dataHora, LocalDateTime dataHoraFim) {

        this.id = id;
        this.sala = sala;
        this.filme = filme;
        this.dataHora = dataHora;
        this.dataHorafim = dataHoraFim;
        this.disponibilidade = sala.getCapacidade();
    }

    /* Sobrecarga: para a classe DAO */
    public Sessao(int id, Sala sala, Filme filme, LocalDateTime dataHora, int disponibilidade) {

        this.id = id;
        this.sala = sala;
        this.filme = filme;
        this.dataHora = dataHora;
        this.dataHorafim = dataHora.plusSeconds(filme.getDuracao_s());
        this.disponibilidade = disponibilidade;
    }

    /* Metodos surpreendentes */

    /**
     *  Define o id de uma sessao com base na sala e seu indice nessa sala, de forma
     * a nunca existir duas sessoes com ids iguais, mesmo que em salas diferentes
     */
    public static int gerarIdUnico(int iSessao, Sala sala) {

        return (iSessao ^ sala.hashCode()) & Integer.MAX_VALUE;
    }

    /* Metodos sobreescritos */

    @Override
    public String toString() {
        return "ID: " + this.id + "\t\tSala: " + this.sala.getNome() + "\t\tFilme: "
                + this.filme.getNome() + "\t\tData e Hora: " +this.dataHora;
    }

    /* Getters e Settes */

    public int getId() { return id; }

    public Sala getSala() { return sala; }

    public void setSala(Sala sala) { this.sala = sala; }

    public Filme getFilme() { return filme; }

    public void setFilme(Filme filme) { this.filme = filme; }

    public LocalDateTime getDataHora() { return dataHora; }

    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public LocalDateTime getDataHorafim() { return dataHorafim; }

    public void setDataHorafim(LocalDateTime dataHorafim) { this.dataHorafim = dataHorafim; }

    public int getDisponibilidade() { return disponibilidade; }

    public void setDisponibilidade(int disponibilidade) { this.disponibilidade = disponibilidade; }
}
