package br.com.alugueldecarros.application.mapper;

import br.com.alugueldecarros.application.dto.AnaliseDtos;
import br.com.alugueldecarros.application.dto.AuthDtos;
import br.com.alugueldecarros.application.dto.CatalogoDtos;
import br.com.alugueldecarros.application.dto.ContratoDtos;
import br.com.alugueldecarros.application.dto.NotificacaoDtos;
import br.com.alugueldecarros.application.dto.PedidoDtos;
import br.com.alugueldecarros.domain.model.Agente;
import br.com.alugueldecarros.domain.model.Automovel;
import br.com.alugueldecarros.domain.model.Cliente;
import br.com.alugueldecarros.domain.model.Contrato;
import br.com.alugueldecarros.domain.model.ContratoCredito;
import br.com.alugueldecarros.domain.model.Emprego;
import br.com.alugueldecarros.domain.model.Notificacao;
import br.com.alugueldecarros.domain.model.PedidoAluguel;
import br.com.alugueldecarros.domain.model.Usuario;

public final class ApiMapper {

    private ApiMapper() {
    }

    public static AuthDtos.UsuarioResponse toUsuarioResponse(Usuario usuario) {
        return new AuthDtos.UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getClass().getSimpleName()
        );
    }

    public static AuthDtos.ClienteResponse toClienteResponse(Cliente cliente) {
        return new AuthDtos.ClienteResponse(
                cliente.getId(),
                cliente.getNome(),
                cliente.getEmail(),
                cliente.getCpf(),
                cliente.getProfissao(),
                cliente.getEmpregos().stream().map(ApiMapper::toEmpregoResponse).toList()
        );
    }

    public static AuthDtos.EmpregoResponse toEmpregoResponse(Emprego emprego) {
        return new AuthDtos.EmpregoResponse(
                emprego.getEntidadeEmpregadoraId(),
                emprego.getNomeEntidadeEmpregadora(),
                emprego.getRendimento()
        );
    }

    public static AuthDtos.AgenteResponse toAgenteResponse(Agente agente) {
        return new AuthDtos.AgenteResponse(
                agente.getId(),
                agente.getNome(),
                agente.getEmail(),
                agente.getTipo()
        );
    }

    public static CatalogoDtos.AutomovelResponse toAutomovelResponse(Automovel automovel) {
        return new CatalogoDtos.AutomovelResponse(
                automovel.getId(),
                automovel.getMatricula(),
                automovel.getAno(),
                automovel.getMarca(),
                automovel.getModelo(),
                automovel.getPlaca(),
                automovel.getProprietarioId(),
                automovel.getValorDiaria(),
                automovel.getStatus(),
                automovel.isDisponivel()
        );
    }

    public static PedidoDtos.PedidoResponse toPedidoResponse(PedidoAluguel pedido) {
        return new PedidoDtos.PedidoResponse(
                pedido.getId(),
                pedido.getDataCriacao(),
                pedido.getStatus(),
                pedido.getTipoPedido(),
                pedido.getClienteId(),
                pedido.getAutomovelId(),
                pedido.getDataInicio(),
                pedido.getDataFim(),
                pedido.getJustificativa(),
                pedido.isRequerCredito(),
                pedido.getValorEstimado(),
                pedido.getAgenteResponsavelId(),
                pedido.getParecerAgente(),
                pedido.getContratoCreditoId(),
                pedido.getContratoId(),
                pedido.getContratoOrigemId()
        );
    }

    public static AnaliseDtos.CreditoResponse toCreditoResponse(ContratoCredito credito) {
        return new AnaliseDtos.CreditoResponse(
                credito.getId(),
                credito.getPedidoId(),
                credito.getContratoId(),
                credito.getBancoId(),
                credito.getValor(),
                credito.getTaxaJuros(),
                credito.getParcelaMensal()
        );
    }

    public static ContratoDtos.ContratoResponse toContratoResponse(Contrato contrato) {
        return new ContratoDtos.ContratoResponse(
                contrato.getId(),
                contrato.getPedidoId(),
                contrato.getClienteId(),
                contrato.getAutomovelId(),
                contrato.getDataInicio(),
                contrato.getDataFim(),
                contrato.getDataAssinatura(),
                contrato.getTipo(),
                contrato.getStatus(),
                contrato.isAtivo(),
                contrato.getDataDevolucao(),
                contrato.getQuilometragemFinal(),
                contrato.getAvarias()
        );
    }

    public static NotificacaoDtos.NotificacaoResponse toNotificacaoResponse(Notificacao notificacao) {
        return new NotificacaoDtos.NotificacaoResponse(
                notificacao.getId(),
                notificacao.getTitulo(),
                notificacao.getMensagem(),
                notificacao.getDataCriacao()
        );
    }
}
