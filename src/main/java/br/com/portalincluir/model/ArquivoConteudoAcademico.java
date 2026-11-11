package br.com.portalincluir.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/** Binario separado para que as consultas de materiais nao carreguem os anexos. */
@Entity @Table(name = "arquivos_conteudo_academico") @Getter @Setter
@org.hibernate.envers.Audited
public class ArquivoConteudoAcademico {
    @Id
    private Long id;
    @MapsId @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "conteudo_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ConteudoAcademico conteudo;
    @Column(nullable = false, length = 10485760)
    @org.hibernate.envers.NotAudited
    private byte[] dados;
}
