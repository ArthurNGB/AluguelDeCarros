package br.com.alugueldecarros.domain.model;

import java.math.BigDecimal;

public class Automovel extends BaseEntity {

    private String matricula;
    private int ano;
    private String marca;
    private String modelo;
    private String placa;
    private Long proprietarioId;
    private BigDecimal valorDiaria;
    private StatusAutomovel status = StatusAutomovel.DISPONIVEL;

    public Automovel() {
    }

    public Automovel(String matricula, int ano, String marca, String modelo, String placa, Long proprietarioId, BigDecimal valorDiaria) {
        this.matricula = matricula;
        this.ano = ano;
        this.marca = marca;
        this.modelo = modelo;
        this.placa = placa;
        this.proprietarioId = proprietarioId;
        this.valorDiaria = valorDiaria;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public Long getProprietarioId() {
        return proprietarioId;
    }

    public void setProprietarioId(Long proprietarioId) {
        this.proprietarioId = proprietarioId;
    }

    public BigDecimal getValorDiaria() {
        return valorDiaria;
    }

    public void setValorDiaria(BigDecimal valorDiaria) {
        this.valorDiaria = valorDiaria;
    }

    public StatusAutomovel getStatus() {
        return status;
    }

    public void setStatus(StatusAutomovel status) {
        this.status = status;
    }

    public boolean isDisponivel() {
        return status == StatusAutomovel.DISPONIVEL;
    }
}
