package br.com.portalincluir.config;
import br.com.portalincluir.service.EscopoEstudanteService;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ArrayNode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.*;
import org.springframework.core.annotation.Order;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.*;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import java.util.Set;

@RestControllerAdvice @RequiredArgsConstructor @Order(Ordered.HIGHEST_PRECEDENCE)
public class EscopoEstudanteAdvice implements ResponseBodyAdvice<Object> {
    private final EscopoEstudanteService escopo;
    private final ObjectMapper mapper;
    private final HttpServletRequest request;
    @Override public boolean supports(MethodParameter method, Class<? extends HttpMessageConverter<?>> converter) { return org.springframework.http.converter.json.MappingJackson2HttpMessageConverter.class.isAssignableFrom(converter); }
    @Override public Object beforeBodyWrite(Object body, MethodParameter method, MediaType type, Class<? extends HttpMessageConverter<?>> converter, ServerHttpRequest req, ServerHttpResponse response) {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        if (body == null || !request.getMethod().equals("GET") || path.contains("/profissionais") || !path.matches("/api/(estudantes|planos-acao|planos-educacionais|adaptacoes-pedagogicas)(/.*)?")) return body;
        if (response instanceof ServletServerHttpResponse servlet && servlet.getServletResponse().getStatus() >= 400) return body;
        var permitidos = escopo.idsPermitidos();
        if (permitidos.isEmpty()) return body;
        JsonNode json = mapper.valueToTree(body);
        if (!json.isArray()) return body;
        Set<Long> ids = permitidos.get(); ArrayNode resultado = mapper.createArrayNode();
        String campo = path.startsWith("/api/estudantes") ? "id" : "estudanteId";
        json.forEach(item -> { JsonNode id = item.get(campo); if (id != null && id.isIntegralNumber() && ids.contains(id.longValue())) resultado.add(item); });
        return resultado;
    }
}
