package br.com.alugueldecarros.domain.repository;

import br.com.alugueldecarros.domain.model.Agente;
import br.com.alugueldecarros.domain.model.Cliente;
import br.com.alugueldecarros.domain.model.Usuario;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UsuarioRepository extends AbstractInMemoryRepository<Usuario> {

    public Optional<Usuario> findByEmail(String email) {
        return listAll().stream()
                .filter(usuario -> usuario.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    public Optional<Cliente> findClienteByCpf(String cpf) {
        String cpfNormalizado = somenteDigitos(cpf);
        return listClientes().stream()
                .filter(cliente -> somenteDigitos(cliente.getCpf()).equals(cpfNormalizado))
                .findFirst();
    }

    public Optional<Cliente> findClienteByRg(String rg) {
        String rgNormalizado = somenteDigitos(rg);
        return listClientes().stream()
                .filter(cliente -> somenteDigitos(cliente.getRg()).equals(rgNormalizado))
                .findFirst();
    }

    public Optional<Cliente> findClienteById(Long id) {
        return findById(id)
                .filter(Cliente.class::isInstance)
                .map(Cliente.class::cast);
    }

    public Optional<Agente> findAgenteById(Long id) {
        return findById(id)
                .filter(Agente.class::isInstance)
                .map(Agente.class::cast);
    }

    public List<Cliente> listClientes() {
        return listAll().stream()
                .filter(Cliente.class::isInstance)
                .map(Cliente.class::cast)
                .toList();
    }

    public List<Agente> listAgentes() {
        return listAll().stream()
                .filter(Agente.class::isInstance)
                .map(Agente.class::cast)
                .toList();
    }

    private String somenteDigitos(String valor) {
        return valor == null ? "" : valor.replaceAll("\\D", "");
    }
}
