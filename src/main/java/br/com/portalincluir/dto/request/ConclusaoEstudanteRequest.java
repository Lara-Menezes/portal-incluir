package br.com.portalincluir.dto.request;
import java.time.LocalDate;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
public record ConclusaoEstudanteRequest(@NotNull @PastOrPresent LocalDate dataConclusao) {}
