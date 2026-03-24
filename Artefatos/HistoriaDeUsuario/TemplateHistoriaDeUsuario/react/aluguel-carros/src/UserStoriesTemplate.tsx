import React from 'react';

const UserStoriesTemplate = () => {
  const stories = [
    {
      id: "HU 01",
      title: "Registro e Solicitação de Aluguel",
      role: "cliente individual cadastrado no sistema",
      action: "inserir um novo pedido de aluguel de automóvel, informando meus dados profissionais e rendimentos",
      benefit: "eu possa iniciar o processo de contratação de um veículo de forma online",
      criteria: [
        "O sistema deve validar se o usuário está autenticado antes de exibir o formulário de pedido.",
        "O formulário deve permitir a inserção de até 3 entidades empregadoras com seus respectivos rendimentos.",
        "Todos os campos obrigatórios (dados de identificação: RG, CPF, Nome, Endereço) devem ser validados antes do envio.",
        "Após o envio bem-sucedido, o pedido deve receber o status 'Em Análise' automaticamente.",
        "O sistema deve exibir uma confirmação visual com o número do pedido gerado."
      ],
      priority: "HIGH",
      priorityColor: "bg-orange-200 text-orange-900",
      value: "$25",
      points: "8"
    },
    {
      id: "HU 02",
      title: "Avaliação Financeira do Pedido",
      role: "agente financeiro (banco)",
      action: "consultar os pedidos de aluguel pendentes e avaliar a situação financeira do contratante",
      benefit: "eu possa emitir um parecer positivo ou negativo e, se necessário, associar um contrato de crédito ao pedido",
      criteria: [
        "O agente deve visualizar os dados de identificação completos e os rendimentos declarados pelo cliente.",
        "O sistema deve listar apenas os pedidos com status 'Em Análise', filtrando por data de criação.",
        "O sistema deve permitir registrar a aprovação ou rejeição fundamentada do pedido.",
        "Caso aprovado, O sistema deve permitir vincular as informações de matrícula e modelo do automóvel ao contrato.",
        "A decisão do agente deve atualizar o status do pedido para 'Aprovado' ou 'Rejeitado' imediatamente."
      ],
      priority: "HIGH",
      priorityColor: "bg-orange-200 text-orange-900",
      value: "$20",
      points: "13"
    },
    {
      id: "HU 03",
      title: "Gestão de Contratos e Propriedade",
      role: "agente de empresa de aluguel",
      action: "modificar os detalhes de um pedido aprovado e definir a propriedade do veículo no contrato (Cliente, Empresa ou Banco)",
      benefit: "a realização do contrato reflita corretamente os termos acordados e o registro do automóvel esteja atualizado",
      criteria: [
        "Permitir a edição dos campos: marca, modelo, ano e placa do veículo vinculado ao pedido.",
        "Oferecer seleção obrigatória para o tipo de propriedade do automóvel conforme o tipo de contrato.",
        "Impedir a modificação de pedidos com status diferente de 'Aprovado'.",
        "Garantir que as alterações fiquem disponíveis para consulta imediata pelo cliente através da interface web.",
        "Registrar em log o autor e a data/hora de cada modificação realizada."
      ],
      priority: "MEDIUM",
      priorityColor: "bg-yellow-200 text-yellow-900",
      value: "$5",
      points: "5"
    },
    {
      id: "HU 04",
      title: "Autenticação e Controle de Acesso",
      role: "novo usuário (seja cliente ou agente)",
      action: "me cadastrar e realizar login no sistema com credenciais seguras",
      benefit: "eu possa acessar as funcionalidades restritas ao meu perfil e garantir a privacidade dos meus dados",
      criteria: [
        "O formulário de cadastro deve coletar: nome, CPF, RG, endereço, profissão e tipo de perfil (Cliente ou Agente). Se o tipo 'Agente' for selecionado, o sistema deve exigir um Token Corporativo para liberar o cadastro.",
        "O sistema deve diferenciar o painel de controle (Dashboard) entre perfis de Clientes e Agentes.",
        "Deve impedir que um cliente acesse rotas exclusivas de Agentes (ex.: Avaliação Financeira).",
        "A senha deve ter no mínimo 8 caracteres; o sistema deve rejeitar senhas fracas com mensagem descritiva.",
        "Após 5 tentativas de login inválidas consecutivas, a conta deve ser temporariamente bloqueada."
      ],
      priority: "HIGH",
      priorityColor: "bg-orange-200 text-orange-900",
      value: "$15",
      points: "8"
    },
    {
      id: "HU 05",
      title: "Realização do Contrato de Aluguel",
      role: "agente do sistema",
      action: "converter um pedido aprovado em um contrato formal, definindo quem será o proprietário legal do veículo (Cliente, Empresa ou Banco)",
      benefit: "o processo jurídico de aluguel seja oficializado e o automóvel seja vinculado ao proprietário conforme o contrato",
      criteria: [
        "O sistema deve permitir selecionar o tipo de propriedade: Cliente, Empresa ou Banco.",
        "Se o contrato envolver financiamento, o sistema deve exigir o vínculo com um Banco Agente.",
        "Ao finalizar, o status do automóvel deve ser atualizado para 'Alugado/Em Contrato'.",
        "O sistema deve gerar um número único de contrato e registrar a data de execução.",
        "Não deve ser possível realizar um contrato para pedidos com status diferente de 'Aprovado'."
      ],
      priority: "HIGH",
      priorityColor: "bg-orange-200 text-orange-900",
      value: "$20",
      points: "13"
    },
    {
      id: "HU 06",
      title: "Consulta de Status do Pedido",
      role: "cliente cadastrado com ao menos um pedido criado",
      action: "consultar o status atual e o histórico de andamento dos meus pedidos de aluguel",
      benefit: "eu possa acompanhar o progresso da minha solicitação sem precisar contatar a empresa diretamente",
      criteria: [
        "O cliente deve visualizar apenas os seus próprios pedidos, nunca os de outros usuários.",
        "O sistema deve exibir o status atual de cada pedido: Em Análise, Aprovado, Rejeitado ou Alugado/Em Contrato.",
        "Deve ser possível filtrar pedidos por status e ordenar por data de criação.",
        "Ao clicar em um pedido, o cliente deve ver os detalhes do veículo associado (quando disponível).",
        "A consulta deve refletir o estado mais atualizado do pedido em tempo real."
      ],
      priority: "MEDIUM",
      priorityColor: "bg-yellow-200 text-yellow-900",
      value: "$5",
      points: "5"
    },
    {
      id: "HU 07",
      title: "Cancelamento de Pedido",
      role: "cliente com um pedido em andamento",
      action: "cancelar um pedido de aluguel que ainda não tenha sido convertido em contrato",
      benefit: "eu possa desistir da solicitação sem precisar entrar em contato com a empresa",
      criteria: [
        "O cancelamento deve ser permitido apenas para pedidos com status 'Em Análise'.",
        "Pedidos com status 'Aprovado', 'Rejeitado' ou 'Alugado/Em Contrato' não podem ser cancelados pelo cliente.",
        "O sistema deve exibir uma confirmação antes de efetivar o cancelamento.",
        "Após o cancelamento, o status do pedido deve ser atualizado para 'Cancelado' e o pedido deve permanecer no histórico.",
        "O cliente deve receber uma notificação (ou mensagem na tela) confirmando o cancelamento."
      ],
      priority: "LOW",
      priorityColor: "bg-green-200 text-green-900",
      value: "$5",
      points: "3"
    },
    {
      id: "HU 08",
      title: "Modificação de Pedido pelo Cliente",
      role: "cliente com um pedido em andamento",
      action: "modificar os dados do meu pedido de aluguel (como entidades empregadoras ou rendimentos)",
      benefit: "eu possa corrigir ou atualizar informações fornecidas antes da avaliação financeira",
      criteria: [
        "A edição só deve ser permitida para pedidos que possuam o status 'Em Análise'.",
        "O sistema não deve permitir a alteração de pedidos que já tenham sido 'Aprovados', 'Rejeitados' ou convertidos em contrato.",
        "Os campos editados devem passar pelas mesmas validações de preenchimento obrigatório aplicadas na criação do pedido.",
        "O sistema deve manter o status 'Em Análise' após a edição e registar a atualização para o agente financeiro.",
        "O cliente deve receber uma mensagem de sucesso visual ao guardar as novas informações."
      ],
      priority: "MEDIUM",
      priorityColor: "bg-yellow-200 text-yellow-900",
      value: "$5",
      points: "5"
    }
  ];

  return (
    <div 
      className="p-4 md:p-8 font-sans bg-gray-50 min-h-screen text-gray-800 print:bg-white print:p-0"
      style={{ WebkitPrintColorAdjust: 'exact', printColorAdjust: 'exact' }}
    >
      <div className="max-w-[1500px] mx-auto">
        
        {/* Cabeçalho Melhorado */}
        <div className="flex justify-between items-end mb-4 print:mb-4">
          <div>
            <h1 className="text-3xl font-bold text-[#0a192f] font-serif print:text-2xl">User Stories: Aluguel de Carros</h1>
          </div>
          
          <button 
            onClick={() => window.print()}
            className="print:hidden bg-blue-600 hover:bg-blue-700 text-white font-semibold py-2 px-5 rounded shadow transition-colors"
          >
            🖨️ Gerar PDF Final
          </button>
        </div>

        {/* Banner de Informação Discreto (Escondido na impressão) */}
        <div className="flex items-center gap-3 bg-blue-50 text-blue-800 text-sm px-4 py-3 rounded-md mb-6 border border-blue-100 print:hidden shadow-sm">
          <svg className="w-5 h-5 flex-shrink-0 text-blue-600" fill="currentColor" viewBox="0 0 20 20">
            <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clipRule="evenodd"></path>
          </svg>
          <p>
            <strong>Guia de Leitura:</strong> O <strong>Valor ($)</strong> indica a prioridade de negócio (o que traz mais retorno e deve ser feito primeiro), enquanto os <strong>Pontos</strong> indicam a complexidade ou dificuldade técnica do trabalho.
          </p>
        </div>

        <div className="w-full bg-white shadow-lg rounded border border-gray-300 print:shadow-none print:border-none">
          
          <table className="w-full text-left border-collapse table-fixed">
            <thead>
              <tr className="bg-gray-100 text-gray-600 text-[12px] uppercase tracking-wider border-b-2 border-gray-300 print:bg-gray-100">
                <th className="w-[14%] p-4 border-r border-gray-300 font-bold">Título / ID</th>
                <th className="w-[30%] p-4 border-r border-gray-300 font-bold">História de Usuário</th>
                <th className="w-[35%] p-4 border-r border-gray-300 font-bold">Critérios de Aceite</th>
                <th className="w-[7%] p-2 border-r border-gray-300 font-bold text-center">Prioridade</th>
                
                {/* Cabeçalho Valor com Tooltip */}
                <th className="w-[7%] p-2 border-r border-gray-300 font-bold text-center text-green-700 relative group cursor-help">
                  <span className="border-b border-dotted border-green-700 pb-[1px]">Valor ($)</span>
                  <div className="absolute bottom-full left-1/2 -translate-x-1/2 mb-2 hidden group-hover:block w-40 bg-gray-800 text-white text-[11px] rounded p-2 z-10 font-normal normal-case tracking-normal shadow-lg transition-opacity">
                    Indica o que deve ser feito primeiro (Prioridade de Negócio).
                    <div className="absolute top-full left-1/2 -translate-x-1/2 border-4 border-transparent border-t-gray-800"></div>
                  </div>
                </th>
                
                {/* Cabeçalho Pontos com Tooltip */}
                <th className="w-[7%] p-2 font-bold text-center text-blue-700 relative group cursor-help">
                  <span className="border-b border-dotted border-blue-700 pb-[1px]">Pontos</span>
                  <div className="absolute bottom-full left-1/2 -translate-x-1/2 mb-2 hidden group-hover:block w-40 bg-gray-800 text-white text-[11px] rounded p-2 z-10 font-normal normal-case tracking-normal shadow-lg transition-opacity">
                    Indica a dificuldade ou o esforço do trabalho (Estimativa).
                    <div className="absolute top-full left-1/2 -translate-x-1/2 border-4 border-transparent border-t-gray-800"></div>
                  </div>
                </th>
              </tr>
            </thead>
            
            <tbody>
              {stories.map((story, index) => (
                <tr key={index} className="border-b border-gray-300 last:border-b-0 break-inside-avoid hover:bg-gray-50/50">
                  
                  <td className="p-4 border-r border-gray-300 font-bold text-gray-800 align-middle text-sm">
                    {story.id}<br/>{story.title}
                  </td>
                  
                  <td className="p-4 border-r border-gray-300 text-gray-800 align-middle text-[14px] leading-relaxed">
                    <span className="font-bold text-black">Como um</span> {story.role}, <br />
                    <span className="font-bold text-black">Eu quero</span> {story.action}, <br />
                    <span className="font-bold text-black">Para que</span> {story.benefit}.
                  </td>
                  
                  <td className="border-r border-gray-300 align-top p-0">
                    <div className="flex flex-col h-full">
                      {story.criteria.map((criterion, i) => (
                        <div key={i} className="flex border-b border-gray-200 last:border-b-0 flex-grow">
                          <div className="w-8 flex-shrink-0 flex items-center justify-center border-r border-gray-200 font-bold text-gray-400 bg-gray-50/50 text-xs">
                            {i + 1}
                          </div>
                          <div className="p-3 text-[13px] text-gray-600 flex items-center">
                            {criterion}
                          </div>
                        </div>
                      ))}
                    </div>
                  </td>
                  
                  <td className={`border-r border-gray-300 text-center font-bold align-middle text-[12px] ${story.priorityColor}`}>
                    {story.priority}
                  </td>
                  
                  {/* Células de Valores com title nativo como fallback */}
                  <td title="Prioridade de Negócio" className="p-2 border-r border-gray-300 text-center font-bold text-green-700 align-middle text-lg bg-green-50/30 cursor-default">
                    {story.value}
                  </td>

                  <td title="Dificuldade da Tarefa" className="p-2 text-center font-bold text-blue-700 align-middle text-lg bg-blue-50/30 cursor-default">
                    {story.points}
                  </td>
                  
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        
      </div>
    </div>
  );
};

export default UserStoriesTemplate;