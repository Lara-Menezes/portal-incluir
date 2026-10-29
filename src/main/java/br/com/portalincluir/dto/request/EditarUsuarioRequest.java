package br.com.portalincluir.dto.request;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
public record EditarUsuarioRequest(
    @NotBlank @Size(max=150) String nome,
    @NotBlank @Email @Size(max=150) String email,
    @NotBlank @Size(max=30) String matricula,
    @Size(max=20) String telefone,
    @PastOrPresent LocalDate dataNascimento,
    @NotBlank @Size(max=120) String setor,
    @Size(max=120) String cargo,
    @Size(max=120) String disciplina,
    @Size(max=100) String areaAtuacao,
    @Size(max=30) String conselhoProfissional,
    @Size(max=50) String registroProfissional,
    @Size(max=6) java.util.Set<String> cursosAtuacao) {}
