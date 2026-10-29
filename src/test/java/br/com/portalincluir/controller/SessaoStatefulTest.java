package br.com.portalincluir.controller;

import br.com.portalincluir.dto.request.LoginRequest;
import br.com.portalincluir.dto.response.UsuarioResponse;
import br.com.portalincluir.enums.PerfilUsuario;
import br.com.portalincluir.model.Usuario;
import br.com.portalincluir.service.PermissaoService;
import br.com.portalincluir.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SessaoStatefulTest {
    private static final String LOGIN = "{\"email\":\"pessoa@example.invalid\",\"senha\":\"senha-valida\"}";

    @Test void loginInvalidoPreservaSessaoExistente() throws Exception {
        var service = mock(UsuarioService.class);
        var acesso = mock(PermissaoService.class);
        when(service.login(any(LoginRequest.class))).thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        var mvc = MockMvcBuilders.standaloneSetup(new AuthController(service, acesso)).build();
        var anterior = new MockHttpSession();
        anterior.setAttribute("usuarioAutenticadoId", 7L);
        mvc.perform(post("/api/auth/login").session(anterior).contentType(MediaType.APPLICATION_JSON).content(LOGIN))
            .andExpect(status().isUnauthorized());
        assertFalse(anterior.isInvalid());
        assertEquals(7L, anterior.getAttribute("usuarioAutenticadoId"));
    }

    @Test void loginValidoTrocaIdentificadorELogoutInvalidaSessao() throws Exception {
        var service = mock(UsuarioService.class);
        var acesso = mock(PermissaoService.class);
        var usuario = new UsuarioResponse(9L, "Pessoa", "pessoa@example.invalid", PerfilUsuario.PROFESSOR, 3L, Set.of("estudantes.consultar"));
        when(service.login(any(LoginRequest.class))).thenReturn(usuario);
        var mvc = MockMvcBuilders.standaloneSetup(new AuthController(service, acesso)).build();
        var anterior = new MockHttpSession();
        anterior.setAttribute("usuarioAutenticadoId", 7L);
        var result = mvc.perform(post("/api/auth/login").session(anterior).contentType(MediaType.APPLICATION_JSON).content(LOGIN))
            .andExpect(status().isOk()).andReturn();
        var atual = (MockHttpSession) result.getRequest().getSession(false);
        assertTrue(anterior.isInvalid());
        assertNotNull(atual);
        assertNotEquals(anterior.getId(), atual.getId());
        assertEquals(9L, atual.getAttribute("usuarioAutenticadoId"));
        mvc.perform(post("/api/auth/logout").session(atual)).andExpect(status().isNoContent());
        assertTrue(atual.isInvalid());
        mvc.perform(post("/api/auth/logout")).andExpect(status().isNoContent());
    }

    @Test void consultaDeSessaoNaoPermiteCacheENaoCriaSessaoAusente() throws Exception {
        var service = mock(UsuarioService.class);
        var acesso = mock(PermissaoService.class);
        var usuario = new Usuario();
        when(acesso.autenticado(any(HttpServletRequest.class))).thenReturn(usuario);
        when(service.resposta(usuario)).thenReturn(new UsuarioResponse(9L, "Pessoa", "pessoa@example.invalid", PerfilUsuario.PROFESSOR, 3L, Set.of()));
        var mvc = MockMvcBuilders.standaloneSetup(new AuthController(service, acesso)).build();
        mvc.perform(get("/api/auth/me").session(new MockHttpSession()))
            .andExpect(status().isOk()).andExpect(header().string("Cache-Control", "no-store"));
        when(acesso.autenticado(any(HttpServletRequest.class))).thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        var result = mvc.perform(get("/api/auth/me")).andExpect(status().isUnauthorized()).andReturn();
        assertNull(result.getRequest().getSession(false));
    }
}
