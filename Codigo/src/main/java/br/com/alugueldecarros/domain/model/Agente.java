package br.com.alugueldecarros.domain.model;

public abstract class Agente extends Usuario {

    private final TipoAgente tipo;

    protected Agente(String nome, String email, String senha, TipoAgente tipo) {
        super(nome, email, senha);
        this.tipo = tipo;
    }

    public TipoAgente getTipo() {
        return tipo;
    }
}
