package br.com.alugueldecarros.application.dto;

import br.com.alugueldecarros.domain.model.StatusAutomovel;

import java.math.BigDecimal;

public final class CatalogoDtos {

    private CatalogoDtos() {
    }

    public record AutomovelResponse(
            Long id,
            String matricula,
            int ano,
            String marca,
            String modelo,
            String placa,
            Long proprietarioId,
            BigDecimal valorDiaria,
            StatusAutomovel status,
            boolean disponivel
    ) {
    }

    public record CriarAutomovelRequest(
            String matricula,
            int ano,
            String marca,
            String modelo,
            String placa,
            BigDecimal valorDiaria
    ) {
    }

    public record AtualizarAutomovelRequest(
            String matricula,
            int ano,
            String marca,
            String modelo,
            String placa,
            BigDecimal valorDiaria,
            StatusAutomovel status
    ) {
    }
}
