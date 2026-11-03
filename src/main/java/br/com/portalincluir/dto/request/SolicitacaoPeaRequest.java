package br.com.portalincluir.dto.request;
import jakarta.validation.constraints.*;
import java.util.Set;
public record SolicitacaoPeaRequest(
    @NotBlank @Pattern(regexp="PROFESSORES|SETOR|TODOS") String modalidade,
    @NotBlank @Size(max=150) String contexto,
    @Size(max=2000) String orientacoes,
    @Size(max=120) String setor,
    @Size(max=500) Set<@NotNull @Positive Long> professorIds
) {}
