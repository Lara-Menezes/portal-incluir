package br.com.portalincluir.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.*;
import java.util.*;
import java.util.zip.ZipInputStream;

final class ValidadorArquivoAcademico {
    private ValidadorArquivoAcademico() { }
    static final long LIMITE = 10 * 1024 * 1024;
    record Arquivo(String nome, String tipo, byte[] dados) { }

    static Arquivo validar(MultipartFile file) {
        if (file == null || file.isEmpty()) throw erro("Selecione um arquivo não vazio.");
        if (file.getSize() > LIMITE) throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "O arquivo deve ter no máximo 10 MB.");
        String nome = Optional.ofNullable(file.getOriginalFilename()).orElse("").replace('\\', '/');
        nome = nome.substring(nome.lastIndexOf('/') + 1).replaceAll("[^\\p{L}\\p{N}._ ()-]", "_");
        if (nome.isBlank() || nome.length() > 180) throw erro("Nome de arquivo inválido (máximo de 180 caracteres).");
        String extensao = nome.substring(nome.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
        try {
            byte[] dados = file.getBytes();
            if (dados.length > LIMITE) throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "O arquivo deve ter no máximo 10 MB.");
            String tipo = switch (extensao) {
                case "pdf" -> { exigir(comeca(dados, new byte[]{37, 80, 68, 70, 45})); yield "application/pdf"; }
                case "png" -> { exigir(comeca(dados, new byte[]{(byte)137, 80, 78, 71, 13, 10, 26, 10})); yield "image/png"; }
                case "jpg", "jpeg" -> { exigir(comeca(dados, new byte[]{(byte)255, (byte)216, (byte)255})); yield "image/jpeg"; }
                case "txt" -> {
                    String texto = StandardCharsets.UTF_8.newDecoder().onMalformedInput(CodingErrorAction.REPORT)
                            .onUnmappableCharacter(CodingErrorAction.REPORT).decode(ByteBuffer.wrap(dados)).toString();
                    exigir(!texto.contains("\u0000")); yield "text/plain";
                }
                case "docx" -> { validarWord(dados); yield "application/vnd.openxmlformats-officedocument.wordprocessingml.document"; }
                default -> throw erro("Formatos permitidos: PDF, DOCX, TXT, PNG e JPEG.");
            };
            return new Arquivo(nome, tipo, dados);
        } catch (IOException ex) { throw erro("O arquivo é inválido ou não pôde ser lido."); }
    }

    private static boolean comeca(byte[] dados, byte[] inicio) {
        return dados.length >= inicio.length && Arrays.equals(Arrays.copyOf(dados, inicio.length), inicio);
    }
    private static void validarWord(byte[] dados) throws IOException {
        Set<String> entradas = new HashSet<>();
        long total = 0;
        byte[] buffer = new byte[8192];
        try (var zip = new ZipInputStream(new ByteArrayInputStream(dados))) {
            java.util.zip.ZipEntry entrada;
            while ((entrada = zip.getNextEntry()) != null) {
                String nome = entrada.getName();
                exigir(!nome.contains("..") && !nome.startsWith("/") && !nome.contains("\\")
                        && !nome.toLowerCase(Locale.ROOT).contains("vbaproject"));
                exigir(entradas.add(nome) && entradas.size() <= 1024);
                int lidos;
                while ((lidos = zip.read(buffer)) != -1) { total += lidos; exigir(total <= 25 * 1024 * 1024); }
            }
        }
        exigir(entradas.contains("[Content_Types].xml") && entradas.contains("word/document.xml"));
    }
    private static void exigir(boolean valido) { if (!valido) throw erro("O conteúdo do arquivo não corresponde ao formato permitido."); }
    private static ResponseStatusException erro(String mensagem) { return new ResponseStatusException(HttpStatus.BAD_REQUEST, mensagem); }
}
