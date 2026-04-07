package br.com.alugueldecarros.application.service;

import br.com.alugueldecarros.domain.model.Notificacao;
import br.com.alugueldecarros.domain.model.PedidoAluguel;
import br.com.alugueldecarros.domain.repository.NotificacaoRepository;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.operators.multi.processors.BroadcastProcessor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class NotificacaoService {

    private final Map<Long, BroadcastProcessor<Notificacao>> canais = new ConcurrentHashMap<>();

    @Inject
    NotificacaoRepository notificacaoRepository;

    public void notificarMudancaStatus(PedidoAluguel pedido, String descricaoStatus) {
        Notificacao notificacao = new Notificacao();
        notificacao.setClienteId(pedido.getClienteId());
        notificacao.setTitulo("Status do pedido atualizado");
        notificacao.setMensagem("Pedido " + pedido.getId() + " atualizado para " + descricaoStatus + ".");
        notificacao.setDataCriacao(LocalDateTime.now());
        notificacao.setLida(false);
        notificacaoRepository.save(notificacao);
        canal(pedido.getClienteId()).onNext(notificacao);
    }

    public List<Notificacao> listarNaoLidas(Long clienteId) {
        List<Notificacao> notificacoes = notificacaoRepository.findNaoLidas(clienteId);
        notificacoes.forEach(notificacao -> {
            notificacao.setLida(true);
            notificacaoRepository.save(notificacao);
        });
        return notificacoes;
    }

    public List<Notificacao> listarHistorico(Long clienteId) {
        return notificacaoRepository.findByClienteId(clienteId).stream()
                .sorted((a, b) -> b.getDataCriacao().compareTo(a.getDataCriacao()))
                .limit(10)
                .toList();
    }

    public Multi<Notificacao> acompanhar(Long clienteId) {
        return canal(clienteId);
    }

    private BroadcastProcessor<Notificacao> canal(Long clienteId) {
        return canais.computeIfAbsent(clienteId, ignored -> BroadcastProcessor.create());
    }
}
