package br.com.portalincluir.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
@Entity @Getter @Setter @org.hibernate.envers.Audited
@Table(name="solicitacoes_acesso", indexes=@Index(name="idx_solicitacao_status_data", columnList="status,criada_em"))
public class SolicitacaoAcesso {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Version private long versao;
  @Column(nullable=false, length=30) private String tipo="CADASTRO";
  private Long solicitanteId;
  @Column(nullable=false, length=150) private String nome;
  @Column(nullable=false, length=150) private String email;
  @Column(unique=true, length=150) private String emailPendente;
  @Column(nullable=false, length=40) private String perfil;
  @Column(nullable=false, length=20) private String status="PENDENTE";
  @Column(nullable=false, length=12000) private String dadosJson;
  @org.hibernate.envers.NotAudited @Column(length=100) private String senhaHash;
  @Column(name="criada_em", nullable=false) private LocalDateTime criadaEm;
  private LocalDateTime analisadaEm;
  private Long analisadaPor;
  private Long usuarioId;
  @Column(length=1000) private String motivo;
}
