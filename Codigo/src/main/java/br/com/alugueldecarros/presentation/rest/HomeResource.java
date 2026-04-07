package br.com.alugueldecarros.presentation.rest;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

import java.net.URI;

@Path("/")
public class HomeResource {

    @GET
    public Response home() {
        URI appUri = UriBuilder.fromPath("/app").build();
        return Response.seeOther(appUri).build();
    }

    @GET
    @Path("/favicon.ico")
    public Response favicon() {
        return Response.noContent().build();
    }
}
