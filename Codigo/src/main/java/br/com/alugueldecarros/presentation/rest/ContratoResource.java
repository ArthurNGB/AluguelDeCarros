package br.com.alugueldecarros.presentation.rest;

import br.com.alugueldecarros.application.dto.ContratoDtos;
import br.com.alugueldecarros.application.facade.ContratoFacade;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/contratos")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ContratoResource {

    @Inject
    ContratoFacade contratoFacade;

    @POST
    public Response criarContrato(@Valid ContratoDtos.CriarContratoRequest request) {
        return Response.status(Response.Status.CREATED)
                .entity(contratoFacade.criarContrato(request))
                .build();
    }

    @POST
    @Path("/devolucoes")
    public ContratoDtos.ContratoResponse devolverContrato(@Valid ContratoDtos.DevolverContratoRequest request) {
        return contratoFacade.devolver(request);
    }

    @GET
    @Path("/{contratoId}/pdf")
    @Produces("application/pdf")
    public Response baixarPdf(@PathParam("contratoId") Long contratoId) {
        return Response.ok(contratoFacade.gerarPdf(contratoId))
                .header("Content-Disposition", "attachment; filename=contrato-" + contratoId + ".pdf")
                .build();
    }
}
