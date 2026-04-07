package br.com.alugueldecarros.presentation.rest;

import br.com.alugueldecarros.application.dto.AuthDtos;
import br.com.alugueldecarros.application.dto.PedidoDtos;
import br.com.alugueldecarros.application.facade.PedidoFacade;
import br.com.alugueldecarros.application.mapper.ApiMapper;
import br.com.alugueldecarros.domain.repository.UsuarioRepository;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/clientes")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ClienteResource {

    @Inject
    UsuarioRepository usuarioRepository;

    @Inject
    PedidoFacade pedidoFacade;

    @GET
    public List<AuthDtos.ClienteResponse> listarClientes() {
        return usuarioRepository.listClientes().stream()
                .map(ApiMapper::toClienteResponse)
                .toList();
    }

    @POST
    @Path("/{clienteId}/pedidos")
    public Response criarPedido(@PathParam("clienteId") Long clienteId, @Valid PedidoDtos.CriarPedidoRequest request) {
        return Response.status(Response.Status.CREATED)
                .entity(pedidoFacade.criar(clienteId, request))
                .build();
    }

    @PUT
    @Path("/{clienteId}/pedidos/{pedidoId}")
    public PedidoDtos.PedidoResponse atualizarPedido(
            @PathParam("clienteId") Long clienteId,
            @PathParam("pedidoId") Long pedidoId,
            @Valid PedidoDtos.AtualizarPedidoRequest request
    ) {
        return pedidoFacade.atualizar(clienteId, pedidoId, request);
    }

    @PATCH
    @Path("/{clienteId}/pedidos/{pedidoId}/cancelamento")
    public PedidoDtos.PedidoResponse cancelarPedido(
            @PathParam("clienteId") Long clienteId,
            @PathParam("pedidoId") Long pedidoId
    ) {
        return pedidoFacade.cancelar(clienteId, pedidoId);
    }

    @GET
    @Path("/{clienteId}/pedidos")
    public List<PedidoDtos.PedidoResponse> listarPedidos(@PathParam("clienteId") Long clienteId) {
        return pedidoFacade.listar(clienteId);
    }

    @POST
    @Path("/{clienteId}/prorrogacoes")
    public Response solicitarProrrogacao(
            @PathParam("clienteId") Long clienteId,
            @Valid PedidoDtos.SolicitarProrrogacaoRequest request
    ) {
        return Response.status(Response.Status.CREATED)
                .entity(pedidoFacade.solicitarProrrogacao(clienteId, request))
                .build();
    }
}
