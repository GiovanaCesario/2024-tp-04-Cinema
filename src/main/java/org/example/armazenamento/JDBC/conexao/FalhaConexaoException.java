package org.example.armazenamento.JDBC.conexao;

public class FalhaConexaoException extends Exception {

    public FalhaConexaoException(String mensagem) {
        super(mensagem);
    }
}