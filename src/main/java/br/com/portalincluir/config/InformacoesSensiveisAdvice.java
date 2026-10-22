package br.com.portalincluir.config;
import br.com.portalincluir.service.PermissaoService;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.*;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import java.util.Set;

@RestControllerAdvice @RequiredArgsConstructor
public class InformacoesSensiveisAdvice implements ResponseBodyAdvice<Object> {
    private final PermissaoService acesso;
    private final ObjectMapper mapper;
    private final HttpServletRequest request;
    private static final Set<String> CAMPOS = Set.of("possuiNecessidadeEspecifica", "necessidadesEspecificas", "outraNecessidadeEspecifica", "condicoesAssociadas", "outraCondicaoAssociada", "atendimentosCampus", "acessibilidadesFisicas", "outraAcessibilidadeFisica", "possuiOutraNecessidadeAtendimento", "descricaoOutraNecessidadeAtendimento", "possuiComorbidade", "comorbidades", "usaMedicacaoContinua", "medicacoes", "frequentaServicoApoio", "servicosApoio", "possuiDocumentosComprobatorios", "documentosComprobatorios");
    @Override public boolean supports(MethodParameter method, Class<? extends HttpMessageConverter<?>> converter) { return org.springframework.http.converter.json.MappingJackson2HttpMessageConverter.class.isAssignableFrom(converter); }
    @Override public Object beforeBodyWrite(Object body, MethodParameter method, MediaType type, Class<? extends HttpMessageConverter<?>> converter, ServerHttpRequest req, ServerHttpResponse response) {
        String path = request.getRequestURI();
        if (!path.startsWith(request.getContextPath() + "/api/") || path.contains("/auth/")) return body;
        response.getHeaders().setCacheControl("no-store");
        if (body == null || !request.getMethod().equals("GET") || !(path.contains("/estudantes") || path.contains("/planos-acao") || path.contains("/planos-educacionais"))) return body;
        // Error bodies must preserve the original authorization error.
        if (response instanceof ServletServerHttpResponse servlet && servlet.getServletResponse().getStatus() >= 400) return body;
        if (PermissaoService.efetivas(acesso.autenticado(request)).contains("sensivel.consultar")) return body;
        JsonNode json = mapper.valueToTree(body); ocultar(json); return json;
    }
    private void ocultar(JsonNode node) {
        if (node.isObject()) { ObjectNode objeto = (ObjectNode)node; CAMPOS.forEach(campo -> { if (objeto.has(campo)) objeto.putNull(campo); }); }
        node.elements().forEachRemaining(this::ocultar);
    }
}
