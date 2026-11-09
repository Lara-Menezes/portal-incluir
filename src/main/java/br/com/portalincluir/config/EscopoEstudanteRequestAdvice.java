package br.com.portalincluir.config;
import br.com.portalincluir.service.EscopoEstudanteService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;
import java.lang.reflect.Type;

@RestControllerAdvice @RequiredArgsConstructor
public class EscopoEstudanteRequestAdvice extends RequestBodyAdviceAdapter {
    private final EscopoEstudanteService escopo;
    @Override public boolean supports(MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converter) {
        // Materiais reutilizaveis nao exigem estudante. O service valida autoria e vinculo quando informado.
        return parameter.getParameterType().getPackageName().equals("br.com.portalincluir.dto.request")
                && parameter.getParameterType() != br.com.portalincluir.dto.request.ConteudoAcademicoRequest.class;
    }
    @Override public Object afterBodyRead(Object body, HttpInputMessage input, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converter) {
        var propriedades = new BeanWrapperImpl(body);
        if (propriedades.isReadableProperty("estudanteId")) escopo.exigir((Long)propriedades.getPropertyValue("estudanteId"));
        return body;
    }
}
