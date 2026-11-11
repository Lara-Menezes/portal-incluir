package br.com.portalincluir.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
@Entity @Getter @Setter @org.hibernate.envers.Audited
@Table(name="fluxos_estudantes")
public class FluxoEstudante {
  @Id private Long id;
  @MapsId @OneToOne(optional=false) @JoinColumn(name="estudante_id") private Estudante estudante;
  @Version private long versao;
  @Column(nullable=false) private String anamneseStatus = "PENDENTE";
  @ManyToOne private Usuario anamneseResponsavel;
  @ManyToOne private Usuario planoResponsavel;
  @ManyToOne private Usuario peiResponsavel;
  @ManyToOne @org.hibernate.envers.Audited(targetAuditMode = org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED) private Coordenador coordenador;
}
