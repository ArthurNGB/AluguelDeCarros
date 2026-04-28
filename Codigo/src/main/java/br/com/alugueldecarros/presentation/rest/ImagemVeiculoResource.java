package br.com.alugueldecarros.presentation.rest;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Set;

@Path("/app/uploads/veiculos")
public class ImagemVeiculoResource {

    private static final Set<String> EXTENSOES_PERMITIDAS = Set.of("jpg", "jpeg", "png", "webp");

    @ConfigProperty(name = "aluguel.upload.veiculos-dir", defaultValue = "uploads/veiculos")
    String diretorioUploadVeiculos;

    /*
     * Endpoint principal usado pelo front-end.
     *
     * Exemplo:
     * /app/uploads/veiculos/id-1
     *
     * Ele procura automaticamente:
     * uploads/veiculos/id-1.jpg
     * uploads/veiculos/id-1.jpeg
     * uploads/veiculos/id-1.png
     * uploads/veiculos/id-1.webp
     *
     * Assim o JavaScript não precisa tentar várias extensões e o console
     * não fica cheio de 404.
     */
    @GET
    @Path("/id-{id}")
    @Produces(MediaType.WILDCARD)
    public Response visualizarPorId(@PathParam("id") Long id) {
        if (id == null || id <= 0) {
            return imagemPadrao();
        }

        java.nio.file.Path imagem = localizarImagemPorId(id);
        if (imagem == null) {
            return imagemPadrao();
        }

        return responderImagem(imagem);
    }

    /*
     * Compatibilidade: ainda permite abrir a imagem com extensão direta.
     * Exemplo:
     * /app/uploads/veiculos/id-1.jpg
     */
    @GET
    @Path("/{arquivo}")
    @Produces(MediaType.WILDCARD)
    public Response visualizarArquivo(@PathParam("arquivo") String arquivo) {
        if (arquivo == null || !arquivo.matches("id-[0-9]+\\.(jpg|jpeg|png|webp)")) {
            return imagemPadrao();
        }

        java.nio.file.Path diretorio = diretorioUpload();
        java.nio.file.Path imagem = diretorio.resolve(arquivo).normalize();

        if (!imagem.startsWith(diretorio) || !Files.exists(imagem) || !Files.isRegularFile(imagem)) {
            return imagemPadrao();
        }

        return responderImagem(imagem);
    }

    private java.nio.file.Path localizarImagemPorId(Long id) {
        java.nio.file.Path diretorio = diretorioUpload();

        if (!Files.exists(diretorio) || !Files.isDirectory(diretorio)) {
            return null;
        }

        String prefixo = "id-" + id + ".";

        try (DirectoryStream<java.nio.file.Path> stream = Files.newDirectoryStream(diretorio, prefixo + "*")) {
            for (java.nio.file.Path arquivo : stream) {
                if (Files.isRegularFile(arquivo) && extensaoPermitida(arquivo.getFileName().toString())) {
                    return arquivo;
                }
            }
        } catch (Exception ignored) {
            return null;
        }

        return null;
    }

    private java.nio.file.Path diretorioUpload() {
        return Paths.get(diretorioUploadVeiculos).toAbsolutePath().normalize();
    }

    private boolean extensaoPermitida(String nomeArquivo) {
        int ponto = nomeArquivo.lastIndexOf('.');
        if (ponto < 0 || ponto == nomeArquivo.length() - 1) {
            return false;
        }

        String extensao = nomeArquivo.substring(ponto + 1).toLowerCase(Locale.ROOT);
        return EXTENSOES_PERMITIDAS.contains(extensao);
    }

    private Response responderImagem(java.nio.file.Path imagem) {
        return Response.ok(imagem.toFile())
                .type(tipoConteudo(imagem.getFileName().toString()))
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .build();
    }

    private Response imagemPadrao() {
        String svg = """
                <svg xmlns="http://www.w3.org/2000/svg" width="900" height="620" viewBox="0 0 900 620">
                  <defs>
                    <linearGradient id="bg" x1="0" x2="1" y1="0" y2="1">
                      <stop offset="0%" stop-color="#dbeafe"/>
                      <stop offset="48%" stop-color="#eef2ff"/>
                      <stop offset="100%" stop-color="#f8fafc"/>
                    </linearGradient>
                    <linearGradient id="car" x1="0" x2="1" y1="0" y2="0">
                      <stop offset="0%" stop-color="#2563eb"/>
                      <stop offset="100%" stop-color="#0f172a"/>
                    </linearGradient>
                  </defs>
                  <rect width="900" height="620" fill="url(#bg)"/>
                  <circle cx="720" cy="105" r="165" fill="#ffffff" opacity=".42"/>
                  <circle cx="140" cy="540" r="190" fill="#bfdbfe" opacity=".42"/>
                  <g transform="translate(150 220)">
                    <path d="M104 170h500c30 0 56 23 61 53l10 61H42l9-58c5-32 23-56 53-56Z" fill="url(#car)" opacity=".92"/>
                    <path d="M212 72h232c45 0 87 25 108 65l52 99H92l73-116c11-30 26-48 47-48Z" fill="#1d4ed8" opacity=".88"/>
                    <path d="M232 105h82v88H174l46-72c3-10 6-16 12-16Zm114 0h88c26 0 47 13 61 35l34 53H346v-88Z" fill="#dbeafe" opacity=".95"/>
                    <circle cx="174" cy="290" r="56" fill="#0f172a"/>
                    <circle cx="174" cy="290" r="24" fill="#e5e7eb"/>
                    <circle cx="548" cy="290" r="56" fill="#0f172a"/>
                    <circle cx="548" cy="290" r="24" fill="#e5e7eb"/>
                    <rect x="70" y="224" width="585" height="38" rx="19" fill="#60a5fa" opacity=".38"/>
                  </g>
                  <text x="450" y="555" text-anchor="middle" font-family="Arial, sans-serif" font-size="34" font-weight="800" fill="#334155">
                    Imagem do veículo
                  </text>
                </svg>
                """;

        return Response.ok(svg)
                .type("image/svg+xml")
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .build();
    }

    private String tipoConteudo(String arquivo) {
        String nome = arquivo.toLowerCase(Locale.ROOT);
        if (nome.endsWith(".png")) {
            return "image/png";
        }
        if (nome.endsWith(".webp")) {
            return "image/webp";
        }
        return "image/jpeg";
    }
}
