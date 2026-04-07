package br.com.alugueldecarros.config;

import br.com.alugueldecarros.domain.exception.BusinessException;
import br.com.alugueldecarros.domain.exception.NotFoundException;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.List;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {
        if (exception instanceof jakarta.ws.rs.NotFoundException notFoundException) {
            return buildResponse(Response.Status.NOT_FOUND, notFoundException.getMessage(), List.of());
        }

        if (exception instanceof NotFoundException notFoundException) {
            return buildResponse(Response.Status.NOT_FOUND, notFoundException.getMessage(), List.of());
        }

        if (exception instanceof BusinessException businessException) {
            return buildResponse(Response.Status.BAD_REQUEST, businessException.getMessage(), List.of());
        }

        if (exception instanceof ConstraintViolationException validationException) {
            List<String> detalhes = validationException.getConstraintViolations().stream()
                    .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                    .toList();
            return buildResponse(Response.Status.BAD_REQUEST, "Erro de validacao.", detalhes);
        }

        return buildResponse(Response.Status.INTERNAL_SERVER_ERROR, "Erro interno inesperado.", List.of());
    }

    private Response buildResponse(Response.Status status, String mensagem, List<String> detalhes) {
        return Response.status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(new ErrorResponse(mensagem, detalhes))
                .build();
    }

    public record ErrorResponse(String mensagem, List<String> detalhes) {
    }
}
