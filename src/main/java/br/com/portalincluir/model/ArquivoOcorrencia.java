package br.com.portalincluir.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@org.hibernate.envers.Audited
@Entity @Table(name = "arquivos_ocorrencia") @Getter @Setter
public class ArquivoOcorrencia {
    @Id private Long id;
    @MapsId @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ocorrencia_id", nullable = false)
    @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
    private Ocorrencia ocorrencia;
    @org.hibernate.envers.NotAudited
    @Column(nullable = false, length = 10485760) private byte[] dados;
}
