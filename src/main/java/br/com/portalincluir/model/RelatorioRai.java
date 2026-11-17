package br.com.portalincluir.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import java.time.LocalDateTime;

@Entity @Audited @Getter @Setter
@Table(name = "relatorios_rai")
public class RelatorioRai {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "estudante_id", nullable = false) private Estudante estudante;
    @Column(nullable = false, columnDefinition = "text") private String conteudo;
    @Version private long versao;
    @Column(nullable = false) private LocalDateTime dataAtualizacao;
    private LocalDateTime concluidoEm;
}
