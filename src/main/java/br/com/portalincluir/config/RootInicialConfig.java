package br.com.portalincluir.config;
import br.com.portalincluir.repository.UsuarioRepository;
import br.com.portalincluir.service.UsuarioService;
import br.com.portalincluir.dto.request.CadastroUsuarioRequest;
import br.com.portalincluir.enums.PerfilUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component @RequiredArgsConstructor
public class RootInicialConfig implements ApplicationRunner {
  private final UsuarioRepository usuarios;
  private final UsuarioService contas;
  @Override @Transactional public void run(ApplicationArguments args) {
    if (usuarios.findByRootTrue().isPresent()) return;
    var r=new CadastroUsuarioRequest(); r.setNome("Administrador geral"); r.setEmail("root@portal-incluir.local"); r.setSenha("0123456789"); r.setMatricula("ROOT-SISTEMA"); r.setPerfil(PerfilUsuario.COORDENADOR); r.setCargo("Administrador geral"); r.setSetor("Portal Incluir");
    var criada=contas.cadastrar(r); var u=usuarios.findById(criada.id()).orElseThrow(); u.setRoot(true); u.setAdministracaoAprovada(true); usuarios.saveAndFlush(u);
  }
}
