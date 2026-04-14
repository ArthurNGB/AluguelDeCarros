<<<<<<< HEAD
# Sistema de Aluguel de Carros — Locadora Horizonte

Sistema web completo de gestão de aluguel de veículos, desenvolvido com **Java 21 + Quarkus**, interface server-side com **Qute** e API REST documentada via **Swagger**. Toda a persistência é feita em memória, tornando o projeto ideal para demonstração e aprendizado do fluxo completo de uma locadora.

---

## O que o sistema faz

O sistema simula o ciclo de vida completo de um aluguel de carro, desde a solicitação pelo cliente até a devolução do veículo. Ele envolve três perfis de usuário — **Cliente**, **Empresa (locadora)** e **Banco** — cada um com responsabilidades distintas dentro de um fluxo de aprovação em quatro etapas.

---

## Perfis de usuário

### Cliente
Pessoa que deseja alugar um veículo. Pode se cadastrar, fazer login, navegar pelo catálogo, criar pedidos de aluguel, acompanhar o status em tempo real, solicitar prorrogação de contratos ativos e cancelar pedidos que ainda não foram contratados.

### Empresa (Locadora)
Responsável pela frota de veículos. Avalia os pedidos submetidos pelos clientes, aprovando ou rejeitando com base em um parecer. Também avalia solicitações de prorrogação de contratos. Gerencia o cadastro, atualização e remoção de veículos da frota.

### Banco
Instituição financeira que concede crédito. Quando um cliente solicita financiamento junto com o pedido de aluguel, o banco analisa e libera o crédito, definindo valor, taxa de juros e número de parcelas.

---

## Fluxo completo de um aluguel

### Etapa 1 — Avaliação de pedido
O cliente acessa o **Catálogo**, escolhe um veículo disponível e cria um pedido de aluguel informando as datas desejadas, uma justificativa e se deseja solicitar crédito junto. O pedido entra com status **Submetido**.

A empresa avaliadora analisa o pedido no **Painel Operacional** e registra um parecer aprovando ou rejeitando.

- Se **rejeitado**: pedido encerra com status **Rejeitado**.
- Se **aprovado sem crédito**: pedido avança diretamente para a Etapa 3 com status **Aprovado**.
- Se **aprovado com crédito**: pedido avança para a Etapa 2 com status **Aguardando crédito**.

### Etapa 2 — Concessão de crédito (opcional)
Apenas para pedidos onde o cliente marcou "Solicitar crédito junto". O banco acessa a fila de pedidos aguardando crédito e preenche os dados do financiamento: valor liberado, taxa de juros e número de parcelas. O sistema calcula automaticamente o valor da parcela mensal. Após liberação, o pedido avança com status **Crédito aprovado**.

### Etapa 3 — Geração de contrato
Pedidos com status **Aprovado** ou **Crédito aprovado** ficam disponíveis para geração de contrato. A empresa seleciona o pedido, escolhe o tipo de contrato (Diário, Semanal ou Mensal) e confirma. O sistema:
- Cria o contrato oficial com data de assinatura;
- Bloqueia o veículo na frota (status muda para **Em contrato**);
- Atualiza o pedido para status **Contratado**;
- Disponibiliza o PDF do contrato para o cliente baixar.

### Etapa 4 — Registro de devolução
Quando o cliente devolve o veículo, a empresa registra a quilometragem final e eventuais avarias. O sistema:
- Encerra o contrato (status **Finalizado**);
- Libera o veículo de volta à frota (status **Disponível**);
- Atualiza o pedido para status **Finalizado**;
- Notifica o cliente.

---

## Prorrogação de contrato
Se o cliente precisar manter o veículo por mais tempo, pode solicitar uma prorrogação enquanto o contrato estiver ativo. A solicitação gera um novo pedido do tipo **Prorrogação** que vai para análise da empresa. Se aprovada, a data de fim do contrato é estendida automaticamente. Se rejeitada, o contrato segue com a data original.

---

## Funcionalidades por aba

### Minha Área (visão do cliente)
- **Notificações**: central de mensagens com atualizações em tempo real via SSE (Server-Sent Events) — cada mudança de status gera um aviso instantâneo.
- **Acompanhamento de pedidos**: lista todos os pedidos com rastreador visual de 4 etapas (Submetido → Em análise → Aprovado → Contratado) e badge colorido de status.
- **Histórico de contratos**: tabela com todos os contratos e link para baixar o PDF enquanto o contrato estiver ativo.
- **Solicitar prorrogação**: formulário que aparece apenas quando há contratos ativos.
- **Atualizar pedido**: permite editar pedidos que ainda não foram contratados (datas, veículo, justificativa).
- **Cancelar pedido**: disponível para pedidos que ainda não geraram contrato.

### Catálogo
- Listagem de veículos disponíveis com marca, modelo, placa, ano e valor da diária.
- Filtro por marca e ano mínimo.
- Botão "Solicitar aluguel" que pré-seleciona o veículo no formulário de criação de pedido.
- Formulário de criação de pedido com opção de solicitar crédito.

### Acesso
- **Login**: vincula a sessão a um cliente existente via cookie.
- **Cadastro**: cria um novo cliente informando nome, e-mail, senha, CPF, RG, endereço e renda mensal.

### Painel Operacional (visão da empresa/banco)
- **Fluxo de aprovação visual**: painel com os 4 passos e contadores de itens pendentes em cada etapa.
- **Etapa 1 – Avaliação**: aprovação/rejeição de pedidos de aluguel e de solicitações de prorrogação.
- **Etapa 2 – Crédito**: concessão de financiamento com valor, juros e parcelas.
- **Etapa 3 – Contrato**: geração do contrato oficial e bloqueio do veículo.
- **Etapa 4 – Devolução**: encerramento do contrato com quilometragem final e registro de avarias.
- **Gestão de frota**: cadastro, edição inline e remoção de veículos.

---

## Estados de um pedido

| Status | Descrição |
|---|---|
| Submetido | Pedido criado pelo cliente, aguardando avaliação |
| Em análise | Sendo analisado pela empresa |
| Aguardando crédito | Aprovado, aguardando liberação do banco |
| Aprovado | Aprovado (sem crédito), pronto para contrato |
| Crédito aprovado | Crédito liberado, pronto para contrato |
| Rejeitado | Reprovado pela empresa |
| Cancelado | Cancelado pelo cliente |
| Contratado | Contrato gerado e ativo |
| Finalizado | Veículo devolvido, ciclo encerrado |
| Prorrogação em análise | Solicitação de extensão aguardando avaliação |
| Prorrogação aprovada | Data do contrato estendida |
| Prorrogação rejeitada | Extensão negada |

---

## Tecnologias utilizadas

- **Java 21**
- **Quarkus** — framework backend
- **Qute** — template engine server-side para o frontend
- **JAX-RS / RESTEasy Reactive** — endpoints REST
- **OpenIDE (lowagie/iText)** — geração de PDF do contrato
- **SSE (Server-Sent Events)** — notificações em tempo real
- **Maven** — gerenciamento de dependências

---

## Arquitetura do projeto

```
src/main/java/.../
├── domain/
│   ├── model/          # Entidades e enums (PedidoAluguel, Contrato, StatusPedido…)
│   ├── repository/     # Repositórios em memória
│   └── exception/      # Exceções de negócio
├── application/
│   ├── service/        # Regras de negócio (PedidoService, ContratoService…)
│   ├── facade/         # Orquestração dos casos de uso
│   ├── dto/            # Contratos de entrada e saída da API
│   └── mapper/         # Conversão entre entidades e DTOs
├── presentation/
│   └── rest/           # Endpoints REST e controller da interface Qute (AppResource)
└── config/
    └── DataSeeder      # Dados iniciais carregados na inicialização
```

---

## Como executar

**Pré-requisitos:** Java 21+ e Maven instalados.
=======
# Codigo do Projeto

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
>>>>>>> d798b9dba2bddcc36cbb13e793e8f279a2b1221e

```bash
mvn quarkus:dev
```

<<<<<<< HEAD
| Recurso | URL |
|---|---|
| Interface web | http://localhost:8080/app |
| Swagger UI | http://localhost:8080/q/swagger-ui |
| API REST | http://localhost:8080/api |

---

## Dados iniciais

Ao iniciar, o sistema cria automaticamente:

| # | Tipo | Nome | Credenciais |
|---|---|---|---|
| 1 | Empresa (locadora) | Locadora Horizonte | empresa@horizonte.com / 123456 |
| 2 | Banco | Banco Rodovia | banco@rodovia.com / 123456 |
| 3 | Cliente | Maria Silva | maria@cliente.com / 123456 |

E três veículos disponíveis: Toyota Corolla, Jeep Compass e Chevrolet Onix.

Como os dados ficam em memória, eles são resetados a cada reinício do servidor.
=======
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
>>>>>>> d798b9dba2bddcc36cbb13e793e8f279a2b1221e
