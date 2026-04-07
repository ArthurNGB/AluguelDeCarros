package br.com.alugueldecarros.application.dto;

import java.time.LocalDateTime;

public final class NotificacaoDtos {

    private NotificacaoDtos() {
    }

    public record NotificacaoResponse(
            Long id,
            String titulo,
            String mensagem,
            LocalDateTime dataCriacao
    ) {
    }
}
