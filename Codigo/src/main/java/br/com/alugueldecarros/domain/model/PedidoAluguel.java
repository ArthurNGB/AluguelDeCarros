package br.com.alugueldecarros.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PedidoAluguel extends BaseEntity {

    private LocalDateTime dataCriacao;
    private StatusPedido status;
    private TipoPedido tipoPedido = TipoPedido.ALUGUEL;
    private Long clienteId;
    private Long automovelId;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private String justificativa;
    private boolean requerCredito;
    private BigDecimal valorEstimado;
    private Long agenteResponsavelId;
    private String parecerAgente;
    private Long contratoCreditoId;
    private Long contratoId;
    private Long contratoOrigemId;

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public void setStatus(StatusPedido status) {
        this.status = status;
    }

    public TipoPedido getTipoPedido() {
        return tipoPedido;
    }

    public void setTipoPedido(TipoPedido tipoPedido) {
        this.tipoPedido = tipoPedido;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public Long getAutomovelId() {
        return automovelId;
    }

    public void setAutomovelId(Long automovelId) {
        this.automovelId = automovelId;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }

    public boolean isRequerCredito() {
        return requerCredito;
    }

    public void setRequerCredito(boolean requerCredito) {
        this.requerCredito = requerCredito;
    }

    public BigDecimal getValorEstimado() {
        return valorEstimado;
    }

    public void setValorEstimado(BigDecimal valorEstimado) {
        this.valorEstimado = valorEstimado;
    }

    public Long getAgenteResponsavelId() {
        return agenteResponsavelId;
    }

    public void setAgenteResponsavelId(Long agenteResponsavelId) {
        this.agenteResponsavelId = agenteResponsavelId;
    }

    public String getParecerAgente() {
        return parecerAgente;
    }

    public void setParecerAgente(String parecerAgente) {
        this.parecerAgente = parecerAgente;
    }

    public Long getContratoCreditoId() {
        return contratoCreditoId;
    }

    public void setContratoCreditoId(Long contratoCreditoId) {
        this.contratoCreditoId = contratoCreditoId;
    }

    public Long getContratoId() {
        return contratoId;
    }

    public void setContratoId(Long contratoId) {
        this.contratoId = contratoId;
    }

    public Long getContratoOrigemId() {
        return contratoOrigemId;
    }

    public void setContratoOrigemId(Long contratoOrigemId) {
        this.contratoOrigemId = contratoOrigemId;
    }
}
