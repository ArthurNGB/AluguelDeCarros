package br.com.alugueldecarros.application.facade;

import br.com.alugueldecarros.application.dto.PedidoDtos;
import br.com.alugueldecarros.application.mapper.ApiMapper;
import br.com.alugueldecarros.application.service.PedidoService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class PedidoFacade {

    @Inject
    PedidoService pedidoService;

    public PedidoDtos.PedidoResponse criar(Long clienteId, PedidoDtos.CriarPedidoRequest request) {
        return ApiMapper.toPedidoResponse(pedidoService.criar(clienteId, request));
    }

    public PedidoDtos.PedidoResponse atualizar(Long clienteId, Long pedidoId, PedidoDtos.AtualizarPedidoRequest request) {
        return ApiMapper.toPedidoResponse(pedidoService.atualizar(clienteId, pedidoId, request));
    }

    public PedidoDtos.PedidoResponse cancelar(Long clienteId, Long pedidoId) {
        return ApiMapper.toPedidoResponse(pedidoService.cancelar(clienteId, pedidoId));
    }

    public List<PedidoDtos.PedidoResponse> listar(Long clienteId) {
        return pedidoService.listarPorCliente(clienteId).stream()
                .map(ApiMapper::toPedidoResponse)
                .toList();
    }

    public PedidoDtos.PedidoResponse solicitarProrrogacao(Long clienteId, PedidoDtos.SolicitarProrrogacaoRequest request) {
        return ApiMapper.toPedidoResponse(pedidoService.solicitarProrrogacao(clienteId, request));
    }
}
