package br.com.alugueldecarros.domain.model;

import java.math.BigDecimal;

public class ContratoCredito extends BaseEntity {

    private Long pedidoId;
    private Long contratoId;
    private Long bancoId;
    private BigDecimal valor;
    private BigDecimal taxaJuros;
    private BigDecimal parcelaMensal;

    public Long getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(Long pedidoId) {
        this.pedidoId = pedidoId;
    }

    public Long getContratoId() {
        return contratoId;
    }

    public void setContratoId(Long contratoId) {
        this.contratoId = contratoId;
    }

    public Long getBancoId() {
        return bancoId;
    }

    public void setBancoId(Long bancoId) {
        this.bancoId = bancoId;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public BigDecimal getTaxaJuros() {
        return taxaJuros;
    }

    public void setTaxaJuros(BigDecimal taxaJuros) {
        this.taxaJuros = taxaJuros;
    }

    public BigDecimal getParcelaMensal() {
        return parcelaMensal;
    }

    public void setParcelaMensal(BigDecimal parcelaMensal) {
        this.parcelaMensal = parcelaMensal;
    }
}
