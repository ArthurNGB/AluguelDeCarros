package br.com.alugueldecarros.domain.repository;

import br.com.alugueldecarros.domain.model.Contrato;
import br.com.alugueldecarros.domain.model.StatusContrato;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ContratoRepository extends AbstractInMemoryRepository<Contrato> {

    public Optional<Contrato> findActiveByAutomovelId(Long automovelId) {
        return listAll().stream()
                .filter(Contrato::isAtivo)
                .filter(contrato -> contrato.getAutomovelId().equals(automovelId))
                .findFirst();
    }

    public List<Contrato> findByClienteId(Long clienteId) {
        return listAll().stream()
                .filter(contrato -> contrato.getClienteId().equals(clienteId))
                .toList();
    }

    public List<Contrato> findAtivos() {
        return listAll().stream()
                .filter(contrato -> contrato.getStatus() == StatusContrato.EM_CONTRATO)
                .toList();
    }
}
