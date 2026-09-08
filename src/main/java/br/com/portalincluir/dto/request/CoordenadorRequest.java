package br.com.portalincluir.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class CoordenadorRequest {

    @NotBlank
    @Size(max = 150)
    private String nome;

    @NotBlank
    @Email
    @Size(max = 150)
    private String email;

    @Pattern(regexp = "^$|^[0-9()+\\-\\s]{8,20}$", message = "telefone inválido")
    private String telefone;

    @Past
    private LocalDate dataNascimento;

    @NotBlank
    @Size(max = 30)
    private String matricula;

    @NotBlank
    @Size(max = 120)
    private String cargo;

    @NotBlank
    @Size(max = 120)
    private String setor;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9._-]{4,80}$", message = "identificador de acesso inválido")
    private String identificadorAcesso;

    @Size(min = 8, max = 72)
    private String senha;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }
    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }
    public String getSetor() { return setor; }
    public void setSetor(String setor) { this.setor = setor; }
    public String getIdentificadorAcesso() { return identificadorAcesso; }
    public void setIdentificadorAcesso(String identificadorAcesso) { this.identificadorAcesso = identificadorAcesso; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
}
