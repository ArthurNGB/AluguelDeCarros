package br.com.alugueldecarros.application.service;

import br.com.alugueldecarros.application.dto.AnaliseDtos;
import br.com.alugueldecarros.domain.exception.BusinessException;
import br.com.alugueldecarros.domain.exception.NotFoundException;
import br.com.alugueldecarros.domain.model.Agente;
import br.com.alugueldecarros.domain.model.Contrato;
import br.com.alugueldecarros.domain.model.ContratoCredito;
import br.com.alugueldecarros.domain.model.PedidoAluguel;
import br.com.alugueldecarros.domain.model.StatusPedido;
import br.com.alugueldecarros.domain.model.TipoAgente;
import br.com.alugueldecarros.domain.model.TipoPedido;
import br.com.alugueldecarros.domain.repository.ContratoCreditoRepository;
import br.com.alugueldecarros.domain.repository.ContratoRepository;
import br.com.alugueldecarros.domain.repository.PedidoAluguelRepository;
import br.com.alugueldecarros.domain.repository.UsuarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@ApplicationScoped
public class AnaliseService {

    @Inject
    UsuarioRepository usuarioRepository;

    @Inject
    PedidoAluguelRepository pedidoRepository;

    @Inject
    ContratoCreditoRepository contratoCreditoRepository;

    @Inject
    ContratoRepository contratoRepository;

    @Inject
    NotificacaoService notificacaoService;

    public PedidoAluguel avaliarPedido(Long agenteId, AnaliseDtos.AvaliarPedidoRequest request) {
        Agente agente = buscarAgente(agenteId);

        PedidoAluguel pedido = pedidoRepository.findById(request.pedidoId())
                .orElseThrow(() -> new NotFoundException("Pedido nao encontrado."));

        if (pedido.getStatus() == StatusPedido.CANCELADO
                || pedido.getStatus() == StatusPedido.CONTRATADO
                || pedido.getStatus() == StatusPedido.FINALIZADO) {
            throw new BusinessException("Nao e possivel avaliar este pedido.");
        }

        if (pedido.getTipoPedido() == TipoPedido.ALUGUEL && agente.getTipo() != TipoAgente.EMPRESA) {
            throw new BusinessException("Somente agentes do tipo EMPRESA podem avaliar o pedido de aluguel.");
        }

        pedido.setAgenteResponsavelId(agente.getId());
        pedido.setParecerAgente(request.parecer());

        if (pedido.getTipoPedido() == TipoPedido.PRORROGACAO) {
            processarProrrogacao(pedido, request.aprovado());
        } else {
            pedido.setStatus(request.aprovado()
                    ? (pedido.isRequerCredito() ? StatusPedido.AGUARDANDO_CREDITO : StatusPedido.APROVADO)
                    : StatusPedido.REJEITADO);
        }

        PedidoAluguel salvo = pedidoRepository.save(pedido);
        notificacaoService.notificarMudancaStatus(salvo, salvo.getStatus().name());
        return salvo;
    }

    public ContratoCredito concederCredito(Long agenteId, AnaliseDtos.ConcederCreditoRequest request) {
        Agente agente = buscarAgente(agenteId);

        if (agente.getTipo() != TipoAgente.BANCO) {
            throw new BusinessException("Somente agentes do tipo BANCO podem conceder credito.");
        }

        PedidoAluguel pedido = pedidoRepository.findById(request.pedidoId())
                .orElseThrow(() -> new NotFoundException("Pedido nao encontrado."));

        if (!pedido.isRequerCredito()) {
            throw new BusinessException("O pedido informado nao requer credito.");
        }

        if (pedido.getStatus() != StatusPedido.AGUARDANDO_CREDITO) {
            throw new BusinessException("O pedido precisa estar aguardando credito para esta operacao.");
        }

        contratoCreditoRepository.findByPedidoId(pedido.getId())
                .ifPresent(credito -> {
                    throw new BusinessException("Ja existe credito concedido para este pedido.");
                });

        ContratoCredito contratoCredito = new ContratoCredito();
        contratoCredito.setPedidoId(pedido.getId());
        contratoCredito.setBancoId(agente.getId());
        contratoCredito.setValor(request.valor());
        contratoCredito.setTaxaJuros(request.taxaJuros());
        contratoCredito.setParcelaMensal(calcularParcelaMensal(
                request.valor(),
                request.taxaJuros(),
                request.quantidadeParcelas()
        ));

        contratoCreditoRepository.save(contratoCredito);

        pedido.setContratoCreditoId(contratoCredito.getId());
        pedido.setStatus(StatusPedido.CREDITO_APROVADO);
        pedidoRepository.save(pedido);

        notificacaoService.notificarMudancaStatus(pedido, pedido.getStatus().name());

        return contratoCredito;
    }

    public AnaliseDtos.ResumoMensalResponse resumoMensalAtual() {
        LocalDate hoje = LocalDate.now();

        long pendentes = pedidoRepository.listAll().stream()
                .filter(pedido -> pedido.getDataCriacao() != null)
                .filter(pedido -> pedido.getDataCriacao().getMonth() == hoje.getMonth()
                        && pedido.getDataCriacao().getYear() == hoje.getYear())
                .filter(pedido -> statusPendente(pedido.getStatus()))
                .count();

        long aprovados = pedidoRepository.listAll().stream()
                .filter(pedido -> pedido.getDataCriacao() != null)
                .filter(pedido -> pedido.getDataCriacao().getMonth() == hoje.getMonth()
                        && pedido.getDataCriacao().getYear() == hoje.getYear())
                .filter(pedido -> statusAprovado(pedido.getStatus()))
                .count();

        long rejeitados = pedidoRepository.listAll().stream()
                .filter(pedido -> pedido.getDataCriacao() != null)
                .filter(pedido -> pedido.getDataCriacao().getMonth() == hoje.getMonth()
                        && pedido.getDataCriacao().getYear() == hoje.getYear())
                .filter(pedido -> statusRejeitado(pedido.getStatus()))
                .count();

        return new AnaliseDtos.ResumoMensalResponse(pendentes, aprovados, rejeitados);
    }

    private Agente buscarAgente(Long agenteId) {
        return usuarioRepository.findAgenteById(agenteId)
                .orElseThrow(() -> new NotFoundException("Agente nao encontrado."));
    }

    private BigDecimal calcularParcelaMensal(BigDecimal valor, BigDecimal taxaJuros, BigDecimal quantidadeParcelas) {
        return valor.multiply(BigDecimal.ONE.add(taxaJuros))
                .divide(quantidadeParcelas, 2, RoundingMode.HALF_UP);
    }

    private void processarProrrogacao(PedidoAluguel pedido, boolean aprovado) {
        if (aprovado) {
            Contrato contrato = contratoRepository.findById(pedido.getContratoOrigemId())
                    .orElseThrow(() -> new NotFoundException("Contrato nao encontrado para a prorrogacao."));

            contrato.setDataFim(pedido.getDataFim());
            contratoRepository.save(contrato);

            pedido.setStatus(StatusPedido.PRORROGACAO_APROVADA);
        } else {
            pedido.setStatus(StatusPedido.PRORROGACAO_REJEITADA);
        }
    }

    private boolean statusPendente(StatusPedido statusPedido) {
        return statusPedido == StatusPedido.SUBMETIDO
                || statusPedido == StatusPedido.AGUARDANDO_CREDITO
                || statusPedido == StatusPedido.PRORROGACAO_SOLICITADA;
    }

    private boolean statusAprovado(StatusPedido statusPedido) {
        return statusPedido == StatusPedido.APROVADO
                || statusPedido == StatusPedido.CREDITO_APROVADO
                || statusPedido == StatusPedido.CONTRATADO
                || statusPedido == StatusPedido.FINALIZADO
                || statusPedido == StatusPedido.PRORROGACAO_APROVADA;
    }

    private boolean statusRejeitado(StatusPedido statusPedido) {
        return statusPedido == StatusPedido.REJEITADO
                || statusPedido == StatusPedido.PRORROGACAO_REJEITADA;
    }
}