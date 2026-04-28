package br.com.alugueldecarros.application.service;

import br.com.alugueldecarros.application.dto.ContratoDtos;
import br.com.alugueldecarros.domain.exception.BusinessException;
import br.com.alugueldecarros.domain.exception.NotFoundException;
import br.com.alugueldecarros.domain.model.Agente;
import br.com.alugueldecarros.domain.model.Automovel;
import br.com.alugueldecarros.domain.model.Cliente;
import br.com.alugueldecarros.domain.model.Contrato;
import br.com.alugueldecarros.domain.model.ContratoCredito;
import br.com.alugueldecarros.domain.model.PedidoAluguel;
import br.com.alugueldecarros.domain.model.StatusAutomovel;
import br.com.alugueldecarros.domain.model.StatusContrato;
import br.com.alugueldecarros.domain.model.StatusPedido;
import br.com.alugueldecarros.domain.repository.AutomovelRepository;
import br.com.alugueldecarros.domain.repository.ContratoCreditoRepository;
import br.com.alugueldecarros.domain.repository.ContratoRepository;
import br.com.alugueldecarros.domain.repository.PedidoAluguelRepository;
import br.com.alugueldecarros.domain.repository.UsuarioRepository;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;

@ApplicationScoped
public class ContratoService {

    @Inject
    PedidoAluguelRepository pedidoRepository;

    @Inject
    ContratoRepository contratoRepository;

    @Inject
    ContratoCreditoRepository contratoCreditoRepository;

    @Inject
    AutomovelRepository automovelRepository;

    @Inject
    UsuarioRepository usuarioRepository;

    @Inject
    NotificacaoService notificacaoService;

    public Contrato criarContrato(ContratoDtos.CriarContratoRequest request) {
        PedidoAluguel pedido = pedidoRepository.findById(request.pedidoId())
                .orElseThrow(() -> new NotFoundException("Pedido nao encontrado."));

        if (pedido.getStatus() != StatusPedido.APROVADO && pedido.getStatus() != StatusPedido.CREDITO_APROVADO) {
            throw new BusinessException("O pedido precisa estar aprovado para gerar contrato.");
        }

        Automovel automovel = automovelRepository.findById(pedido.getAutomovelId())
                .orElseThrow(() -> new NotFoundException("Automovel nao encontrado."));

        if (!automovel.isDisponivel()) {
            throw new BusinessException("O automovel nao esta mais disponivel.");
        }

        contratoRepository.findActiveByAutomovelId(automovel.getId())
                .ifPresent(contrato -> {
                    throw new BusinessException("Ja existe contrato ativo para este automovel.");
                });

        Contrato contrato = new Contrato();
        contrato.setPedidoId(pedido.getId());
        contrato.setClienteId(pedido.getClienteId());
        contrato.setAutomovelId(pedido.getAutomovelId());
        contrato.setDataInicio(pedido.getDataInicio());
        contrato.setDataFim(pedido.getDataFim());
        contrato.setDataAssinatura(LocalDate.now());
        contrato.setTipo(request.tipoContrato());
        contrato.setStatus(StatusContrato.EM_CONTRATO);

        contratoRepository.save(contrato);

        if (pedido.getContratoCreditoId() != null) {
            ContratoCredito credito = contratoCreditoRepository.findById(pedido.getContratoCreditoId())
                    .orElseThrow(() -> new NotFoundException("Contrato de credito nao encontrado."));

            credito.setContratoId(contrato.getId());
            contratoCreditoRepository.save(credito);
        }

        automovel.setStatus(StatusAutomovel.ALUGADO);
        automovelRepository.save(automovel);

        pedido.setContratoId(contrato.getId());
        pedido.setStatus(StatusPedido.CONTRATADO);
        pedidoRepository.save(pedido);

        notificacaoService.notificarMudancaStatus(pedido, pedido.getStatus().name());

        return contrato;
    }

    public Contrato devolver(ContratoDtos.DevolverContratoRequest request) {
        Contrato contrato = contratoRepository.findById(request.contratoId())
                .orElseThrow(() -> new NotFoundException("Contrato nao encontrado."));

        if (!contrato.isAtivo()) {
            throw new BusinessException("Apenas contratos ativos podem ser finalizados.");
        }

        if (request.quilometragemFinal() == null || request.quilometragemFinal() <= 0) {
            throw new BusinessException("Informe uma quilometragem final valida.");
        }

        Automovel automovel = automovelRepository.findById(contrato.getAutomovelId())
                .orElseThrow(() -> new NotFoundException("Automovel nao encontrado."));

        contrato.setStatus(StatusContrato.FINALIZADO);
        contrato.setQuilometragemFinal(request.quilometragemFinal());
        contrato.setAvarias(request.avarias());
        contrato.setDataDevolucao(LocalDateTime.now());
        contratoRepository.save(contrato);

        automovel.setStatus(StatusAutomovel.DISPONIVEL);
        automovelRepository.save(automovel);

        PedidoAluguel pedido = pedidoRepository.findById(contrato.getPedidoId())
                .orElseThrow(() -> new NotFoundException("Pedido nao encontrado."));

        pedido.setStatus(StatusPedido.FINALIZADO);
        pedidoRepository.save(pedido);

        notificacaoService.notificarMudancaStatus(pedido, pedido.getStatus().name());

        return contrato;
    }

    public byte[] gerarPdf(Long contratoId) {
        Contrato contrato = contratoRepository.findById(contratoId)
                .orElseThrow(() -> new NotFoundException("Contrato nao encontrado."));

        if (!contrato.isAtivo()) {
            throw new BusinessException("O PDF so pode ser gerado para contratos em andamento.");
        }

        PedidoAluguel pedido = pedidoRepository.findById(contrato.getPedidoId())
                .orElseThrow(() -> new NotFoundException("Pedido nao encontrado."));

        Cliente cliente = usuarioRepository.findClienteById(contrato.getClienteId())
                .orElseThrow(() -> new NotFoundException("Cliente nao encontrado."));

        Automovel automovel = automovelRepository.findById(contrato.getAutomovelId())
                .orElseThrow(() -> new NotFoundException("Automovel nao encontrado."));

        Agente empresa = usuarioRepository.findAgenteById(automovel.getProprietarioId())
                .orElseThrow(() -> new NotFoundException("Agente da empresa nao encontrado."));

        Agente agenteResponsavel = pedido.getAgenteResponsavelId() == null
                ? empresa
                : usuarioRepository.findAgenteById(pedido.getAgenteResponsavelId()).orElse(empresa);

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            Document document = new Document();
            PdfWriter.getInstance(document, outputStream);

            document.open();

            Font titulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font subtitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font corpo = FontFactory.getFont(FontFactory.HELVETICA, 11);

            document.add(new Paragraph("Contrato de Locacao de Veiculo", titulo));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Dados do cliente", subtitulo));
            document.add(new Paragraph("Nome: " + cliente.getNome(), corpo));
            document.add(new Paragraph("CPF: " + cliente.getCpf(), corpo));
            document.add(new Paragraph("Endereco: " + cliente.getEndereco(), corpo));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Dados do automovel", subtitulo));
            document.add(new Paragraph("Matricula: " + automovel.getMatricula(), corpo));
            document.add(new Paragraph("Marca/Modelo: " + automovel.getMarca() + " " + automovel.getModelo(), corpo));
            document.add(new Paragraph("Ano: " + automovel.getAno(), corpo));
            document.add(new Paragraph("Placa: " + automovel.getPlaca(), corpo));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Dados da empresa", subtitulo));
            document.add(new Paragraph("Empresa responsavel: " + empresa.getNome(), corpo));
            document.add(new Paragraph("Agente responsavel: " + agenteResponsavel.getNome(), corpo));
            document.add(new Paragraph("Tipo de contrato: " + contrato.getTipo(), corpo));
            document.add(new Paragraph("Periodo: " + contrato.getDataInicio() + " ate " + contrato.getDataFim(), corpo));
            document.add(new Paragraph("Data da assinatura: " + contrato.getDataAssinatura(), corpo));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Termos de propriedade", subtitulo));
            document.add(new Paragraph(
                    "O automovel permanece de propriedade da locadora durante toda a vigencia do contrato. " +
                            "O cliente assume a responsabilidade pela guarda, uso regular e devolucao nas condicoes acordadas.",
                    corpo
            ));

            document.add(new Paragraph(
                    "Este documento foi emitido automaticamente pelo sistema para fins de registro do aluguel.",
                    corpo
            ));

            document.close();

            return outputStream.toByteArray();
        } catch (DocumentException exception) {
            throw new BusinessException("Nao foi possivel gerar o PDF do contrato.");
        }
    }
}