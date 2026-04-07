package br.com.alugueldecarros.domain.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Contrato extends BaseEntity {

    private Long pedidoId;
    private Long clienteId;
    private Long automovelId;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private LocalDate dataAssinatura;
    private String tipo;
    private StatusContrato status;
    private LocalDateTime dataDevolucao;
    private Integer quilometragemFinal;
    private String avarias;

    public Long getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(Long pedidoId) {
        this.pedidoId = pedidoId;
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

    public LocalDate getDataAssinatura() {
        return dataAssinatura;
    }

    public void setDataAssinatura(LocalDate dataAssinatura) {
        this.dataAssinatura = dataAssinatura;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public StatusContrato getStatus() {
        return status;
    }

    public void setStatus(StatusContrato status) {
        this.status = status;
    }

    public LocalDateTime getDataDevolucao() {
        return dataDevolucao;
    }

    public void setDataDevolucao(LocalDateTime dataDevolucao) {
        this.dataDevolucao = dataDevolucao;
    }

    public Integer getQuilometragemFinal() {
        return quilometragemFinal;
    }

    public void setQuilometragemFinal(Integer quilometragemFinal) {
        this.quilometragemFinal = quilometragemFinal;
    }

    public String getAvarias() {
        return avarias;
    }

    public void setAvarias(String avarias) {
        this.avarias = avarias;
    }

    public boolean isAtivo() {
        return status == StatusContrato.EM_CONTRATO;
    }
}
