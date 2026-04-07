package br.com.alugueldecarros.application.service;

import br.com.alugueldecarros.application.dto.AuthDtos;
import br.com.alugueldecarros.domain.exception.BusinessException;
import br.com.alugueldecarros.domain.exception.NotFoundException;
import br.com.alugueldecarros.domain.model.Cliente;
import br.com.alugueldecarros.domain.model.Emprego;
import br.com.alugueldecarros.domain.model.EntidadeEmpregadora;
import br.com.alugueldecarros.domain.model.Usuario;
import br.com.alugueldecarros.domain.repository.EntidadeEmpregadoraRepository;
import br.com.alugueldecarros.domain.repository.UsuarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

@ApplicationScoped
public class AutenticacaoService {

    @Inject
    UsuarioRepository usuarioRepository;

    @Inject
    EntidadeEmpregadoraRepository entidadeEmpregadoraRepository;

    public Cliente registrarCliente(AuthDtos.RegisterClientRequest request) {
        String nome = textoObrigatorio(request.nome(), "Nome");
        String email = normalizarEmail(request.email());
        String senha = textoObrigatorio(request.senha(), "Senha");
        String cpf = validarCpf(request.cpf());
        String rg = validarRg(request.rg());
        String endereco = textoObrigatorio(request.endereco(), "Endereco");
        String profissao = textoObrigatorio(request.profissao(), "Profissao");

        usuarioRepository.findByEmail(email)
                .ifPresent(usuario -> {
                    throw new BusinessException("Ja existe usuario cadastrado com este e-mail.");
                });
        usuarioRepository.findClienteByCpf(cpf)
                .ifPresent(cliente -> {
                    throw new BusinessException("Ja existe cliente cadastrado com este CPF.");
                });
        usuarioRepository.findClienteByRg(rg)
                .ifPresent(cliente -> {
                    throw new BusinessException("Ja existe cliente cadastrado com este RG.");
                });

        Cliente cliente = new Cliente(
                nome,
                email,
                senha,
                cpf,
                rg,
                endereco,
                profissao
        );

        List<AuthDtos.EmpregoRequest> empregosRequest = request.empregos() == null ? List.of() : request.empregos();
        if (empregosRequest.isEmpty()) {
            throw new BusinessException("O cliente precisa informar ao menos um emprego.");
        }

        empregosRequest.forEach(empregoRequest -> {
            String nomeEntidade = textoObrigatorio(empregoRequest.nomeEntidadeEmpregadora(), "Empresa empregadora");
            String cnpj = validarCnpj(empregoRequest.cnpj());
            BigDecimal rendimento = validarRendimento(empregoRequest.rendimento());

            EntidadeEmpregadora entidade = entidadeEmpregadoraRepository.findByCnpj(cnpj)
                    .orElseGet(() -> entidadeEmpregadoraRepository.save(
                            new EntidadeEmpregadora(nomeEntidade, cnpj)
                    ));

            cliente.getEmpregos().add(new Emprego(
                    entidade.getId(),
                    entidade.getNome(),
                    rendimento
            ));
        });

        return (Cliente) usuarioRepository.save(cliente);
    }

    public Usuario login(AuthDtos.LoginRequest request) {
        String email = normalizarEmail(request.email());
        String senha = textoObrigatorio(request.senha(), "Senha");

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Usuario nao encontrado para o e-mail informado."));

        if (!usuario.login(email, senha)) {
            throw new BusinessException("Credenciais invalidas.");
        }

        return usuario;
    }

    private String normalizarEmail(String email) {
        String normalizado = textoObrigatorio(email, "E-mail").toLowerCase(Locale.ROOT);
        if (!normalizado.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new BusinessException("Informe um e-mail valido.");
        }
        return normalizado;
    }

    private String validarCpf(String cpf) {
        validarFormatoPermitido(cpf, "CPF");
        String somenteDigitos = somenteDigitos(cpf);
        if (somenteDigitos.length() != 11) {
            throw new BusinessException("CPF deve conter 11 numeros.");
        }
        return somenteDigitos;
    }

    private String validarRg(String rg) {
        validarFormatoPermitido(rg, "RG");
        String somenteDigitos = somenteDigitos(rg);
        if (somenteDigitos.length() < 7 || somenteDigitos.length() > 14) {
            throw new BusinessException("RG deve conter entre 7 e 14 numeros.");
        }
        return somenteDigitos;
    }

    private String validarCnpj(String cnpj) {
        validarFormatoPermitido(cnpj, "CNPJ");
        String somenteDigitos = somenteDigitos(cnpj);
        if (somenteDigitos.length() != 14) {
            throw new BusinessException("CNPJ deve conter 14 numeros.");
        }
        return somenteDigitos;
    }

    private BigDecimal validarRendimento(BigDecimal rendimento) {
        if (rendimento == null || rendimento.signum() <= 0) {
            throw new BusinessException("Informe um rendimento valido.");
        }
        return rendimento;
    }

    private void validarFormatoPermitido(String valor, String campo) {
        String texto = textoObrigatorio(valor, campo);
        if (!texto.matches("[0-9.\\-\\/ ]+")) {
            throw new BusinessException(campo + " deve conter apenas numeros.");
        }
    }

    private String somenteDigitos(String valor) {
        return valor.replaceAll("\\D", "");
    }

    private String textoObrigatorio(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new BusinessException(campo + " e obrigatorio.");
        }
        return valor.trim();
    }
}
