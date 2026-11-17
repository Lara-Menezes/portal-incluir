package br.com.portalincluir.dto.response;

import br.com.portalincluir.enums.StatusCoordenador;
import br.com.portalincluir.model.Coordenador;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CoordenadorResponse {
    public record Resumo(long estudantesAtivos, long professoresAtivos, long planosAtivos,
                         long planosArquivados, List<EstudanteResumo> estudantesRecentes) {}

    public record EstudanteResumo(Long id, String nome, String curso, String matricula, boolean ativo) {}

    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private LocalDate dataNascimento;
    private String matricula;
    private String cargo;
    private String setor;
    private StatusCoordenador status;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    public CoordenadorResponse(Coordenador coordenador) {
        this.id = coordenador.getId();
        this.nome = coordenador.getNome();
        this.email = coordenador.getEmail();
        this.telefone = coordenador.getTelefone();
        this.dataNascimento = coordenador.getDataNascimento();
        this.matricula = coordenador.getMatricula();
        this.cargo = coordenador.getCargo();
        this.setor = coordenador.getSetor();
        this.status = coordenador.getStatus();
        this.dataCriacao = coordenador.getDataCriacao();
        this.dataAtualizacao = coordenador.getDataAtualizacao();
    }
}
