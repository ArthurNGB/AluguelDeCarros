package br.com.alugueldecarros.domain.repository;

import br.com.alugueldecarros.domain.model.ContratoCredito;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class ContratoCreditoRepository extends AbstractInMemoryRepository<ContratoCredito> {

    public Optional<ContratoCredito> findByPedidoId(Long pedidoId) {
        return listAll().stream()
                .filter(credito -> credito.getPedidoId().equals(pedidoId))
                .findFirst();
    }
}
