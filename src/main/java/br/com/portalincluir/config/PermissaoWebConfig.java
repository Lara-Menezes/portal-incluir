package br.com.portalincluir.config;

import br.com.portalincluir.service.PermissaoService;
import br.com.portalincluir.service.GestaoUsuarioService;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Configuration @RequiredArgsConstructor
public class PermissaoWebConfig implements WebMvcConfigurer {
    private final PermissaoService acesso;
    private final br.com.portalincluir.repository.UsuarioRepository usuarios;
    @Override public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
                if (request.getMethod().equals("OPTIONS")) return true;
                String path = request.getRequestURI().substring(request.getContextPath().length());
                if (path.startsWith("/api/auth/") || !path.startsWith("/api/")) return true;
                // O controller do fluxo valida cada operacao e a atribuicao da etapa.
                if (path.matches("/api/estudantes/[0-9]+/fluxo(/.*)?")) { acesso.autenticado(request); return true; }
                boolean leitura = request.getMethod().equals("GET");
                String permission = null;
                if (path.startsWith("/api/usuarios")) permission = path.endsWith("/permissoes") ? "permissoes.gerenciar" : leitura ? "usuarios" : "usuarios.gerenciar";
                else if (path.startsWith("/api/auditorias")) permission = "auditorias";
                else if (path.matches("/api/estudantes/[0-9]+/historico(/.*)?")) permission = "auditorias";
                else if (path.matches("/api/estudantes/[0-9]+/profissionais(/.*)?")) permission = leitura && !path.endsWith("/disponiveis") ? "estudantes.consultar" : "estudantes.vincular";
                else if (path.startsWith("/api/estudantes")) permission = leitura ? "estudantes.consultar" : "estudantes.editar";
                else if (path.startsWith("/api/planos-acao")) permission = "planos.gerar";
                else if (path.startsWith("/api/planos-educacionais")) permission = leitura ? "pei.consultar" : "pei.editar";
                else if (path.startsWith("/api/adaptacoes-pedagogicas")) permission = leitura ? "pedagogico.consultar" : "pedagogico.editar";
                if (permission != null) {
                    if (!leitura && path.startsWith("/api/estudantes") && (path.equals("/api/estudantes") || request.getMethod().equals("PUT"))) { acesso.exigir(request, "anamnese"); acesso.exigir(request, "sensivel.consultar"); }
                    if (!leitura && (path.startsWith("/api/planos-acao") || path.startsWith("/api/planos-educacionais")) && (request.getMethod().equals("DELETE") || path.endsWith("/arquivar") || path.endsWith("/desarquivar"))) { acesso.exigir(request, "documentos.gerenciar"); return true; }

                    if (leitura && (path.startsWith("/api/planos-acao") || path.startsWith("/api/planos-educacionais"))) {
                        var usuario = acesso.autenticado(request);
                        var permissoes = PermissaoService.efetivas(usuario);
                        if (permissoes.contains("documentos") || permissoes.contains("relatorios")) return true;
                    }
                    acesso.exigir(request, permission); return true;
                }
                if (path.matches("/api/(coordenadores|professores|equipe-multidisciplinar|administradores)(/.*)?")) {
                    var usuario = acesso.autenticado(request);
                    String prefixo = switch(usuario.getPerfil()) { case COORDENADOR -> "coordenadores"; case PROFESSOR -> "professores"; case ADMINISTRADOR -> "administradores"; case EQUIPE_MULTIDISCIPLINAR -> "equipe-multidisciplinar"; };
                    Object id = new BeanWrapperImpl(GestaoUsuarioService.profissional(usuario)).getPropertyValue("id");
                    String proprio = "/api/" + prefixo + "/" + id;
                    if (!leitura && (!request.getMethod().equals("PUT") || !path.matches("/api/[^/]+/[0-9]+"))) {
                        boolean vinculado = usuarios.findAll().stream().anyMatch(u -> {
                            String tipo = switch(u.getPerfil()) { case COORDENADOR -> "coordenadores"; case PROFESSOR -> "professores"; case ADMINISTRADOR -> "administradores"; case EQUIPE_MULTIDISCIPLINAR -> "equipe-multidisciplinar"; };
                            Object profissionalId = new BeanWrapperImpl(GestaoUsuarioService.profissional(u)).getPropertyValue("id");
                            String raiz = "/api/" + tipo + "/" + profissionalId;
                            return path.equals(raiz) || path.startsWith(raiz + "/");
                        });
                        if (vinculado) throw new ResponseStatusException(HttpStatus.CONFLICT, "Gerencie a conta pela tela de usuarios para preservar seus vinculos e acessos.");
                    }
                    if (leitura && path.equals(proprio + "/dashboard")) { acesso.exigir(request, "estudantes.consultar"); return true; }
                    if (path.equals(proprio)) return true;
                    acesso.exigir(request, leitura ? "usuarios" : "usuarios.gerenciar");
                }
                return true;
            }
        }).addPathPatterns("/api/**");
    }
}
