package br.com.alugueldecarros.domain.model;

public class Empresa extends Agente {

    public Empresa(String nome, String email, String senha) {
        super(nome, email, senha, TipoAgente.EMPRESA);
    }
}
