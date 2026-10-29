package br.com.portalincluir.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.util.Set;
import br.com.portalincluir.enums.TipoNecessidadeEspecifica;
import br.com.portalincluir.enums.TipoCondicaoAssociada;
import br.com.portalincluir.enums.TipoAtendimentoCampus;
import br.com.portalincluir.enums.TipoAcessibilidadeFisica;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EstudanteRequest {
    private boolean anamneseConcluida;

    @NotBlank
    @Size(max = 150)
    private String nomeCompleto;

    @NotNull
    @Past
    private LocalDate dataNascimento;

    @NotBlank
    @Size(max = 150)
    private String curso;

    @NotBlank
    @Size(max = 30)
    private String matricula;

    @NotBlank
    @Size(max = 20)
    private String periodoIngresso;

    @Size(max = 20)
    private String telefone;

    @Email
    @Size(max = 150)
    private String email;

    @Size(max = 150)
    private String nomeResponsavel;

    @Size(max = 20)
    private String telefoneResponsavel;

    @Email
    @Size(max = 150)
    private String emailResponsavel;

    private Boolean possuiNecessidadeEspecifica;

    private Set<TipoNecessidadeEspecifica> necessidadesEspecificas;

    @Size(max = 500)
    private String outraNecessidadeEspecifica;

    private Set<TipoCondicaoAssociada> condicoesAssociadas;

    @Size(max = 500)
    private String outraCondicaoAssociada;

    private Set<TipoAtendimentoCampus> atendimentosCampus;

    private Set<TipoAcessibilidadeFisica> acessibilidadesFisicas;

    @Size(max = 500)
    private String outraAcessibilidadeFisica;

    private Boolean possuiOutraNecessidadeAtendimento;

    @Size(max = 1000)
    private String descricaoOutraNecessidadeAtendimento;

}