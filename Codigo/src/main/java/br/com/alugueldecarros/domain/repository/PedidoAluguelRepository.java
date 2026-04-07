package br.com.alugueldecarros.domain.repository;

import br.com.alugueldecarros.domain.model.PedidoAluguel;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PedidoAluguelRepository extends AbstractInMemoryRepository<PedidoAluguel> {

    public List<PedidoAluguel> findByClienteId(Long clienteId) {
        return listAll().stream()
                .filter(pedido -> pedido.getClienteId().equals(clienteId))
                .toList();
    }

    public List<PedidoAluguel> findByClienteIdAndContratoOrigem(Long clienteId, Long contratoId) {
        return listAll().stream()
                .filter(pedido -> pedido.getClienteId().equals(clienteId))
                .filter(pedido -> contratoId.equals(pedido.getContratoOrigemId()))
                .toList();
    }

    public Optional<PedidoAluguel> findByContratoOrigemId(Long contratoOrigemId) {
        return listAll().stream()
                .filter(pedido -> contratoOrigemId.equals(pedido.getContratoOrigemId()))
                .findFirst();
    }
}
