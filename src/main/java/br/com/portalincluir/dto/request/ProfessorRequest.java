package br.com.portalincluir.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfessorRequest {

    @NotBlank
    @Size(max = 150)
    private String nome;

    @NotBlank
    @Email
    @Size(max = 150)
    private String email;

    @Size(max = 20)
    private String telefone;

    @Past
    private LocalDate dataNascimento;

    @NotBlank
    @Size(max = 30)
    private String matricula;

    @NotBlank
    @Size(max = 120)
    private String disciplina;

    @NotBlank
    @Size(max = 120)
    private String setor;

}
