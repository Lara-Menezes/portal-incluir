package br.com.portalincluir.controller;
import br.com.portalincluir.service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
class AuditoriaEndpointTest {
  @Test void rotasRegistradasRetornamHistoricoComPermissao() throws Exception {
    var service = mock(AuditoriaService.class); var acesso = mock(PermissaoService.class);
    when(service.consultar("", "", null, null, "", 0, 100)).thenReturn(new AuditoriaService.Consulta(List.of(), 0));
    when(service.tabelas()).thenReturn(List.of("fluxos_estudantes_aud"));
    var mvc = MockMvcBuilders.standaloneSetup(new AuditoriaController(service, acesso)).build();
    mvc.perform(get("/api/auditorias").param("pagina","0").param("tamanho","100")).andExpect(status().isOk()).andExpect(header().string("Cache-Control","no-store")).andExpect(jsonPath("total").value(0));
    mvc.perform(get("/api/auditorias/tabelas")).andExpect(status().isOk()).andExpect(jsonPath("$[0]").value("fluxos_estudantes_aud"));
    verify(acesso, times(2)).exigir(any(HttpServletRequest.class), eq("auditorias"));
  }
  @Test void semPermissaoNaoConsultaDados() throws Exception {
    var service = mock(AuditoriaService.class); var acesso = mock(PermissaoService.class);
    when(acesso.exigir(any(HttpServletRequest.class), eq("auditorias"))).thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN));
    var mvc = MockMvcBuilders.standaloneSetup(new AuditoriaController(service, acesso)).build();
    mvc.perform(get("/api/auditorias")).andExpect(status().isForbidden());
    mvc.perform(get("/api/auditorias/tabelas")).andExpect(status().isForbidden());
    verifyNoInteractions(service);
  }
}
