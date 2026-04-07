package br.com.alugueldecarros.application.dto;

import br.com.alugueldecarros.domain.model.StatusContrato;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

public final class ContratoDtos {

    private ContratoDtos() {
    }

    public record CriarContratoRequest(
            @NotNull Long pedidoId,
            @NotBlank String tipoContrato
    ) {
    }

    public record ContratoResponse(
            Long id,
            Long pedidoId,
            Long clienteId,
            Long automovelId,
            LocalDate dataInicio,
            LocalDate dataFim,
            LocalDate dataAssinatura,
            String tipo,
            StatusContrato status,
            boolean ativo,
            LocalDateTime dataDevolucao,
            Integer quilometragemFinal,
            String avarias
    ) {
    }

    public record DevolverContratoRequest(
            Long contratoId,
            Integer quilometragemFinal,
            String avarias
    ) {
    }
}
