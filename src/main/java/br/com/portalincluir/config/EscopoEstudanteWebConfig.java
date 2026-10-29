package br.com.portalincluir.config;
import br.com.portalincluir.service.EscopoEstudanteService;
import br.com.portalincluir.repository.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.regex.Pattern;

@Configuration @RequiredArgsConstructor
public class EscopoEstudanteWebConfig implements WebMvcConfigurer {
    private final EscopoEstudanteService escopo;
    private final PlanoAcaoRepository planos;
    private final PlanoEducacionalRepository peis;
    private final AdaptacaoPedagogicaRepository adaptacoes;
    @Override public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
                if (request.getMethod().equals("OPTIONS")) return true;
                String path = request.getRequestURI().substring(request.getContextPath().length());
                if (path.matches("/api/coordenadores/[0-9]+/dashboard")) { escopo.exigirGestao(); return true; }
                if (path.equals("/api/estudantes") && request.getMethod().equals("POST")) { escopo.exigirGestao(); return true; }
                var estudante = Pattern.compile("^/api/estudantes/([0-9]+)(?:/.*)?$").matcher(path);
                if (estudante.matches()) { escopo.exigir(Long.valueOf(estudante.group(1))); return true; }
                var porEstudante = Pattern.compile("^/api/(?:planos-acao|planos-educacionais|adaptacoes-pedagogicas)/estudante/([0-9]+)(?:/.*)?$").matcher(path);
                if (porEstudante.matches()) { escopo.exigir(Long.valueOf(porEstudante.group(1))); return true; }
                var documento = Pattern.compile("^/api/(planos-acao|planos-educacionais|adaptacoes-pedagogicas)/([0-9]+)(?:/.*)?$").matcher(path);
                if (documento.matches()) {
                    Long id = Long.valueOf(documento.group(2));
                    Long estudanteId = switch(documento.group(1)) {
                        case "planos-acao" -> planos.findById(id).map(p -> p.getEstudante().getId()).orElseThrow(EscopoEstudanteWebConfig::ausente);
                        case "planos-educacionais" -> peis.findById(id).map(p -> p.getEstudante().getId()).orElseThrow(EscopoEstudanteWebConfig::ausente);
                        default -> adaptacoes.findById(id).map(p -> p.getEstudante().getId()).orElseThrow(EscopoEstudanteWebConfig::ausente);
                    };
                    escopo.exigir(estudanteId);
                }
                return true;
            }
        }).addPathPatterns("/api/**").order(20);
    }
    private static ResponseStatusException ausente() { return new ResponseStatusException(HttpStatus.NOT_FOUND, "Registro não encontrado."); }
}
