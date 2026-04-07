package br.com.alugueldecarros.presentation.rest;

import br.com.alugueldecarros.application.dto.NotificacaoDtos;
import br.com.alugueldecarros.application.facade.NotificacaoFacade;
import io.smallrye.mutiny.Multi;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.RestStreamElementType;

import java.util.List;

@Path("/api/clientes/{clienteId}/notificacoes")
@Produces(MediaType.APPLICATION_JSON)
public class NotificacaoResource {

    @Inject
    NotificacaoFacade notificacaoFacade;

    @GET
    public List<NotificacaoDtos.NotificacaoResponse> listarHistorico(@PathParam("clienteId") Long clienteId) {
        return notificacaoFacade.listarHistorico(clienteId);
    }

    @GET
    @Path("/nao-lidas")
    public List<NotificacaoDtos.NotificacaoResponse> listarNaoLidas(@PathParam("clienteId") Long clienteId) {
        return notificacaoFacade.listarNaoLidas(clienteId);
    }

    @GET
    @Path("/stream")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @RestStreamElementType(MediaType.APPLICATION_JSON)
    public Multi<NotificacaoDtos.NotificacaoResponse> acompanhar(@PathParam("clienteId") Long clienteId) {
        return notificacaoFacade.acompanhar(clienteId);
    }
}
