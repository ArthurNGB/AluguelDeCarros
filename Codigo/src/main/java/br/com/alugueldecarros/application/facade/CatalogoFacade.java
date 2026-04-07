package br.com.alugueldecarros.application.facade;

import br.com.alugueldecarros.application.dto.CatalogoDtos;
import br.com.alugueldecarros.application.mapper.ApiMapper;
import br.com.alugueldecarros.application.service.FrotaService;
import br.com.alugueldecarros.domain.repository.AutomovelRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class CatalogoFacade {

    @Inject
    FrotaService frotaService;

    @Inject
    AutomovelRepository automovelRepository;

    public List<CatalogoDtos.AutomovelResponse> listarTodos() {
        return automovelRepository.listAll().stream()
                .map(ApiMapper::toAutomovelResponse)
                .toList();
    }

    public List<CatalogoDtos.AutomovelResponse> listarDisponiveis(String marca, Integer anoMinimo) {
        return frotaService.listarDisponiveis(marca, anoMinimo).stream()
                .map(ApiMapper::toAutomovelResponse)
                .toList();
    }

    public CatalogoDtos.AutomovelResponse cadastrar(Long proprietarioId, CatalogoDtos.CriarAutomovelRequest request) {
        return ApiMapper.toAutomovelResponse(frotaService.cadastrar(proprietarioId, request));
    }

    public CatalogoDtos.AutomovelResponse atualizar(Long automovelId, CatalogoDtos.AtualizarAutomovelRequest request) {
        return ApiMapper.toAutomovelResponse(frotaService.atualizar(automovelId, request));
    }

    public void remover(Long automovelId) {
        frotaService.remover(automovelId);
    }
}
