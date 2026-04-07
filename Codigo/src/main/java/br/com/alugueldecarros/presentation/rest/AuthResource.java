package br.com.alugueldecarros.presentation.rest;

import br.com.alugueldecarros.application.dto.AuthDtos;
import br.com.alugueldecarros.application.facade.AutenticacaoFacade;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    AutenticacaoFacade autenticacaoFacade;

    @POST
    @Path("/register-client")
    public Response registerClient(@Valid AuthDtos.RegisterClientRequest request) {
        return Response.status(Response.Status.CREATED)
                .entity(autenticacaoFacade.registrarCliente(request))
                .build();
    }

    @POST
    @Path("/login")
    public AuthDtos.UsuarioResponse login(@Valid AuthDtos.LoginRequest request) {
        return autenticacaoFacade.login(request);
    }
}
