package br.com.alugueldecarros.application.facade;

import br.com.alugueldecarros.application.dto.AuthDtos;
import br.com.alugueldecarros.application.mapper.ApiMapper;
import br.com.alugueldecarros.application.service.AutenticacaoService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AutenticacaoFacade {

    @Inject
    AutenticacaoService autenticacaoService;

    public AuthDtos.ClienteResponse registrarCliente(AuthDtos.RegisterClientRequest request) {
        return ApiMapper.toClienteResponse(autenticacaoService.registrarCliente(request));
    }

    public AuthDtos.UsuarioResponse login(AuthDtos.LoginRequest request) {
        return ApiMapper.toUsuarioResponse(autenticacaoService.login(request));
    }
}
