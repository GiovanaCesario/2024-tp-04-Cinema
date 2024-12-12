package org.example.sala;

import org.example.armazenamento.JDBC.conexao.FalhaConexaoException;
import org.example.exceptions.NunhumaSessaoEncontradaException;
import org.example.sala.salaDAO.SalaDAO;
import org.example.sessao.Sessao;
import org.example.cinema.Cinema;
import org.example.exceptions.SalaOcupadaException;
import org.example.filme.Filme;
import org.example.sessao.sessaoDAO.SessaoDAO;
import org.example.sessao.sessaoDAO.SessaoExistenteException;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class Sala implements Serializable {

    public static SalaDAO salaDAO;

    static {
        try {
            salaDAO = new SalaDAO(false);
        } catch (FalhaConexaoException e) {
            System.err.println(e.getMessage());
        }
    }

    /* Atributos */
    private final String nome;

    private int capacidade;

    private List<Sessao> sessoes;

    private Cinema cinema;

    /* Construtor */
    public Sala(String nome, int capacidade, Cinema cinema) {

        this.nome = nome;
        this.capacidade = capacidade;
        this.sessoes = new ArrayList<>();
        this.cinema = cinema;
    }

    /* Metodos */

    public void criarSessao(Filme filme, LocalDateTime dataHora) throws SalaOcupadaException, FalhaConexaoException, SessaoExistenteException {

        LocalDateTime dataHoraFim = dataHora.plusSeconds(filme.getDuracao_s());

        /* Garante que o horario da nova sessao nao conflita com outras, alem de respeitar um
           intevalo de 30 min entre as sessoes */
        for(Sessao sessaoExistente : sessoes) {

            if (dataHora.isBefore(sessaoExistente.getDataHorafim().plusMinutes(30))
                && dataHoraFim.isAfter(sessaoExistente.getDataHora().minusMinutes(30))) {
                throw new SalaOcupadaException(this.nome);
            }
        }

        Sessao sessao = new Sessao(Sessao.gerarIdUnico(sessoes.size() + 1, this), this, filme, dataHora, dataHoraFim);
        sessoes.add(sessao);

        Sessao.sessaoDAO = new SessaoDAO(true);
        Sessao.sessaoDAO.insere(sessao);
    }

    /**
     * Sobrecarga:
     * Cria uma sessao 30 minutos depois da ultima sessao programada
     */
    public void criarSessao(Filme filme) throws FalhaConexaoException, SessaoExistenteException, NunhumaSessaoEncontradaException {

        Optional<Sessao> ultimaSessao = sessoes.stream().max(Comparator.comparing(Sessao::getDataHora));

        if(ultimaSessao.isPresent()) {

            LocalDateTime dataHora = ultimaSessao.get().getDataHora().plusMinutes(30);

            Sessao sessao = new Sessao(Sessao.gerarIdUnico(sessoes.size() + 1, this),
                    this, filme, dataHora, dataHora.plusSeconds(filme.getDuracao_s()));
            sessoes.add(sessao);

            Sessao.sessaoDAO = new SessaoDAO(true);
            Sessao.sessaoDAO.insere(sessao);

        } else {
            throw new NunhumaSessaoEncontradaException();
        }
    }

    /**
     * Sobrecarga:
     * Cria uma sessao todos os dias da semana no mesmo horario
     */
    public void criarSessao(Filme filme, LocalTime horario) throws SalaOcupadaException, SessaoExistenteException, FalhaConexaoException {

        LocalDate ultimoDia = LocalDate.now().plusDays(6);

        /* Passa por todos os dias da semana e se possivel cria uma sessao */

        for(LocalDate d = LocalDate.now(); d.isBefore(ultimoDia) || d.isEqual(ultimoDia); d = d.plusDays(1)) {

            LocalDateTime dataHora = LocalDateTime.of(d, horario);
            LocalDateTime dataHoraFim = dataHora.plusSeconds(filme.getDuracao_s());

            for(Sessao sessaoExistente : sessoes) {

                if (dataHora.isBefore(sessaoExistente.getDataHorafim().plusMinutes(30))
                        && dataHoraFim.isAfter(sessaoExistente.getDataHora().minusMinutes(30))) {
                    throw new SalaOcupadaException(this.nome);
                }
            }

            Sessao sessao = new Sessao(Sessao.gerarIdUnico(sessoes.size() + 1, this),
                    this, filme, dataHora, dataHoraFim);
            sessoes.add(sessao);

            Sessao.sessaoDAO = new SessaoDAO(true);
            Sessao.sessaoDAO.insere(sessao);
        }
    }

    public void listarSessoes() {

        sessoes.sort(Comparator.comparing(Sessao::getDataHora));
        sessoes.forEach(System.out::println);
    }


    /* Metodos sobrescritos */

    @Override
    public String toString() {
        return "Nome: " + this.nome + "\t\tCapacidade: " + this.capacidade;
    }

    /* Getters e Setters */
    public String getNome() { return nome; }

    public int getCapacidade() { return capacidade; }

    public void setCapacidade(int capacidade) { this.capacidade = capacidade; }

    public List<Sessao> getSessoes() { return sessoes; }

    public void setSessoes(List<Sessao> sessoes) { this.sessoes = sessoes; }

    public Cinema getCinema() { return cinema; }

    public void setCinema(Cinema cinema) { this.cinema = cinema; }
}
