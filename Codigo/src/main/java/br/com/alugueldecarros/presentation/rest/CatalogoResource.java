package br.com.alugueldecarros.presentation.rest;

import br.com.alugueldecarros.application.dto.CatalogoDtos;
import br.com.alugueldecarros.application.facade.CatalogoFacade;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/automoveis")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CatalogoResource {

    @Inject
    CatalogoFacade catalogoFacade;

    @GET
    public List<CatalogoDtos.AutomovelResponse> listarAutomoveis(
            @QueryParam("somenteDisponiveis") Boolean somenteDisponiveis,
            @QueryParam("marca") String marca,
            @QueryParam("anoMinimo") Integer anoMinimo
    ) {
        if (Boolean.TRUE.equals(somenteDisponiveis)) {
            return catalogoFacade.listarDisponiveis(marca, anoMinimo);
        }
        return catalogoFacade.listarTodos();
    }

    @POST
    @Path("/empresa/{empresaId}")
    public Response cadastrarAutomovel(
            @PathParam("empresaId") Long empresaId,
            @Valid CatalogoDtos.CriarAutomovelRequest request
    ) {
        return Response.status(Response.Status.CREATED)
                .entity(catalogoFacade.cadastrar(empresaId, request))
                .build();
    }

    @PUT
    @Path("/{automovelId}")
    public CatalogoDtos.AutomovelResponse atualizarAutomovel(
            @PathParam("automovelId") Long automovelId,
            @Valid CatalogoDtos.AtualizarAutomovelRequest request
    ) {
        return catalogoFacade.atualizar(automovelId, request);
    }

    @DELETE
    @Path("/{automovelId}")
    public Response removerAutomovel(@PathParam("automovelId") Long automovelId) {
        catalogoFacade.remover(automovelId);
        return Response.noContent().build();
    }
}
