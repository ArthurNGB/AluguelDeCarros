package br.com.alugueldecarros.domain.model;

public class Banco extends Agente {

    public Banco(String nome, String email, String senha) {
        super(nome, email, senha, TipoAgente.BANCO);
    }
}
