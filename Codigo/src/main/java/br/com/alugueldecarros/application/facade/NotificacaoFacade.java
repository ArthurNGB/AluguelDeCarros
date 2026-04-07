package br.com.alugueldecarros.application.facade;

import br.com.alugueldecarros.application.dto.NotificacaoDtos;
import br.com.alugueldecarros.application.mapper.ApiMapper;
import br.com.alugueldecarros.application.service.NotificacaoService;
import io.smallrye.mutiny.Multi;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class NotificacaoFacade {

    @Inject
    NotificacaoService notificacaoService;

    public List<NotificacaoDtos.NotificacaoResponse> listarHistorico(Long clienteId) {
        return notificacaoService.listarHistorico(clienteId).stream()
                .map(ApiMapper::toNotificacaoResponse)
                .toList();
    }

    public List<NotificacaoDtos.NotificacaoResponse> listarNaoLidas(Long clienteId) {
        return notificacaoService.listarNaoLidas(clienteId).stream()
                .map(ApiMapper::toNotificacaoResponse)
                .toList();
    }

    public Multi<NotificacaoDtos.NotificacaoResponse> acompanhar(Long clienteId) {
        return notificacaoService.acompanhar(clienteId)
                .map(ApiMapper::toNotificacaoResponse);
    }
}
