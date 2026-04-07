package br.com.alugueldecarros.domain.repository;

import br.com.alugueldecarros.domain.model.EntidadeEmpregadora;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class EntidadeEmpregadoraRepository extends AbstractInMemoryRepository<EntidadeEmpregadora> {

    public Optional<EntidadeEmpregadora> findByCnpj(String cnpj) {
        return listAll().stream()
                .filter(entidade -> entidade.getCnpj().equals(cnpj))
                .findFirst();
    }
}
