# Aluguel De Carros

Backend do **Lab02S02 - Sistema de Aluguel de Carros** desenvolvido com **Java + Quarkus**.

## Arquitetura

O projeto foi organizado em camadas:

- `domain`: entidades, enums e excecoes de negocio.
- `repository`: persistencia em memoria com repositorios `@ApplicationScoped`.
- `application.service`: regras de negocio.
- `application.facade`: orchestration dos casos de uso.
- `application.dto` e `application.mapper`: contratos de entrada e saida da API.
- `presentation.rest`: endpoints REST.

## Como executar

1. Ter Java 21+ e Maven instalados.
2. Na pasta do projeto, executar:

```bash
mvn quarkus:dev
```

3. A API ficara disponivel em `http://localhost:8080`.
4. Swagger UI: `http://localhost:8080/q/swagger-ui`
5. Frontend Qute: `http://localhost:8080/app`

## Endpoints principais

- `POST /api/auth/register-client`
- `POST /api/auth/login`
- `GET /api/clientes`
- `POST /api/clientes/{clienteId}/pedidos`
- `PUT /api/clientes/{clienteId}/pedidos/{pedidoId}`
- `PATCH /api/clientes/{clienteId}/pedidos/{pedidoId}/cancelamento`
- `GET /api/clientes/{clienteId}/pedidos`
- `GET /api/agentes`
- `POST /api/agentes/{agenteId}/avaliacoes`
- `POST /api/agentes/{agenteId}/creditos`
- `POST /api/contratos`
- `GET /api/automoveis`

## Dados iniciais

A aplicacao sobe com dados de exemplo:

- 1 empresa locadora
- 1 banco
- 1 cliente
- 3 automoveis

Isso facilita testar o fluxo completo de pedido, avaliacao, credito e contrato.

## Interface Qute

O projeto agora possui uma interface web server-side com Qute em `http://localhost:8080/app`.
Por ela voce consegue:

- selecionar o cliente
- criar pedido
- avaliar pedido pela empresa
- conceder credito pelo banco
- gerar contrato
- acompanhar automoveis, pedidos, creditos e contratos

## Nomes
* Arthur Nunes 
* Nayarisson Natan 
* Pedro Henrique 
