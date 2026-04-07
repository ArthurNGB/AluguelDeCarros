package br.com.alugueldecarros.application.dto;

import br.com.alugueldecarros.domain.model.StatusPedido;
import br.com.alugueldecarros.domain.model.TipoPedido;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public final class PedidoDtos {

    private PedidoDtos() {
    }

    public record CriarPedidoRequest(
            @NotNull Long automovelId,
            @NotNull @FutureOrPresent LocalDate dataInicio,
            @NotNull @Future LocalDate dataFim,
            @NotBlank String justificativa,
            boolean requerCredito
    ) {
    }

    public record AtualizarPedidoRequest(
            @NotNull Long automovelId,
            @NotNull @FutureOrPresent LocalDate dataInicio,
            @NotNull @Future LocalDate dataFim,
            @NotBlank String justificativa,
            boolean requerCredito
    ) {
    }

    public record PedidoResponse(
            Long id,
            LocalDateTime dataCriacao,
            StatusPedido status,
            TipoPedido tipoPedido,
            Long clienteId,
            Long automovelId,
            LocalDate dataInicio,
            LocalDate dataFim,
            String justificativa,
            boolean requerCredito,
            BigDecimal valorEstimado,
            Long agenteResponsavelId,
            String parecerAgente,
            Long contratoCreditoId,
            Long contratoId,
            Long contratoOrigemId
    ) {
    }

    public record SolicitarProrrogacaoRequest(
            @NotNull Long contratoId,
            @NotNull @Future LocalDate novaDataFim,
            @NotBlank String justificativa
    ) {
    }
}
