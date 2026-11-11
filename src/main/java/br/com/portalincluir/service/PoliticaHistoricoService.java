package br.com.portalincluir.service;

import br.com.portalincluir.model.Estudante;
import br.com.portalincluir.repository.EstudanteRepository;
import java.time.*;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PoliticaHistoricoService {
    private final Clock clock;
    private final EstudanteRepository estudantes;
    private final jakarta.persistence.EntityManager em;
    public PoliticaHistoricoService(Clock clock, EstudanteRepository estudantes, jakarta.persistence.EntityManager em) {
        this.clock = clock;
        this.estudantes = estudantes;
        this.em = em;
    }
    public LocalDate hoje() { return LocalDate.now(clock); }
    public LocalDateTime agora() { return LocalDateTime.now(clock); }
    public boolean disponivel(Estudante estudante) {
        return estudante.getDataLimiteRetencao() == null || hoje().isBefore(estudante.getDataLimiteRetencao());
    }
    public void exigirDisponivel(Estudante estudante) {
        if (!disponivel(estudante)) throw new ResponseStatusException(HttpStatus.GONE, "Prazo de retencao encerrado");
    }
    public Estudante bloquear(Long id) {
        Estudante estudante = estudantes.buscarComBloqueio(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Estudante nao encontrado"));
        em.refresh(estudante, jakarta.persistence.LockModeType.PESSIMISTIC_WRITE);
        return estudante;
    }
    public void exigirEditavel(Estudante estudante) {
        Estudante atual = bloquear(estudante.getId());
        exigirDisponivel(atual);
        if (atual.isConcluido()) throw new ResponseStatusException(HttpStatus.CONFLICT, "Historico concluido e somente leitura");
    }
    public void exigirMesmoEstudante(Estudante estudante, Long novoId) {
        if (!estudante.getId().equals(novoId)) throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Nao e permitido transferir registros entre estudantes");
    }
}
