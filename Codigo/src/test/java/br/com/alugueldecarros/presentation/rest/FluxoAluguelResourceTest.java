package br.com.alugueldecarros.presentation.rest;

import br.com.alugueldecarros.domain.model.StatusAutomovel;
import br.com.alugueldecarros.domain.model.StatusContrato;
import br.com.alugueldecarros.domain.repository.AutomovelRepository;
import br.com.alugueldecarros.domain.repository.ContratoRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.config.RedirectConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
class FluxoAluguelResourceTest {

    @Inject
    ContratoRepository contratoRepository;

    @Inject
    AutomovelRepository automovelRepository;

    @Test
    void deveRejeitarCadastroComCpfInvalido() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "nome": "Joao Teste",
                          "email": "joao.teste@cliente.com",
                          "senha": "123456",
                          "cpf": "123A5678910",
                          "rg": "123456789",
                          "endereco": "Rua A, 100",
                          "profissao": "Analista",
                          "empregos": [
                            {
                              "nomeEntidadeEmpregadora": "Empresa Teste",
                              "cnpj": "12345678000199",
                              "rendimento": 4500.00
                            }
                          ]
                        }
                        """)
                .when()
                .post("/api/auth/register-client")
                .then()
                .statusCode(400)
                .body("mensagem", equalTo("CPF deve conter apenas numeros."));
    }

    @Test
    void deveCadastrarClienteEPermitirLogin() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "nome": "Carlos Souza",
                          "email": "carlos.souza@cliente.com",
                          "senha": "123456",
                          "cpf": "98765432100",
                          "rg": "987654321",
                          "endereco": "Rua B, 200",
                          "profissao": "Nao informado",
                          "empregos": [
                            {
                              "nomeEntidadeEmpregadora": "Renda principal",
                              "cnpj": "00000000000000",
                              "rendimento": 3200.00
                            }
                          ]
                        }
                        """)
                .when()
                .post("/api/auth/register-client")
                .then()
                .statusCode(201)
                .body("email", equalTo("carlos.souza@cliente.com"));

        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "email": "carlos.souza@cliente.com",
                          "senha": "123456"
                        }
                        """)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .body("email", equalTo("carlos.souza@cliente.com"))
                .body("perfil", equalTo("Cliente"));
    }

    @Test
    void loginNaUiDeveAtualizarClienteAtual() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "nome": "Ana Costa",
                          "email": "ana.costa@cliente.com",
                          "senha": "123456",
                          "cpf": "11122233344",
                          "rg": "123123123",
                          "endereco": "Rua C, 300",
                          "profissao": "Nao informado",
                          "empregos": [
                            {
                              "nomeEntidadeEmpregadora": "Renda principal",
                              "cnpj": "00000000000000",
                              "rendimento": 2800.00
                            }
                          ]
                        }
                        """)
                .when()
                .post("/api/auth/register-client")
                .then()
                .statusCode(201);

        String clienteCookie = given()
                .config(RestAssuredConfig.config().redirect(RedirectConfig.redirectConfig().followRedirects(false)))
                .contentType(ContentType.URLENC)
                .formParam("clienteId", 3)
                .formParam("email", "ana.costa@cliente.com")
                .formParam("senha", "123456")
                .when()
                .post("/app/login")
                .then()
                .statusCode(303)
                .extract()
                .cookie("clienteAtualId");

        given()
                .cookie("clienteAtualId", clienteCookie)
                .when()
                .get("/app")
                .then()
                .statusCode(200)
                .body(org.hamcrest.Matchers.containsString("Ana Costa"));
    }

    @Test
    void deveExecutarFluxoCompletoDeAluguelComCredito() {
        int pedidoId = given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "automovelId": 1,
                          "dataInicio": "%s",
                          "dataFim": "%s",
                          "justificativa": "Viagem a trabalho",
                          "requerCredito": true
                        }
                        """.formatted(LocalDate.now().plusDays(2), LocalDate.now().plusDays(7)))
                .when()
                .post("/api/clientes/3/pedidos")
                .then()
                .statusCode(201)
                .body("status", equalTo("SUBMETIDO"))
                .body("valorEstimado", notNullValue())
                .extract()
                .path("id");

        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "pedidoId": %d,
                          "aprovado": true,
                          "parecer": "Cliente com perfil adequado"
                        }
                        """.formatted(pedidoId))
                .when()
                .post("/api/agentes/1/avaliacoes")
                .then()
                .statusCode(200)
                .body("status", equalTo("AGUARDANDO_CREDITO"));

        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "pedidoId": %d,
                          "valor": 900.00,
                          "taxaJuros": 0.10,
                          "quantidadeParcelas": 6
                        }
                        """.formatted(pedidoId))
                .when()
                .post("/api/agentes/2/creditos")
                .then()
                .statusCode(200)
                .body("pedidoId", equalTo(pedidoId))
                .body("parcelaMensal", notNullValue());

        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "pedidoId": %d,
                          "tipoContrato": "ALUGUEL_MENSAL"
                        }
                        """.formatted(pedidoId))
                .when()
                .post("/api/contratos")
                .then()
                .statusCode(201)
                .body("pedidoId", equalTo(pedidoId))
                .body("ativo", equalTo(true));
    }

    @Test
    void deveCadastrarFiltrarERemoverAutomovelSemDuplicidade() {
        String sufixo = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String matricula = "FROTA-" + sufixo;
        String placa = "ZZ" + sufixo.substring(0, 1) + "1" + sufixo.substring(1, 2) + "2" + sufixo.substring(2, 3);

        int automovelId = cadastrarAutomovel(matricula, placa, "Nissan", "Kicks", 2025, "220.00");

        given()
                .queryParam("somenteDisponiveis", true)
                .queryParam("marca", "Nissan")
                .queryParam("anoMinimo", 2025)
                .when()
                .get("/api/automoveis")
                .then()
                .statusCode(200)
                .body("find { it.id == " + automovelId + " }.placa", equalTo(placa));

        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "matricula": "%s",
                          "ano": 2025,
                          "marca": "Nissan",
                          "modelo": "Sentra",
                          "placa": "%s",
                          "valorDiaria": 210.00
                        }
                        """.formatted("OUTRA-" + sufixo, placa))
                .when()
                .post("/api/automoveis/empresa/1")
                .then()
                .statusCode(400)
                .body("mensagem", equalTo("Ja existe automovel com esta placa."));

        given()
                .when()
                .delete("/api/automoveis/{automovelId}", automovelId)
                .then()
                .statusCode(204);
    }

    @Test
    void deveNotificarProrrogarGerarPdfEFinalizarContrato() {
        String sufixo = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        int automovelId = cadastrarAutomovel("CONTR-" + sufixo, "QW" + sufixo.substring(0, 1) + "1E2", "Honda", "City", 2026, "230.00");

        LocalDate inicio = LocalDate.now().plusDays(8);
        LocalDate fim = LocalDate.now().plusDays(12);
        int pedidoId = criarPedido(3, automovelId, inicio, fim, false, "Viagem curta");

        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "pedidoId": %d,
                          "aprovado": true,
                          "parecer": "Pedido aprovado"
                        }
                        """.formatted(pedidoId))
                .when()
                .post("/api/agentes/1/avaliacoes")
                .then()
                .statusCode(200)
                .body("status", equalTo("APROVADO"));

        given()
                .when()
                .get("/api/clientes/3/notificacoes/nao-lidas")
                .then()
                .statusCode(200)
                .body(containsString("Pedido " + pedidoId))
                .body(containsString("APROVADO"));

        int contratoId = given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "pedidoId": %d,
                          "tipoContrato": "ALUGUEL_MENSAL"
                        }
                        """.formatted(pedidoId))
                .when()
                .post("/api/contratos")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        given()
                .when()
                .get("/api/contratos/{contratoId}/pdf", contratoId)
                .then()
                .statusCode(200)
                .contentType("application/pdf");

        LocalDate novaData = LocalDate.now().plusDays(16);
        int pedidoProrrogacaoId = given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "contratoId": %d,
                          "novaDataFim": "%s",
                          "justificativa": "Viagem prorrogada"
                        }
                        """.formatted(contratoId, novaData))
                .when()
                .post("/api/clientes/3/prorrogacoes")
                .then()
                .statusCode(201)
                .body("status", equalTo("PRORROGACAO_SOLICITADA"))
                .extract()
                .path("id");

        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "pedidoId": %d,
                          "aprovado": true,
                          "parecer": "Prorrogacao aprovada"
                        }
                        """.formatted(pedidoProrrogacaoId))
                .when()
                .post("/api/agentes/1/avaliacoes")
                .then()
                .statusCode(200)
                .body("status", equalTo("PRORROGACAO_APROVADA"));

        org.junit.jupiter.api.Assertions.assertEquals(novaData, contratoRepository.findById((long) contratoId).orElseThrow().getDataFim());

        given()
                .when()
                .delete("/api/automoveis/{automovelId}", automovelId)
                .then()
                .statusCode(400)
                .body("mensagem", equalTo("Nao e possivel excluir um automovel vinculado a contrato ativo."));

        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "contratoId": %d,
                          "quilometragemFinal": 15432,
                          "avarias": "Sem avarias"
                        }
                        """.formatted(contratoId))
                .when()
                .post("/api/contratos/devolucoes")
                .then()
                .statusCode(200)
                .body("status", equalTo("FINALIZADO"));

        org.junit.jupiter.api.Assertions.assertEquals(StatusContrato.FINALIZADO, contratoRepository.findById((long) contratoId).orElseThrow().getStatus());
        org.junit.jupiter.api.Assertions.assertEquals(StatusAutomovel.DISPONIVEL, automovelRepository.findById((long) automovelId).orElseThrow().getStatus());

        given()
                .when()
                .get("/api/contratos/{contratoId}/pdf", contratoId)
                .then()
                .statusCode(400)
                .body("mensagem", equalTo("O PDF so pode ser gerado para contratos em andamento."));
    }

    @Test
    void deveExibirResumoMensalDoAgente() {
        String sufixo = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        int automovelId = cadastrarAutomovel("RES-" + sufixo, "RT" + sufixo.substring(0, 1) + "1Y2", "Renault", "Duster", 2026, "210.00");
        criarPedido(3, automovelId, LocalDate.now().plusDays(20), LocalDate.now().plusDays(24), false, "Resumo mensal");

        given()
                .when()
                .get("/api/agentes/resumo-mensal")
                .then()
                .statusCode(200)
                .body("pendentes", greaterThanOrEqualTo(1))
                .body("aprovados", greaterThanOrEqualTo(0))
                .body("rejeitados", greaterThanOrEqualTo(0));
    }

    private int cadastrarAutomovel(String matricula, String placa, String marca, String modelo, int ano, String valorDiaria) {
        return given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "matricula": "%s",
                          "ano": %d,
                          "marca": "%s",
                          "modelo": "%s",
                          "placa": "%s",
                          "valorDiaria": %s
                        }
                        """.formatted(matricula, ano, marca, modelo, placa, valorDiaria))
                .when()
                .post("/api/automoveis/empresa/1")
                .then()
                .statusCode(201)
                .extract()
                .path("id");
    }

    private int criarPedido(int clienteId, int automovelId, LocalDate inicio, LocalDate fim, boolean requerCredito, String justificativa) {
        return given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "automovelId": %d,
                          "dataInicio": "%s",
                          "dataFim": "%s",
                          "justificativa": "%s",
                          "requerCredito": %s
                        }
                        """.formatted(automovelId, inicio, fim, justificativa, requerCredito))
                .when()
                .post("/api/clientes/{clienteId}/pedidos", clienteId)
                .then()
                .statusCode(201)
                .extract()
                .path("id");
    }
}
