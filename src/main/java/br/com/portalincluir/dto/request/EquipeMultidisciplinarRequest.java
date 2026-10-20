package br.com.portalincluir.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EquipeMultidisciplinarRequest {

    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 150)
    private String nome;

    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "Informe um e-mail válido")
    @Size(max = 150)
    private String email;

    @Pattern(
            regexp = "^$|^[0-9()+\\-\\s]{8,20}$",
            message = "Informe um telefone válido"
    )
    private String telefone;

    @Past(message = "A data de nascimento deve estar no passado")
    private LocalDate dataNascimento;

    @NotBlank(message = "A matrícula é obrigatória")
    @Size(max = 30)
    private String matricula;

    @NotBlank(message = "A área de atuação é obrigatória")
    @Size(max = 100)
    private String areaAtuacao;

    @NotBlank(message = "O cargo é obrigatório")
    @Size(max = 120)
    private String cargo;

    @NotBlank(message = "O setor é obrigatório")
    @Size(max = 120)
    private String setor;

    @Size(max = 30)
    private String conselhoProfissional;

    @Size(max = 50)
    private String registroProfissional;

}