package br.com.alugueldecarros.domain.model;

import java.util.ArrayList;
import java.util.List;

public class Cliente extends Usuario {

    private String cpf;
    private String rg;
    private String endereco;
    private String profissao;
    private final List<Emprego> empregos = new ArrayList<>();

    public Cliente() {
    }

    public Cliente(String nome, String email, String senha, String cpf, String rg, String endereco, String profissao) {
        super(nome, email, senha);
        this.cpf = cpf;
        this.rg = rg;
        this.endereco = endereco;
        this.profissao = profissao;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getRg() {
        return rg;
    }

    public void setRg(String rg) {
        this.rg = rg;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getProfissao() {
        return profissao;
    }

    public void setProfissao(String profissao) {
        this.profissao = profissao;
    }

    public List<Emprego> getEmpregos() {
        return empregos;
    }
}
