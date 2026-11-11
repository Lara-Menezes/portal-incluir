
package br.com.portalincluir.dto.response;

import br.com.portalincluir.model.PlanoEducacional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlanoEducacionalResponse {

    private Long id;

    private Long estudanteId;
    private String estudanteNome;
    private String estudanteMatricula;
    private String estudanteCurso;

    private Long coordenadorId;
    private String coordenadorNome;

    private String semestreAno;
    private String historico;
    private String necessidadesEspecificas;
    private String conhecimentosHabilidadesInteresses;
    private String dificuldadesApresentadas;
    private String propostasAdaptacoesCurriculares;

    private List<AdaptacaoPedagogicaResponse> adaptacoesPedagogicas;

    private boolean arquivado;

    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    public PlanoEducacionalResponse(PlanoEducacional plano) {

        this.id = plano.getId();

        this.estudanteId = plano.getEstudante().getId();
        this.estudanteNome = plano.getEstudante().getNome();
        this.estudanteMatricula = plano.getEstudante().getMatricula();
        this.estudanteCurso = plano.getEstudante().getCurso();

        this.coordenadorId = plano.getCoordenador().getId();
        this.coordenadorNome = plano.getCoordenador().getNome();

        this.semestreAno = plano.getSemestreAno();
        this.historico = plano.getHistorico();
        this.necessidadesEspecificas =
                plano.getNecessidadesEspecificas();
        this.conhecimentosHabilidadesInteresses =
                plano.getConhecimentosHabilidadesInteresses();
        this.dificuldadesApresentadas =
                plano.getDificuldadesApresentadas();
        this.propostasAdaptacoesCurriculares =
                plano.getPropostasAdaptacoesCurriculares();

        this.adaptacoesPedagogicas =
                plano.getAdaptacoesPedagogicas()
                        .stream()
                        .map(AdaptacaoPedagogicaResponse::new)
                        .collect(Collectors.toList());

        this.arquivado = plano.isArquivado();

        this.dataCriacao = plano.getDataCriacao();
        this.dataAtualizacao = plano.getDataAtualizacao();
    }
}
