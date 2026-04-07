package br.com.alugueldecarros.application.facade;

import br.com.alugueldecarros.application.dto.AnaliseDtos;
import br.com.alugueldecarros.application.dto.PedidoDtos;
import br.com.alugueldecarros.application.mapper.ApiMapper;
import br.com.alugueldecarros.application.service.AnaliseService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AnaliseFacade {

    @Inject
    AnaliseService analiseService;

    public PedidoDtos.PedidoResponse avaliarPedido(Long agenteId, AnaliseDtos.AvaliarPedidoRequest request) {
        return ApiMapper.toPedidoResponse(analiseService.avaliarPedido(agenteId, request));
    }

    public AnaliseDtos.CreditoResponse concederCredito(Long agenteId, AnaliseDtos.ConcederCreditoRequest request) {
        return ApiMapper.toCreditoResponse(analiseService.concederCredito(agenteId, request));
    }

    public AnaliseDtos.ResumoMensalResponse resumoMensalAtual() {
        return analiseService.resumoMensalAtual();
    }
}
