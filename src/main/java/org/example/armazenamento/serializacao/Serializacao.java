package org.example.armazenamento.serializacao;

import org.example.cinema.Cinema;
import org.example.filme.Filme;

import java.io.*;
import java.util.List;

public class Serializacao {

    public static void serializarFilmes(List<Filme> filmes) throws IOException {

        String nomeArquivo = "filmes.ser";

        FileOutputStream arquivoFilmes = new FileOutputStream(nomeArquivo);
        ObjectOutputStream serializador = new ObjectOutputStream(arquivoFilmes);

        serializador.writeObject(filmes);

        serializador.close();
        arquivoFilmes.close();
    }

    public static List<Filme> deserializarFilmes() throws IOException, ClassNotFoundException{

        String nomeArquivo = "filmes.ser";

        FileInputStream arquivoConta = new FileInputStream(nomeArquivo);
        ObjectInputStream desserializador = new ObjectInputStream(arquivoConta);
        List<Filme> filmesDesserializados = (List<Filme>) desserializador.readObject();

        desserializador.close();
        arquivoConta.close();

        return filmesDesserializados;
    }

    public static void serializarCinemas(List<Cinema> cinemas) throws IOException {

        String nomeArquivo = "cinemas.ser";

        FileOutputStream arquivoFilmes = new FileOutputStream(nomeArquivo);
        ObjectOutputStream serializador = new ObjectOutputStream(arquivoFilmes);

        serializador.writeObject(cinemas);

        serializador.close();
        arquivoFilmes.close();
    }

    public static List<Cinema> deserializarCinemas() throws IOException, ClassNotFoundException{

        String nomeArquivo = "cinemas.ser";

        FileInputStream arquivoConta = new FileInputStream(nomeArquivo);
        ObjectInputStream desserializador = new ObjectInputStream(arquivoConta);
        List<Cinema> cinemasDesserializados = (List<Cinema>) desserializador.readObject();

        desserializador.close();
        arquivoConta.close();

        return cinemasDesserializados;
    }
}
