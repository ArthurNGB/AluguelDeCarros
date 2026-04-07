package br.com.alugueldecarros.domain.model;

import java.util.Objects;

public abstract class Usuario extends BaseEntity {

    private String nome;
    private String email;
    private String senha;

    protected Usuario() {
    }

    protected Usuario(String nome, String email, String senha) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

    public boolean login(String email, String senha) {
        return Objects.equals(this.email, email) && Objects.equals(this.senha, senha);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
