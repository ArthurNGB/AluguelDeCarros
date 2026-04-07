package br.com.alugueldecarros.domain.repository;

import br.com.alugueldecarros.domain.model.Notificacao;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class NotificacaoRepository extends AbstractInMemoryRepository<Notificacao> {

    public List<Notificacao> findByClienteId(Long clienteId) {
        return listAll().stream()
                .filter(notificacao -> notificacao.getClienteId().equals(clienteId))
                .toList();
    }

    public List<Notificacao> findNaoLidas(Long clienteId) {
        return listAll().stream()
                .filter(notificacao -> notificacao.getClienteId().equals(clienteId))
                .filter(notificacao -> !notificacao.isLida())
                .toList();
    }
}
