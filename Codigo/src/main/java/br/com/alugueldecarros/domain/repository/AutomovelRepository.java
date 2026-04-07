package br.com.alugueldecarros.domain.repository;

import br.com.alugueldecarros.domain.model.Automovel;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class AutomovelRepository extends AbstractInMemoryRepository<Automovel> {

    public Optional<Automovel> findByPlaca(String placa) {
        return listAll().stream()
                .filter(automovel -> automovel.getPlaca().equalsIgnoreCase(placa))
                .findFirst();
    }

    public Optional<Automovel> findByMatricula(String matricula) {
        return listAll().stream()
                .filter(automovel -> automovel.getMatricula().equalsIgnoreCase(matricula))
                .findFirst();
    }

    public List<Automovel> findDisponiveis() {
        return listAll().stream()
                .filter(Automovel::isDisponivel)
                .toList();
    }
}
