package br.com.alugueldecarros.application.service;

import br.com.alugueldecarros.application.dto.PedidoDtos;
import br.com.alugueldecarros.domain.exception.BusinessException;
import br.com.alugueldecarros.domain.exception.NotFoundException;
import br.com.alugueldecarros.domain.model.Automovel;
import br.com.alugueldecarros.domain.model.Cliente;
import br.com.alugueldecarros.domain.model.Contrato;
import br.com.alugueldecarros.domain.model.PedidoAluguel;
import br.com.alugueldecarros.domain.model.StatusPedido;
import br.com.alugueldecarros.domain.model.TipoPedido;
import br.com.alugueldecarros.domain.repository.AutomovelRepository;
import br.com.alugueldecarros.domain.repository.ContratoRepository;
import br.com.alugueldecarros.domain.repository.PedidoAluguelRepository;
import br.com.alugueldecarros.domain.repository.UsuarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@ApplicationScoped
public class PedidoService {

    @Inject
    UsuarioRepository usuarioRepository;

    @Inject
    AutomovelRepository automovelRepository;

    @Inject
    PedidoAluguelRepository pedidoRepository;

    @Inject
    ContratoRepository contratoRepository;

    public PedidoAluguel criar(Long clienteId, PedidoDtos.CriarPedidoRequest request) {
        Cliente cliente = buscarCliente(clienteId);
        Automovel automovel = buscarAutomovelDisponivel(request.automovelId());

        validarDatas(request.dataInicio(), request.dataFim());

        PedidoAluguel pedido = new PedidoAluguel();
        pedido.setClienteId(cliente.getId());
        pedido.setAutomovelId(automovel.getId());
        pedido.setDataCriacao(LocalDateTime.now());
        pedido.setDataInicio(request.dataInicio());
        pedido.setDataFim(request.dataFim());
        pedido.setJustificativa(request.justificativa());
        pedido.setRequerCredito(request.requerCredito());
        pedido.setStatus(StatusPedido.SUBMETIDO);
        pedido.setTipoPedido(TipoPedido.ALUGUEL);
        pedido.setValorEstimado(calcularValor(automovel, request.dataInicio(), request.dataFim()));

        return pedidoRepository.save(pedido);
    }

    public PedidoAluguel atualizar(Long clienteId, Long pedidoId, PedidoDtos.AtualizarPedidoRequest request) {
        buscarCliente(clienteId);
        PedidoAluguel pedido = buscarPedidoDoCliente(clienteId, pedidoId);
        validarEdicaoPermitida(pedido);
        validarDatas(request.dataInicio(), request.dataFim());

        Automovel automovel = automovelRepository.findById(request.automovelId())
                .orElseThrow(() -> new NotFoundException("Automovel nao encontrado."));
        if (!automovel.isDisponivel() && !automovel.getId().equals(pedido.getAutomovelId())) {
            throw new BusinessException("O automovel informado nao esta disponivel.");
        }

        pedido.setAutomovelId(automovel.getId());
        pedido.setDataInicio(request.dataInicio());
        pedido.setDataFim(request.dataFim());
        pedido.setJustificativa(request.justificativa());
        pedido.setRequerCredito(request.requerCredito());
        pedido.setStatus(StatusPedido.SUBMETIDO);
        pedido.setParecerAgente(null);
        pedido.setAgenteResponsavelId(null);
        pedido.setContratoCreditoId(null);
        pedido.setValorEstimado(calcularValor(automovel, request.dataInicio(), request.dataFim()));

        return pedidoRepository.save(pedido);
    }

    public PedidoAluguel cancelar(Long clienteId, Long pedidoId) {
        buscarCliente(clienteId);
        PedidoAluguel pedido = buscarPedidoDoCliente(clienteId, pedidoId);

        if (pedido.getStatus() == StatusPedido.CONTRATADO) {
            throw new BusinessException("Nao e possivel cancelar um pedido que ja gerou contrato.");
        }

        pedido.setStatus(StatusPedido.CANCELADO);
        return pedidoRepository.save(pedido);
    }

    public List<PedidoAluguel> listarPorCliente(Long clienteId) {
        buscarCliente(clienteId);
        return pedidoRepository.findByClienteId(clienteId);
    }

    public PedidoAluguel solicitarProrrogacao(Long clienteId, PedidoDtos.SolicitarProrrogacaoRequest request) {
        Cliente cliente = buscarCliente(clienteId);
        Contrato contrato = contratoRepository.findById(request.contratoId())
                .orElseThrow(() -> new NotFoundException("Contrato nao encontrado."));
        if (!contrato.getClienteId().equals(cliente.getId()) || !contrato.isAtivo()) {
            throw new BusinessException("A prorrogacao so pode ser solicitada para contrato ativo do cliente.");
        }
        if (!request.novaDataFim().isAfter(contrato.getDataFim())) {
            throw new BusinessException("A nova data deve ser posterior ao fim atual do contrato.");
        }

        PedidoAluguel pedido = new PedidoAluguel();
        pedido.setClienteId(cliente.getId());
        pedido.setAutomovelId(contrato.getAutomovelId());
        pedido.setDataCriacao(LocalDateTime.now());
        pedido.setDataInicio(contrato.getDataInicio());
        pedido.setDataFim(request.novaDataFim());
        pedido.setJustificativa(request.justificativa());
        pedido.setRequerCredito(false);
        pedido.setStatus(StatusPedido.PRORROGACAO_SOLICITADA);
        pedido.setTipoPedido(TipoPedido.PRORROGACAO);
        pedido.setContratoOrigemId(contrato.getId());
        pedido.setContratoId(contrato.getId());
        pedido.setValorEstimado(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        return pedidoRepository.save(pedido);
    }

    private Cliente buscarCliente(Long clienteId) {
        return usuarioRepository.findClienteById(clienteId)
                .orElseThrow(() -> new NotFoundException("Cliente nao encontrado."));
    }

    private Automovel buscarAutomovelDisponivel(Long automovelId) {
        Automovel automovel = automovelRepository.findById(automovelId)
                .orElseThrow(() -> new NotFoundException("Automovel nao encontrado."));
        if (!automovel.isDisponivel()) {
            throw new BusinessException("O automovel informado nao esta disponivel.");
        }
        return automovel;
    }

    private PedidoAluguel buscarPedidoDoCliente(Long clienteId, Long pedidoId) {
        PedidoAluguel pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new NotFoundException("Pedido nao encontrado."));
        if (!pedido.getClienteId().equals(clienteId)) {
            throw new BusinessException("O pedido informado nao pertence ao cliente.");
        }
        return pedido;
    }

    private void validarDatas(LocalDate dataInicio, LocalDate dataFim) {
        if (!dataFim.isAfter(dataInicio)) {
            throw new BusinessException("A data final deve ser posterior a data inicial.");
        }
    }

    private void validarEdicaoPermitida(PedidoAluguel pedido) {
        if (pedido.getStatus() == StatusPedido.CANCELADO
                || pedido.getStatus() == StatusPedido.REJEITADO
                || pedido.getStatus() == StatusPedido.CONTRATADO) {
            throw new BusinessException("O pedido nao pode mais ser alterado.");
        }
    }

    private BigDecimal calcularValor(Automovel automovel, LocalDate dataInicio, LocalDate dataFim) {
        long dias = Math.max(1, ChronoUnit.DAYS.between(dataInicio, dataFim));
        return automovel.getValorDiaria()
                .multiply(BigDecimal.valueOf(dias))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
