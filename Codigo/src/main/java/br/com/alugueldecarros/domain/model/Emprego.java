package br.com.alugueldecarros.domain.model;

import java.math.BigDecimal;

public class Emprego {

    private Long entidadeEmpregadoraId;
    private String nomeEntidadeEmpregadora;
    private BigDecimal rendimento;

    public Emprego() {
    }

    public Emprego(Long entidadeEmpregadoraId, String nomeEntidadeEmpregadora, BigDecimal rendimento) {
        this.entidadeEmpregadoraId = entidadeEmpregadoraId;
        this.nomeEntidadeEmpregadora = nomeEntidadeEmpregadora;
        this.rendimento = rendimento;
    }

    public Long getEntidadeEmpregadoraId() {
        return entidadeEmpregadoraId;
    }

    public void setEntidadeEmpregadoraId(Long entidadeEmpregadoraId) {
        this.entidadeEmpregadoraId = entidadeEmpregadoraId;
    }

    public String getNomeEntidadeEmpregadora() {
        return nomeEntidadeEmpregadora;
    }

    public void setNomeEntidadeEmpregadora(String nomeEntidadeEmpregadora) {
        this.nomeEntidadeEmpregadora = nomeEntidadeEmpregadora;
    }

    public BigDecimal getRendimento() {
        return rendimento;
    }

    public void setRendimento(BigDecimal rendimento) {
        this.rendimento = rendimento;
    }
}
