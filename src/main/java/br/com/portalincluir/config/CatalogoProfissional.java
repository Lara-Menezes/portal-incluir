package br.com.portalincluir.config;
import java.text.Normalizer;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class CatalogoProfissional {
    private CatalogoProfissional() {}
    public static final List<String> CURSOS = List.of("Técnico Integrado em Informática", "Técnico Integrado em Instrumento Musical", "Técnico Integrado em Edificações", "Tecnólogo em Análise e Desenvolvimento de Sistemas", "Bacharel em Engenharia Civil");
    public static final String BNCC = "Base comum do ensino médio (BNCC)";
    public static final List<String> ATUACOES_DOCENTES = java.util.stream.Stream.concat(CURSOS.stream(), java.util.stream.Stream.of(BNCC)).toList();
    public static final List<String> AREAS = List.of("Psicologia", "Serviço social", "Pedagogia", "Atendimento educacional especializado (AEE)", "Tradução e interpretação de Libras", "Cuidador e apoio às atividades diárias", "Leitura e audiodescrição", "Apoio em Braille", "Alfabetização de jovens e adultos", "Fonoaudiologia", "Terapia ocupacional", "Fisioterapia", "Enfermagem", "Nutrição", "Outra área de apoio");
    private static String chave(String texto) { return Normalizer.normalize(Objects.toString(texto, ""), Normalizer.Form.NFD).replaceAll("\\p{M}", "").strip().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT); }
    public static String curso(String valor) {
        String chave = chave(valor);
        for (String curso : CURSOS) if (chave(curso).equals(chave)) return curso;
        if (Set.of("analise e desenvolvimento de sistemas", "ads", "tecnologia em analise e desenvolvimento de sistemas").contains(chave)) return CURSOS.get(3);
        return valor;
    }
    public static Set<String> cursos(Set<String> valores) {
        if (valores == null || valores.isEmpty() || valores.size() > ATUACOES_DOCENTES.size() || valores.stream().anyMatch(Objects::isNull)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selecione o curso ou a base comum de atuação.");
        Set<String> resultado = new LinkedHashSet<>();
        for (String valor : valores) { String normalizado = chave(BNCC).equals(chave(valor)) ? BNCC : curso(valor); if (!ATUACOES_DOCENTES.contains(normalizado)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selecione um curso ou a base comum da lista."); resultado.add(normalizado); }
        return resultado;
    }
    public static String area(String valor) {
        return AREAS.stream().filter(area -> chave(area).equals(chave(valor))).findFirst().orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selecione uma área de atuação da lista."));
    }
    public static String cursoEstudante(String valor, String anterior) {
        if (valor == null || valor.isBlank()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selecione o curso do estudante.");
        String normalizado = curso(valor);
        if (!CURSOS.contains(normalizado) && !Objects.equals(valor, anterior)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selecione o curso do estudante na lista.");
        return normalizado;
    }
    public static boolean atuaNoCurso(Set<String> atuacoes, String curso) {
        String normalizado = curso(curso);
        return atuacoes.contains(normalizado) || atuacoes.contains(BNCC) && CURSOS.subList(0, 3).contains(normalizado);
    }
}
