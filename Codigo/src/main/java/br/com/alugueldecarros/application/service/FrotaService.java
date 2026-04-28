package br.com.alugueldecarros.application.service;

import br.com.alugueldecarros.application.dto.CatalogoDtos;
import br.com.alugueldecarros.domain.exception.BusinessException;
import br.com.alugueldecarros.domain.exception.NotFoundException;
import br.com.alugueldecarros.domain.model.Automovel;
import br.com.alugueldecarros.domain.model.StatusAutomovel;
import br.com.alugueldecarros.domain.model.StatusPedido;
import br.com.alugueldecarros.domain.repository.AutomovelRepository;
import br.com.alugueldecarros.domain.repository.ContratoRepository;
import br.com.alugueldecarros.domain.repository.PedidoAluguelRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.util.List;

@ApplicationScoped
public class FrotaService {

    @Inject
    AutomovelRepository automovelRepository;

    @Inject
    ContratoRepository contratoRepository;

    @Inject
    PedidoAluguelRepository pedidoRepository;

    public Automovel cadastrar(Long proprietarioId, CatalogoDtos.CriarAutomovelRequest request) {
        validarDuplicidade(request.matricula(), request.placa(), null);

        Automovel automovel = new Automovel(
                normalizarTexto(request.matricula(), "Matricula"),
                request.ano(),
                normalizarTexto(request.marca(), "Marca"),
                normalizarTexto(request.modelo(), "Modelo"),
                normalizarTexto(request.placa(), "Placa"),
                proprietarioId,
                validarValorDiaria(request.valorDiaria())
        );

        automovel.setStatus(StatusAutomovel.DISPONIVEL);

        return automovelRepository.save(automovel);
    }

    public Automovel atualizar(Long automovelId, CatalogoDtos.AtualizarAutomovelRequest request) {
        Automovel automovel = buscarPorId(automovelId);

        validarDuplicidade(request.matricula(), request.placa(), automovelId);

        automovel.setMatricula(normalizarTexto(request.matricula(), "Matricula"));
        automovel.setAno(request.ano());
        automovel.setMarca(normalizarTexto(request.marca(), "Marca"));
        automovel.setModelo(normalizarTexto(request.modelo(), "Modelo"));
        automovel.setPlaca(normalizarTexto(request.placa(), "Placa"));
        automovel.setValorDiaria(validarValorDiaria(request.valorDiaria()));
        automovel.setStatus(request.status() == null ? automovel.getStatus() : request.status());

        return automovelRepository.save(automovel);
    }

    public void remover(Long automovelId) {
        contratoRepository.findActiveByAutomovelId(automovelId)
                .ifPresent(contrato -> {
                    throw new BusinessException("Nao e possivel remover um veiculo com contrato ativo em andamento.");
                });

        boolean temPedidoPendente = pedidoRepository.listAll().stream()
                .filter(pedido -> automovelId.equals(pedido.getAutomovelId()))
                .anyMatch(pedido -> pedido.getStatus() != StatusPedido.CANCELADO
                        && pedido.getStatus() != StatusPedido.REJEITADO
                        && pedido.getStatus() != StatusPedido.CONTRATADO
                        && pedido.getStatus() != StatusPedido.FINALIZADO
                        && pedido.getStatus() != StatusPedido.PRORROGACAO_REJEITADA);

        if (temPedidoPendente) {
            throw new BusinessException("Nao e possivel remover um veiculo com pedidos de aluguel em andamento. Cancele ou finalize os pedidos antes.");
        }

        buscarPorId(automovelId);
        automovelRepository.deleteById(automovelId);
    }

    public List<Automovel> listarDisponiveis(String marca, Integer anoMinimo) {
        return automovelRepository.findDisponiveis().stream()
                .filter(automovel -> marca == null || marca.isBlank() || automovel.getMarca().equalsIgnoreCase(marca.trim()))
                .filter(automovel -> anoMinimo == null || automovel.getAno() >= anoMinimo)
                .toList();
    }

    public Automovel buscarPorId(Long automovelId) {
        return automovelRepository.findById(automovelId)
                .orElseThrow(() -> new NotFoundException("Automovel nao encontrado."));
    }

    private void validarDuplicidade(String matricula, String placa, Long automovelAtualId) {
        String matriculaNormalizada = normalizarTexto(matricula, "Matricula");
        String placaNormalizada = normalizarTexto(placa, "Placa");

        automovelRepository.findByMatricula(matriculaNormalizada)
                .filter(automovel -> !automovel.getId().equals(automovelAtualId))
                .ifPresent(automovel -> {
                    throw new BusinessException("Ja existe automovel com esta matricula.");
                });

        automovelRepository.findByPlaca(placaNormalizada)
                .filter(automovel -> !automovel.getId().equals(automovelAtualId))
                .ifPresent(automovel -> {
                    throw new BusinessException("Ja existe automovel com esta placa.");
                });
    }

    private String normalizarTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new BusinessException(campo + " e obrigatorio.");
        }

        return valor.trim().toUpperCase();
    }

    private BigDecimal validarValorDiaria(BigDecimal valorDiaria) {
        if (valorDiaria == null || valorDiaria.signum() <= 0) {
            throw new BusinessException("Valor da diaria deve ser maior que zero.");
        }

        return valorDiaria;
    }
}