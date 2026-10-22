package br.com.portalincluir.controller;

import br.com.portalincluir.enums.PerfilUsuario;
import br.com.portalincluir.model.Usuario;
import br.com.portalincluir.repository.UsuarioRepository;
import br.com.portalincluir.service.AuditoriaService;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuditoriaControllerTest {
    private final AuditoriaService service = mock(AuditoriaService.class);
    private final UsuarioRepository usuarios = mock(UsuarioRepository.class);
    private final AuditoriaController controller = new AuditoriaController(service, usuarios);

    @Test void anonimoNaoConsultaTabelasOuRegistros() {
        var request = new MockHttpServletRequest();
        assertEquals(401, assertThrows(ResponseStatusException.class, () -> controller.tabelas(request)).getStatusCode().value());
        assertEquals(401, assertThrows(ResponseStatusException.class, () -> controller.consultar(request, "", "", null, null, "", 0, 20)).getStatusCode().value());
        verifyNoInteractions(service);
    }
    @Test void perfilSemPermissaoNaoConsultaAuditorias() {
        for (var perfil : List.of(PerfilUsuario.PROFESSOR, PerfilUsuario.EQUIPE_MULTIDISCIPLINAR)) {
            var request = request(perfil);
            assertEquals(403, assertThrows(ResponseStatusException.class, () -> controller.tabelas(request)).getStatusCode().value());
        }
        verifyNoInteractions(service);
    }
    @Test void coordenadorEAdministradorConsultamSemCache() {
        when(service.tabelas()).thenReturn(List.of("estudantes_aud"));
        for (var perfil : List.of(PerfilUsuario.COORDENADOR, PerfilUsuario.ADMINISTRADOR)) {
            var resposta = controller.tabelas(request(perfil));
            assertEquals(List.of("estudantes_aud"), resposta.getBody());
            assertEquals("no-store", resposta.getHeaders().getCacheControl());
        }
    }
    @Test void usuarioExcluidoPerdeAcesso() {
        var request = new MockHttpServletRequest();
        request.getSession().setAttribute("usuarioAutenticadoId", 1L);
        when(usuarios.findById(1L)).thenReturn(Optional.empty());
        assertEquals(401, assertThrows(ResponseStatusException.class, () -> controller.tabelas(request)).getStatusCode().value());
    }
    private MockHttpServletRequest request(PerfilUsuario perfil) {
        var usuario = new Usuario(); usuario.setPerfil(perfil);
        switch (perfil) {
            case COORDENADOR -> usuario.setCoordenador(new br.com.portalincluir.model.Coordenador());
            case ADMINISTRADOR -> usuario.setAdministrador(new br.com.portalincluir.model.Administrador());
            case PROFESSOR -> usuario.setProfessor(new br.com.portalincluir.model.Professor());
            case EQUIPE_MULTIDISCIPLINAR -> usuario.setEquipeMultidisciplinar(new br.com.portalincluir.model.EquipeMultidisciplinar());
        }
        when(usuarios.findById(1L)).thenReturn(Optional.of(usuario));
        var request = new MockHttpServletRequest();
        request.getSession().setAttribute("usuarioAutenticadoId", 1L);
        return request;
    }
}
