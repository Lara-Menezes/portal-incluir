package br.com.portalincluir.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.SQLException;
import java.time.*;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class AuditoriaService {
    private final NamedParameterJdbcTemplate jdbc;
    private final ObjectMapper mapper;
    private final EscopoEstudanteService escopo;
    public AuditoriaService(NamedParameterJdbcTemplate jdbc, ObjectMapper mapper, EscopoEstudanteService escopo) {
        this.jdbc = jdbc;
        this.mapper = mapper;
        this.escopo = escopo;
    }
    public record Registro(String tabela, int revisao, Instant data, JsonNode identidade,
                           String operacao, JsonNode detalhes) {}
    public record Consulta(List<Registro> registros, long total) {}
    private record Tabela(String nome, String chaves) {}

    private List<Tabela> catalogo() {
        return jdbc.query("""
            SELECT c.relname, json_agg(a.attname ORDER BY k.ordem)::text AS chaves
            FROM pg_class c
            JOIN pg_namespace n ON n.oid = c.relnamespace
            JOIN pg_constraint p ON p.conrelid = c.oid AND p.contype = 'p'
            CROSS JOIN LATERAL unnest(p.conkey) WITH ORDINALITY k(numero, ordem)
            JOIN pg_attribute a ON a.attrelid = c.oid AND a.attnum = k.numero
            WHERE n.nspname = 'auditoria' AND c.relkind IN ('r', 'p')
              AND right(c.relname, 4) = '_aud' AND a.attname NOT IN ('rev', 'revtype')
              AND EXISTS (SELECT 1 FROM pg_attribute x WHERE x.attrelid = c.oid
                  AND x.attname = 'detalhes' AND NOT x.attisdropped)
            GROUP BY c.relname ORDER BY c.relname
            """, Map.of(), (rs, i) -> new Tabela(rs.getString("relname"), rs.getString("chaves")));
    }
    public List<String> tabelas() { return catalogo().stream().map(Tabela::nome).toList(); }
    private static String identificador(String nome) { return "\"" + nome.replace("\"", "\"\"") + "\""; }
    private static String literal(String texto) { return "'" + texto.replace("'", "''") + "'"; }
    private JsonNode json(String value) throws SQLException {
        if (value == null) return null;
        try { return mapper.readTree(value); }
        catch (Exception e) { throw new SQLException("Detalhes de auditoria invalidos", e); }
    }
    public Consulta consultar(String tabela, String operacao, LocalDate inicio, LocalDate fim,
                              String registro, int pagina, int tamanho) {
        if (pagina < 0 || tamanho < 1 || tamanho > 100 || (long) pagina * tamanho > Integer.MAX_VALUE
                || (inicio != null && fim != null && inicio.isAfter(fim)) || registro.length() > 200)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Filtros ou paginacao invalidos");
        Integer tipo = switch (operacao) {
            case "" -> null;
            case "ADD" -> 0;
            case "MOD" -> 1;
            case "DEL" -> 2;
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Operacao invalida");
        };
        var idsPermitidos = escopo.idsPermitidos();
        if (idsPermitidos.isPresent() && idsPermitidos.get().isEmpty()) return new Consulta(List.of(), 0);
        List<Tabela> tabelas = catalogo();
        if (!tabela.isEmpty() && tabelas.stream().noneMatch(t -> t.nome().equals(tabela)))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tabela de auditoria invalida");
        if (!tabela.isEmpty()) tabelas = tabelas.stream().filter(t -> t.nome().equals(tabela)).toList();
        if (tabelas.isEmpty()) return new Consulta(List.of(), 0);
        // Nomes vem exclusivamente do catalogo. Valores de filtros usam parametros vinculados.
        String uniao = String.join(" UNION ALL ", tabelas.stream().map(t -> "SELECT " + literal(t.nome())
            + " AS tabela, a.rev, a.revtype, a.detalhes, "
            + "(SELECT jsonb_object_agg(e.key, e.value) FROM jsonb_each(to_jsonb(a)) e "
            + "WHERE e.key IN (SELECT jsonb_array_elements_text(" + literal(t.chaves()) + "::jsonb))) AS identidade "
            + "FROM auditoria." + identificador(t.nome()) + " a").toList());
        String from = " FROM (" + uniao + ") a JOIN auditoria.revinfo r ON r.rev = a.rev WHERE 1=1";
        var params = new MapSqlParameterSource();
        if (idsPermitidos.isPresent()) {
            params.addValue("escopo", idsPermitidos.get().stream().map(String::valueOf).toList());
            from += " AND ((a.tabela = 'estudantes_aud' AND a.identidade->>'id' IN (:escopo)) OR a.identidade->>'estudante_id' IN (:escopo) OR a.detalhes->'antes'->>'estudante_id' IN (:escopo) OR a.detalhes->'depois'->>'estudante_id' IN (:escopo))";
        }

        if (tipo != null) { from += " AND a.revtype = :tipo"; params.addValue("tipo", tipo); }
        ZoneId zona = ZoneId.of("America/Sao_Paulo");
        if (inicio != null) { from += " AND r.revtstmp >= :inicio"; params.addValue("inicio", inicio.atStartOfDay(zona).toInstant().toEpochMilli()); }
        if (fim != null) { from += " AND r.revtstmp < :fim"; params.addValue("fim", fim.plusDays(1).atStartOfDay(zona).toInstant().toEpochMilli()); }
        if (!registro.isBlank()) {
            from += " AND EXISTS (SELECT 1 FROM jsonb_each_text(a.identidade) e WHERE e.value = :registro)";
            params.addValue("registro", registro.trim());
        }
        long total = jdbc.queryForObject("SELECT count(*)" + from, params, Long.class);
        params.addValue("limite", tamanho).addValue("offset", (long) pagina * tamanho);
        List<Registro> registros = jdbc.query("SELECT a.*, r.revtstmp" + from
            + " ORDER BY a.rev DESC, a.tabela, a.identidade::text LIMIT :limite OFFSET :offset", params,
            (rs, i) -> new Registro(rs.getString("tabela"), rs.getInt("rev"), Instant.ofEpochMilli(rs.getLong("revtstmp")),
                json(rs.getString("identidade")), switch (rs.getInt("revtype")) { case 0 -> "ADD"; case 1 -> "MOD"; default -> "DEL"; },
                json(rs.getString("detalhes"))));
        return new Consulta(registros, total);
    }
}
