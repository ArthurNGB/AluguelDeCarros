package br.com.alugueldecarros.application.dto;

import br.com.alugueldecarros.domain.model.TipoAgente;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

public final class AuthDtos {

    private AuthDtos() {
    }

    public record RegisterClientRequest(
            @NotBlank String nome,
            @Email @NotBlank String email,
            @NotBlank String senha,
            @NotBlank String cpf,
            @NotBlank String rg,
            @NotBlank String endereco,
            @NotBlank String profissao,
            @Valid List<EmpregoRequest> empregos
    ) {
    }

    public record EmpregoRequest(
            @NotBlank String nomeEntidadeEmpregadora,
            @NotBlank String cnpj,
            @NotNull @Positive BigDecimal rendimento
    ) {
    }

    public record LoginRequest(
            @Email @NotBlank String email,
            @NotBlank String senha
    ) {
    }

    public record UsuarioResponse(
            Long id,
            String nome,
            String email,
            String perfil
    ) {
    }

    public record ClienteResponse(
            Long id,
            String nome,
            String email,
            String cpf,
            String profissao,
            @NotEmpty List<EmpregoResponse> empregos
    ) {
    }

    public record EmpregoResponse(
            Long entidadeEmpregadoraId,
            String nomeEntidadeEmpregadora,
            BigDecimal rendimento
    ) {
    }

    public record AgenteResponse(
            Long id,
            String nome,
            String email,
            TipoAgente tipo
    ) {
    }
}
