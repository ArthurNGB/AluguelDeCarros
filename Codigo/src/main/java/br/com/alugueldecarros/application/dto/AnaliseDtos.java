package br.com.alugueldecarros.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public final class AnaliseDtos {

    private AnaliseDtos() {
    }

    public record AvaliarPedidoRequest(
            @NotNull Long pedidoId,
            boolean aprovado,
            @NotBlank String parecer
    ) {
    }

    public record ConcederCreditoRequest(
            @NotNull Long pedidoId,
            @NotNull @Positive BigDecimal valor,
            @NotNull @Positive BigDecimal taxaJuros,
            @NotNull @Positive BigDecimal quantidadeParcelas
    ) {
    }

    public record CreditoResponse(
            Long id,
            Long pedidoId,
            Long contratoId,
            Long bancoId,
            BigDecimal valor,
            BigDecimal taxaJuros,
            BigDecimal parcelaMensal
    ) {
    }

    public record ResumoMensalResponse(
            long pendentes,
            long aprovados,
            long rejeitados
    ) {
    }
}
