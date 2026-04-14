package br.com.alugueldecarros.presentation.rest;

import br.com.alugueldecarros.application.dto.AnaliseDtos;
import br.com.alugueldecarros.application.dto.AuthDtos;
import br.com.alugueldecarros.application.dto.CatalogoDtos;
import br.com.alugueldecarros.application.dto.ContratoDtos;
import br.com.alugueldecarros.application.dto.NotificacaoDtos;
import br.com.alugueldecarros.application.dto.PedidoDtos;
import br.com.alugueldecarros.application.facade.AnaliseFacade;
import br.com.alugueldecarros.application.facade.AutenticacaoFacade;
import br.com.alugueldecarros.application.facade.CatalogoFacade;
import br.com.alugueldecarros.application.facade.ContratoFacade;
import br.com.alugueldecarros.application.facade.NotificacaoFacade;
import br.com.alugueldecarros.application.facade.PedidoFacade;
import br.com.alugueldecarros.application.mapper.ApiMapper;
import br.com.alugueldecarros.domain.model.StatusAutomovel;
import br.com.alugueldecarros.domain.model.StatusContrato;
import br.com.alugueldecarros.domain.model.StatusPedido;
import br.com.alugueldecarros.domain.model.TipoAgente;
import br.com.alugueldecarros.domain.model.TipoPedido;
import br.com.alugueldecarros.domain.repository.ContratoCreditoRepository;
import br.com.alugueldecarros.domain.repository.ContratoRepository;
import br.com.alugueldecarros.domain.repository.PedidoAluguelRepository;
import br.com.alugueldecarros.domain.repository.UsuarioRepository;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import org.jboss.resteasy.reactive.RestForm;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Path("/app")
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Produces(MediaType.TEXT_HTML)
public class AppResource {

    private static final long CLIENTE_PADRAO = 3L;
    private static final String COOKIE_CLIENTE_ATUAL = "clienteAtualId";

    @Inject
    UsuarioRepository usuarioRepository;

    @Inject
    PedidoAluguelRepository pedidoRepository;

    @Inject
    ContratoRepository contratoRepository;

    @Inject
    ContratoCreditoRepository contratoCreditoRepository;

    @Inject
    CatalogoFacade catalogoFacade;

    @Inject
    PedidoFacade pedidoFacade;

    @Inject
    AutenticacaoFacade autenticacaoFacade;

    @Inject
    AnaliseFacade analiseFacade;

    @Inject
    ContratoFacade contratoFacade;

    @Inject
    NotificacaoFacade notificacaoFacade;

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance dashboard(DashboardView view);
    }

    @GET
    public TemplateInstance index(
            @QueryParam("clienteId") Long clienteId,
            @CookieParam(COOKIE_CLIENTE_ATUAL) String clienteIdCookie,
            @QueryParam("success") String success,
            @QueryParam("error") String error,
            @QueryParam("marca") String marca,
            @QueryParam("anoMinimo") Integer anoMinimo,
            @QueryParam("automovelId") Long automovelId
    ) {
        long clienteSelecionado = resolverClienteSelecionado(clienteId, clienteIdCookie);

        return Templates.dashboard(new DashboardView(
                success,
                error,
                clienteSelecionado,
                automovelId,
                marca,
                anoMinimo,
                usuarioRepository.listClientes().stream().map(ApiMapper::toClienteResponse).toList(),
                usuarioRepository.listAgentes().stream().map(ApiMapper::toAgenteResponse).toList(),
                catalogoFacade.listarTodos(),
                catalogoFacade.listarDisponiveis(marca, anoMinimo),
                pedidoFacade.listar(clienteSelecionado),
                pedidoRepository.listAll().stream().map(ApiMapper::toPedidoResponse).toList(),
                contratoRepository.listAll().stream().map(ApiMapper::toContratoResponse).toList(),
                contratoCreditoRepository.listAll().stream().map(ApiMapper::toCreditoResponse).toList(),
                notificacaoFacade.listarHistorico(clienteSelecionado),
                analiseFacade.resumoMensalAtual()
        ));
    }

    @POST
    @Path("/selecionar")
    public Response selecionarCliente(@BeanParam ClienteSelecionadoForm form) {
        return redirectComMensagem(valorOuPadrao(form.clienteId), null, null);
    }

    @POST
    @Path("/pedidos")
    public Response criarPedido(@BeanParam PedidoForm form) {
        try {
            pedidoFacade.criar(form.clienteId, new PedidoDtos.CriarPedidoRequest(
                    form.automovelId,
                    form.dataInicio,
                    form.dataFim,
                    form.justificativa,
                    form.requerCredito != null
            ));
            return redirectComMensagem(form.clienteId, "Pedido criado.", null);
        } catch (Exception exception) {
            return redirectComMensagem(form.clienteId, null, extrairMensagem(exception));
        }
    }

    @POST
    @Path("/pedidos/atualizar")
    public Response atualizarPedido(@BeanParam PedidoUpdateForm form) {
        try {
            pedidoFacade.atualizar(form.clienteId, form.pedidoId, new PedidoDtos.AtualizarPedidoRequest(
                    form.automovelId,
                    form.dataInicio,
                    form.dataFim,
                    form.justificativa,
                    form.requerCredito != null
            ));
            return redirectComMensagem(form.clienteId, "Pedido atualizado.", null);
        } catch (Exception exception) {
            return redirectComMensagem(form.clienteId, null, extrairMensagem(exception));
        }
    }

    @POST
    @Path("/pedidos/cancelar")
    public Response cancelarPedido(@BeanParam PedidoCancelForm form) {
        try {
            pedidoFacade.cancelar(form.clienteId, form.pedidoId);
            return redirectComMensagem(form.clienteId, "Pedido cancelado.", null);
        } catch (Exception exception) {
            return redirectComMensagem(form.clienteId, null, extrairMensagem(exception));
        }
    }

    @POST
    @Path("/pedidos/prorrogacao")
    public Response solicitarProrrogacao(@BeanParam ProrrogacaoForm form) {
        try {
            pedidoFacade.solicitarProrrogacao(form.clienteId, new PedidoDtos.SolicitarProrrogacaoRequest(
                    form.contratoId,
                    form.novaDataFim,
                    form.justificativa
            ));
            return redirectComMensagem(form.clienteId, "Prorrogacao enviada para analise.", null);
        } catch (Exception exception) {
            return redirectComMensagem(form.clienteId, null, extrairMensagem(exception));
        }
    }

    @POST
    @Path("/clientes/cadastro")
    public Response cadastrarCliente(@BeanParam RegisterForm form) {
        try {
            AuthDtos.ClienteResponse cliente = autenticacaoFacade.registrarCliente(new AuthDtos.RegisterClientRequest(
                    form.nome,
                    form.email,
                    form.senha,
                    form.cpf,
                    form.rg,
                    form.endereco,
                    "Nao informado",
                    List.of(new AuthDtos.EmpregoRequest(
                            "Renda principal",
                            "00000000000000",
                            form.rendimento
                    ))
            ));
            return redirectComMensagem(cliente.id(), "Cliente cadastrado.", null);
        } catch (Exception exception) {
            return redirectComMensagem(form.clienteId != null ? form.clienteId : CLIENTE_PADRAO, null, extrairMensagem(exception));
        }
    }

    @POST
    @Path("/login")
    public Response login(@BeanParam LoginForm form) {
        try {
            AuthDtos.UsuarioResponse usuario = autenticacaoFacade.login(new AuthDtos.LoginRequest(form.email, form.senha));
            Long clienteId = "Cliente".equalsIgnoreCase(usuario.perfil()) ? usuario.id() : valorOuPadrao(form.clienteId);
            return redirectComMensagem(clienteId, "Login realizado para " + usuario.nome() + ".", null);
        } catch (Exception exception) {
            return redirectComMensagem(valorOuPadrao(form.clienteId), null, extrairMensagem(exception));
        }
    }

    @POST
    @Path("/avaliacoes")
    public Response avaliarPedido(@BeanParam AvaliacaoForm form) {
        try {
            analiseFacade.avaliarPedido(form.agenteId, new AnaliseDtos.AvaliarPedidoRequest(
                    form.pedidoId,
                    form.aprovado != null,
                    form.parecer
            ));
            return redirectComMensagem(form.clienteId, "Analise salva.", null);
        } catch (Exception exception) {
            return redirectComMensagem(form.clienteId, null, extrairMensagem(exception));
        }
    }

    @POST
    @Path("/creditos")
    public Response concederCredito(@BeanParam CreditoForm form) {
        try {
            analiseFacade.concederCredito(form.agenteId, new AnaliseDtos.ConcederCreditoRequest(
                    form.pedidoId,
                    form.valor,
                    form.taxaJuros,
                    form.quantidadeParcelas
            ));
            return redirectComMensagem(form.clienteId, "Credito aprovado.", null);
        } catch (Exception exception) {
            return redirectComMensagem(form.clienteId, null, extrairMensagem(exception));
        }
    }

    @POST
    @Path("/contratos")
    public Response criarContrato(@BeanParam ContratoForm form) {
        try {
            contratoFacade.criarContrato(new ContratoDtos.CriarContratoRequest(form.pedidoId, form.tipoContrato));
            return redirectComMensagem(form.clienteId, "Contrato gerado.", null);
        } catch (Exception exception) {
            return redirectComMensagem(form.clienteId, null, extrairMensagem(exception));
        }
    }

    @POST
    @Path("/contratos/devolucao")
    public Response devolverContrato(@BeanParam DevolucaoForm form) {
        try {
            contratoFacade.devolver(new ContratoDtos.DevolverContratoRequest(
                    form.contratoId,
                    form.quilometragemFinal,
                    form.avarias
            ));
            return redirectComMensagem(form.clienteId, "Devolucao registrada.", null);
        } catch (Exception exception) {
            return redirectComMensagem(form.clienteId, null, extrairMensagem(exception));
        }
    }

    @POST
    @Path("/frota/cadastrar")
    public Response cadastrarAutomovel(@BeanParam AutomovelCreateForm form) {
        try {
            catalogoFacade.cadastrar(form.empresaId, new CatalogoDtos.CriarAutomovelRequest(
                    form.matricula,
                    form.ano,
                    form.marca,
                    form.modelo,
                    form.placa,
                    form.valorDiaria
            ));
            return redirectComMensagem(form.clienteId, "Automovel cadastrado.", null);
        } catch (Exception exception) {
            return redirectComMensagem(form.clienteId, null, extrairMensagem(exception));
        }
    }

    @POST
    @Path("/frota/atualizar")
    public Response atualizarAutomovel(@BeanParam AutomovelUpdateForm form) {
        try {
            catalogoFacade.atualizar(form.automovelId, new CatalogoDtos.AtualizarAutomovelRequest(
                    form.matricula,
                    form.ano,
                    form.marca,
                    form.modelo,
                    form.placa,
                    form.valorDiaria,
                    StatusAutomovel.valueOf(form.status)
            ));
            return redirectComMensagem(form.clienteId, "Automovel atualizado.", null);
        } catch (Exception exception) {
            return redirectComMensagem(form.clienteId, null, extrairMensagem(exception));
        }
    }

    @POST
    @Path("/frota/remover")
    public Response removerAutomovel(@BeanParam AutomovelRemoveForm form) {
        try {
            catalogoFacade.remover(form.automovelId);
            return redirectComMensagem(form.clienteId, "Automovel removido.", null);
        } catch (Exception exception) {
            return redirectComMensagem(form.clienteId, null, extrairMensagem(exception));
        }
    }

    private Response redirectComMensagem(Long clienteId, String success, String error) {
        UriBuilder uriBuilder = UriBuilder.fromPath("/app")
                .queryParam("clienteId", clienteId);
        if (success != null) {
            uriBuilder.queryParam("success", success);
        }
        if (error != null) {
            uriBuilder.queryParam("error", error);
        }
        URI uri = uriBuilder.build();
        return Response.seeOther(uri)
                .cookie(criarCookieCliente(clienteId))
                .build();
    }

    private String extrairMensagem(Exception exception) {
        if (exception.getCause() instanceof RuntimeException runtimeException && runtimeException.getMessage() != null) {
            return runtimeException.getMessage();
        }
        if (exception instanceof RuntimeException runtimeException && runtimeException.getMessage() != null) {
            return runtimeException.getMessage();
        }
        return "Nao foi possivel concluir a operacao.";
    }

    private Long valorOuPadrao(Long clienteId) {
        return clienteId != null ? clienteId : CLIENTE_PADRAO;
    }

    private long resolverClienteSelecionado(Long clienteId, String clienteIdCookie) {
        if (clienteId != null) {
            return clienteId;
        }
        if (clienteIdCookie != null && clienteIdCookie.matches("\\d+")) {
<<<<<<< HEAD
            long idDoCookie = Long.parseLong(clienteIdCookie);
            if (usuarioRepository.findClienteById(idDoCookie).isPresent()) {
                return idDoCookie;
            }
=======
            return Long.parseLong(clienteIdCookie);
>>>>>>> d798b9dba2bddcc36cbb13e793e8f279a2b1221e
        }
        return CLIENTE_PADRAO;
    }

    private NewCookie criarCookieCliente(Long clienteId) {
        return new NewCookie.Builder(COOKIE_CLIENTE_ATUAL)
                .value(String.valueOf(valorOuPadrao(clienteId)))
                .path("/")
                .httpOnly(false)
                .build();
    }

    public static class ClienteSelecionadoForm {
        @RestForm
        public Long clienteId;
    }

    public static class PedidoForm {
        @RestForm
        public Long clienteId;
        @RestForm
        public Long automovelId;
        @RestForm
        public LocalDate dataInicio;
        @RestForm
        public LocalDate dataFim;
        @RestForm
        public String justificativa;
        @RestForm
        public String requerCredito;
    }

    public static class PedidoUpdateForm {
        @RestForm
        public Long clienteId;
        @RestForm
        public Long pedidoId;
        @RestForm
        public Long automovelId;
        @RestForm
        public LocalDate dataInicio;
        @RestForm
        public LocalDate dataFim;
        @RestForm
        public String justificativa;
        @RestForm
        public String requerCredito;
    }

    public static class PedidoCancelForm {
        @RestForm
        public Long clienteId;
        @RestForm
        public Long pedidoId;
    }

    public static class ProrrogacaoForm {
        @RestForm
        public Long clienteId;
        @RestForm
        public Long contratoId;
        @RestForm
        public LocalDate novaDataFim;
        @RestForm
        public String justificativa;
    }

    public static class LoginForm {
        @RestForm
        public Long clienteId;
        @RestForm
        public String email;
        @RestForm
        public String senha;
    }

    public static class RegisterForm {
        @RestForm
        public Long clienteId;
        @RestForm
        public String nome;
        @RestForm
        public String email;
        @RestForm
        public String senha;
        @RestForm
        public String cpf;
        @RestForm
        public String rg;
        @RestForm
        public String endereco;
        @RestForm
        public BigDecimal rendimento;
    }

    public static class AvaliacaoForm {
        @RestForm
        public Long clienteId;
        @RestForm
        public Long agenteId;
        @RestForm
        public Long pedidoId;
        @RestForm
        public String parecer;
        @RestForm
        public String aprovado;
    }

    public static class CreditoForm {
        @RestForm
        public Long clienteId;
        @RestForm
        public Long agenteId;
        @RestForm
        public Long pedidoId;
        @RestForm
        public BigDecimal valor;
        @RestForm
        public BigDecimal taxaJuros;
        @RestForm
        public BigDecimal quantidadeParcelas;
    }

    public static class ContratoForm {
        @RestForm
        public Long clienteId;
        @RestForm
        public Long pedidoId;
        @RestForm
        public String tipoContrato;
    }

    public static class DevolucaoForm {
        @RestForm
        public Long clienteId;
        @RestForm
        public Long contratoId;
        @RestForm
        public Integer quilometragemFinal;
        @RestForm
        public String avarias;
    }

    public static class AutomovelCreateForm {
        @RestForm
        public Long clienteId;
        @RestForm
        public Long empresaId;
        @RestForm
        public String matricula;
        @RestForm
        public Integer ano;
        @RestForm
        public String marca;
        @RestForm
        public String modelo;
        @RestForm
        public String placa;
        @RestForm
        public BigDecimal valorDiaria;
    }

    public static class AutomovelUpdateForm {
        @RestForm
        public Long clienteId;
        @RestForm
        public Long automovelId;
        @RestForm
        public String matricula;
        @RestForm
        public Integer ano;
        @RestForm
        public String marca;
        @RestForm
        public String modelo;
        @RestForm
        public String placa;
        @RestForm
        public BigDecimal valorDiaria;
        @RestForm
        public String status;
    }

    public static class AutomovelRemoveForm {
        @RestForm
        public Long clienteId;
        @RestForm
        public Long automovelId;
    }

    public record DashboardView(
            String success,
            String error,
            Long clienteSelecionado,
            Long automovelSelecionado,
            String marcaFiltro,
            Integer anoMinimoFiltro,
            List<AuthDtos.ClienteResponse> clientes,
            List<AuthDtos.AgenteResponse> agentes,
            List<CatalogoDtos.AutomovelResponse> automoveis,
            List<CatalogoDtos.AutomovelResponse> catalogoDisponivel,
            List<PedidoDtos.PedidoResponse> pedidosCliente,
            List<PedidoDtos.PedidoResponse> todosPedidos,
            List<ContratoDtos.ContratoResponse> contratos,
            List<AnaliseDtos.CreditoResponse> creditos,
            List<NotificacaoDtos.NotificacaoResponse> notificacoes,
            AnaliseDtos.ResumoMensalResponse resumoMensal
    ) {

        public List<AuthDtos.AgenteResponse> empresas() {
            return agentes.stream().filter(agente -> agente.tipo() == TipoAgente.EMPRESA).toList();
        }

        public List<AuthDtos.AgenteResponse> bancos() {
            return agentes.stream().filter(agente -> agente.tipo() == TipoAgente.BANCO).toList();
        }

        public boolean clienteSelecionado(Long clienteId) {
            return this.clienteSelecionado != null && this.clienteSelecionado.equals(clienteId);
        }

        public String nomeClienteSelecionado() {
            return clientes.stream()
                    .filter(cliente -> cliente.id().equals(clienteSelecionado))
                    .map(AuthDtos.ClienteResponse::nome)
                    .findFirst()
                    .orElse("Cliente");
        }

        public String emailClienteSelecionado() {
            return clientes.stream()
                    .filter(cliente -> cliente.id().equals(clienteSelecionado))
                    .map(AuthDtos.ClienteResponse::email)
                    .findFirst()
                    .orElse("-");
        }

        public String credencialInicialCliente() {
            return "maria@cliente.com / 123456";
        }

        public long totalDisponiveis() {
            return automoveis.stream().filter(CatalogoDtos.AutomovelResponse::disponivel).count();
        }

        public long totalPendentesMes() {
            return resumoMensal.pendentes();
        }

        public long totalAprovadosMes() {
            return resumoMensal.aprovados();
        }

        public long totalRejeitadosMes() {
            return resumoMensal.rejeitados();
        }

        public long totalContratosAtivos() {
            return contratos.stream().filter(ContratoDtos.ContratoResponse::ativo).count();
        }

        public long totalNotificacoes() {
            return notificacoes.size();
        }

        public boolean automovelSelecionado(Long automovelId) {
            if (automovelSelecionado != null) {
                return automovelSelecionado.equals(automovelId);
            }
            return !catalogoDisponivel.isEmpty() && catalogoDisponivel.getFirst().id().equals(automovelId);
        }

        public Long empresaPrincipalId() {
            return empresas().isEmpty() ? null : empresas().getFirst().id();
        }

        public List<PedidoDtos.PedidoResponse> pedidosClienteOrdenados() {
            return pedidosCliente.stream()
                    .sorted(Comparator.comparing(PedidoDtos.PedidoResponse::id).reversed())
                    .toList();
        }

        public List<ContratoDtos.ContratoResponse> contratosCliente() {
            return contratos.stream()
                    .filter(contrato -> contrato.clienteId().equals(clienteSelecionado))
                    .sorted(Comparator.comparing(ContratoDtos.ContratoResponse::id).reversed())
                    .toList();
        }

        public List<ContratoDtos.ContratoResponse> contratosClienteAtivos() {
            return contratosCliente().stream().filter(ContratoDtos.ContratoResponse::ativo).toList();
        }

        public List<ContratoDtos.ContratoResponse> contratosAtivos() {
            return contratos.stream()
                    .filter(ContratoDtos.ContratoResponse::ativo)
                    .sorted(Comparator.comparing(ContratoDtos.ContratoResponse::id).reversed())
                    .toList();
        }

        public List<PedidoDtos.PedidoResponse> pedidosEditaveis() {
            return pedidosCliente.stream()
                    .filter(pedido -> pedido.tipoPedido() == TipoPedido.ALUGUEL)
                    .filter(pedido -> pedido.status() == StatusPedido.SUBMETIDO
                            || pedido.status() == StatusPedido.AGUARDANDO_CREDITO
                            || pedido.status() == StatusPedido.APROVADO
                            || pedido.status() == StatusPedido.CREDITO_APROVADO)
                    .sorted(Comparator.comparing(PedidoDtos.PedidoResponse::id).reversed())
                    .toList();
        }

        public List<PedidoDtos.PedidoResponse> pedidosParaAvaliacao() {
            return todosPedidos.stream()
                    .filter(pedido -> pedido.tipoPedido() == TipoPedido.ALUGUEL)
                    .filter(pedido -> pedido.status() == StatusPedido.SUBMETIDO)
                    .sorted(Comparator.comparing(PedidoDtos.PedidoResponse::id).reversed())
                    .toList();
        }

        public List<PedidoDtos.PedidoResponse> prorrogacoesPendentes() {
            return todosPedidos.stream()
                    .filter(pedido -> pedido.tipoPedido() == TipoPedido.PRORROGACAO)
                    .filter(pedido -> pedido.status() == StatusPedido.PRORROGACAO_SOLICITADA)
                    .sorted(Comparator.comparing(PedidoDtos.PedidoResponse::id).reversed())
                    .toList();
        }

        public List<PedidoDtos.PedidoResponse> pedidosParaCredito() {
            return todosPedidos.stream()
                    .filter(pedido -> pedido.tipoPedido() == TipoPedido.ALUGUEL)
                    .filter(pedido -> pedido.status() == StatusPedido.AGUARDANDO_CREDITO)
                    .sorted(Comparator.comparing(PedidoDtos.PedidoResponse::id).reversed())
                    .toList();
        }

        public List<PedidoDtos.PedidoResponse> pedidosParaContrato() {
            return todosPedidos.stream()
                    .filter(pedido -> pedido.tipoPedido() == TipoPedido.ALUGUEL)
                    .filter(pedido -> pedido.status() == StatusPedido.APROVADO || pedido.status() == StatusPedido.CREDITO_APROVADO)
                    .sorted(Comparator.comparing(PedidoDtos.PedidoResponse::id).reversed())
                    .toList();
        }

        public String descricaoAutomovel(Long automovelId) {
            return automoveis.stream()
                    .filter(automovel -> automovel.id().equals(automovelId))
                    .map(automovel -> automovel.marca() + " " + automovel.modelo() + " | " + automovel.placa())
                    .findFirst()
                    .orElse("Automovel " + automovelId);
        }

        public String nomeAgente(Long agenteId) {
            if (agenteId == null) {
                return "-";
            }
            return agentes.stream()
                    .filter(agente -> agente.id().equals(agenteId))
                    .map(AuthDtos.AgenteResponse::nome)
                    .findFirst()
                    .orElse("Agente " + agenteId);
        }

        public String rotuloStatusPedido(StatusPedido statusPedido) {
            return switch (statusPedido) {
                case SUBMETIDO -> "Em analise";
                case AGUARDANDO_CREDITO -> "Aguardando credito";
                case APROVADO -> "Aprovado";
                case CREDITO_APROVADO -> "Credito aprovado";
                case REJEITADO -> "Rejeitado";
                case CANCELADO -> "Cancelado";
                case CONTRATADO -> "Em contrato";
                case PRORROGACAO_SOLICITADA -> "Prorrogacao em analise";
                case PRORROGACAO_APROVADA -> "Prorrogacao aprovada";
                case PRORROGACAO_REJEITADA -> "Prorrogacao rejeitada";
<<<<<<< HEAD
                case FINALIZADO -> "Finalizado";
=======
>>>>>>> d798b9dba2bddcc36cbb13e793e8f279a2b1221e
            };
        }

        public String rotuloTipoPedido(TipoPedido tipoPedido) {
            return tipoPedido == TipoPedido.PRORROGACAO ? "Prorrogacao" : "Aluguel";
        }

        public String rotuloStatusAutomovel(StatusAutomovel statusAutomovel) {
            return switch (statusAutomovel) {
                case DISPONIVEL -> "Disponivel";
                case ALUGADO -> "Em contrato";
                case MANUTENCAO -> "Manutencao";
            };
        }

        public String rotuloStatusContrato(StatusContrato statusContrato) {
            return statusContrato == StatusContrato.EM_CONTRATO ? "Em contrato" : "Finalizado";
        }

        public String badgeClasse(StatusPedido statusPedido) {
            return switch (statusPedido) {
<<<<<<< HEAD
                case APROVADO, CREDITO_APROVADO, CONTRATADO, PRORROGACAO_APROVADA, FINALIZADO -> "ok";
=======
                case APROVADO, CREDITO_APROVADO, CONTRATADO, PRORROGACAO_APROVADA -> "ok";
>>>>>>> d798b9dba2bddcc36cbb13e793e8f279a2b1221e
                case REJEITADO, CANCELADO, PRORROGACAO_REJEITADA -> "warn";
                default -> "info";
            };
        }

        public String badgeClasseAutomovel(StatusAutomovel statusAutomovel) {
            return switch (statusAutomovel) {
                case DISPONIVEL -> "ok";
                case ALUGADO -> "info";
                case MANUTENCAO -> "warn";
            };
        }

        public boolean automovelStatusDisponivel(CatalogoDtos.AutomovelResponse automovel) {
            return automovel.status() == StatusAutomovel.DISPONIVEL;
        }

        public boolean automovelStatusAlugado(CatalogoDtos.AutomovelResponse automovel) {
            return automovel.status() == StatusAutomovel.ALUGADO;
        }

        public boolean automovelStatusManutencao(CatalogoDtos.AutomovelResponse automovel) {
            return automovel.status() == StatusAutomovel.MANUTENCAO;
        }

        public String formatarMarcaFiltro() {
            return marcaFiltro == null ? "" : marcaFiltro;
        }

        public String formatarAnoMinimoFiltro() {
            return anoMinimoFiltro == null ? "" : String.valueOf(anoMinimoFiltro);
        }

        public String formatarValor(BigDecimal valor) {
            return valor == null ? "-" : "R$ " + valor.setScale(2).toString().replace('.', ',');
        }

        public String resumoCatalogo() {
            if (formatarMarcaFiltro().isBlank() && formatarAnoMinimoFiltro().isBlank()) {
                return "Mostrando veiculos disponiveis.";
            }
            return "Filtro ativo.";
        }

        public boolean temNotificacoes() {
            return !notificacoes.isEmpty();
        }

        public boolean temCatalogo() {
            return !catalogoDisponivel.isEmpty();
        }

        public String slugStatus(StatusPedido statusPedido) {
            return statusPedido.name().toLowerCase(Locale.ROOT);
        }
<<<<<<< HEAD

        public String classeWsPending(int count) {
            return count > 0 ? "ws-pending" : "";
        }

        public boolean podeCancelar(StatusPedido status) {
            return status == StatusPedido.SUBMETIDO
                    || status == StatusPedido.AGUARDANDO_CREDITO
                    || status == StatusPedido.APROVADO
                    || status == StatusPedido.CREDITO_APROVADO;
        }

        public int passoStatus(StatusPedido status) {
            return switch (status) {
                case SUBMETIDO -> 1;
                case AGUARDANDO_CREDITO, PRORROGACAO_SOLICITADA -> 2;
                case APROVADO, CREDITO_APROVADO, PRORROGACAO_APROVADA -> 3;
                case CONTRATADO -> 4;
                case FINALIZADO -> 5;
                case REJEITADO, CANCELADO, PRORROGACAO_REJEITADA -> 0;
            };
        }

        public String classeStep(StatusPedido status, int step) {
            int passo = passoStatus(status);
            if (passo == 0) {
                if (step == 1) return "track-step done";
                if (step == 2) return "track-step ended";
                return "track-step";
            }
            if (step < passo) return "track-step done";
            if (step == passo) return "track-step active";
            return "track-step";
        }

        public boolean temPedidosCliente() {
            return !pedidosCliente.isEmpty();
        }

        public boolean temPedidosEditaveis() {
            return !pedidosEditaveis().isEmpty();
        }

        public boolean temContratosClienteAtivos() {
            return !contratosClienteAtivos().isEmpty();
        }
=======
>>>>>>> d798b9dba2bddcc36cbb13e793e8f279a2b1221e
    }
}
