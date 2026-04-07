package br.com.alugueldecarros.application.facade;

import br.com.alugueldecarros.application.dto.ContratoDtos;
import br.com.alugueldecarros.application.mapper.ApiMapper;
import br.com.alugueldecarros.application.service.ContratoService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ContratoFacade {

    @Inject
    ContratoService contratoService;

    public ContratoDtos.ContratoResponse criarContrato(ContratoDtos.CriarContratoRequest request) {
        return ApiMapper.toContratoResponse(contratoService.criarContrato(request));
    }

    public ContratoDtos.ContratoResponse devolver(ContratoDtos.DevolverContratoRequest request) {
        return ApiMapper.toContratoResponse(contratoService.devolver(request));
    }

    public byte[] gerarPdf(Long contratoId) {
        return contratoService.gerarPdf(contratoId);
    }
}
