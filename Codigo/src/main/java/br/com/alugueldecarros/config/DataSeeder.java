package br.com.alugueldecarros.config;

import br.com.alugueldecarros.domain.model.Automovel;
import br.com.alugueldecarros.domain.model.Banco;
import br.com.alugueldecarros.domain.model.Cliente;
import br.com.alugueldecarros.domain.model.Emprego;
import br.com.alugueldecarros.domain.model.Empresa;
import br.com.alugueldecarros.domain.model.EntidadeEmpregadora;
import br.com.alugueldecarros.domain.repository.AutomovelRepository;
import br.com.alugueldecarros.domain.repository.EntidadeEmpregadoraRepository;
import br.com.alugueldecarros.domain.repository.UsuarioRepository;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import java.math.BigDecimal;

@ApplicationScoped
public class DataSeeder {

    @Inject
    UsuarioRepository usuarioRepository;

    @Inject
    AutomovelRepository automovelRepository;

    @Inject
    EntidadeEmpregadoraRepository entidadeEmpregadoraRepository;

    void onStart(@Observes StartupEvent event) {
        if (!usuarioRepository.listAll().isEmpty()) {
            return;
        }

        Empresa empresa = new Empresa("Locadora Horizonte", "empresa@horizonte.com", "123456");
        usuarioRepository.save(empresa);

        Banco banco = new Banco("Banco Rodovia", "banco@rodovia.com", "123456");
        usuarioRepository.save(banco);

        EntidadeEmpregadora entidade = entidadeEmpregadoraRepository.save(
                new EntidadeEmpregadora("Tech Motors", "12345678000199")
        );

        Cliente cliente = new Cliente(
                "Maria Silva",
                "maria@cliente.com",
                "123456",
                "12345678910",
                "12345678",
                "Rua das Flores, 123",
                "Analista de Sistemas"
        );
        cliente.getEmpregos().add(new Emprego(entidade.getId(), entidade.getNome(), new BigDecimal("6500.00")));
        usuarioRepository.save(cliente);

        automovelRepository.save(new Automovel(
                "AUTO-001", 2023, "Toyota", "Corolla", "ABC1D23", empresa.getId(), new BigDecimal("180.00")
        ));
        automovelRepository.save(new Automovel(
                "AUTO-002", 2024, "Jeep", "Compass", "EFG4H56", empresa.getId(), new BigDecimal("260.00")
        ));
        automovelRepository.save(new Automovel(
                "AUTO-003", 2022, "Chevrolet", "Onix", "IJK7L89", empresa.getId(), new BigDecimal("140.00")
        ));
    }
}
