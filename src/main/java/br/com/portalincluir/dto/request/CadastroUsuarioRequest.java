package br.com.portalincluir.dto.request;

import br.com.portalincluir.enums.PerfilUsuario;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter @Setter
public class CadastroUsuarioRequest {
    @NotBlank @Size(max = 150) private String nome;
    @NotBlank @Email @Size(max = 150) private String email;
    @NotBlank @Size(min = 6, max = 72) private String senha;
    @NotNull private PerfilUsuario perfil;
    @NotBlank @Size(max = 30) private String matricula;
    @Size(max = 20) private String telefone;
    @Past private LocalDate dataNascimento;
    @Size(max = 120) private String cargo;
    @Size(max = 120) private String setor;
    @Size(max = 120) private String disciplina;
    @Size(max = 100) private String areaAtuacao;
}
