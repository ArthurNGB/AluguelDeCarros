package br.com.alugueldecarros.presentation.rest;

import br.com.alugueldecarros.application.dto.AnaliseDtos;
import br.com.alugueldecarros.application.dto.AuthDtos;
import br.com.alugueldecarros.application.dto.PedidoDtos;
import br.com.alugueldecarros.application.facade.AnaliseFacade;
import br.com.alugueldecarros.application.mapper.ApiMapper;
import br.com.alugueldecarros.domain.repository.UsuarioRepository;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/api/agentes")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AgenteResource {

    @Inject
    UsuarioRepository usuarioRepository;

    @Inject
    AnaliseFacade analiseFacade;

    @GET
    public List<AuthDtos.AgenteResponse> listarAgentes() {
        return usuarioRepository.listAgentes().stream()
                .map(ApiMapper::toAgenteResponse)
                .toList();
    }

    @POST
    @Path("/{agenteId}/avaliacoes")
    public PedidoDtos.PedidoResponse avaliarPedido(
            @PathParam("agenteId") Long agenteId,
            @Valid AnaliseDtos.AvaliarPedidoRequest request
    ) {
        return analiseFacade.avaliarPedido(agenteId, request);
    }

    @POST
    @Path("/{agenteId}/creditos")
    public AnaliseDtos.CreditoResponse concederCredito(
            @PathParam("agenteId") Long agenteId,
            @Valid AnaliseDtos.ConcederCreditoRequest request
    ) {
        return analiseFacade.concederCredito(agenteId, request);
    }

    @GET
    @Path("/resumo-mensal")
    public AnaliseDtos.ResumoMensalResponse resumoMensal() {
        return analiseFacade.resumoMensalAtual();
    }
}
