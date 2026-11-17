package br.com.portalincluir.model;

import jakarta.persistence.*;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

/** Schema explicito tambem na sequencia, preservando a numeracao das revisoes migradas. */
@Entity
@RevisionEntity
@Table(name = "revinfo", schema = "auditoria")
public class RevisaoAuditoria {
    @Id
    @RevisionNumber
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "revisao_auditoria")
    @SequenceGenerator(name = "revisao_auditoria", sequenceName = "revinfo_seq",
        schema = "auditoria", allocationSize = 50)
    @Column(name = "rev")
    private int id;

    @RevisionTimestamp
    @Column(name = "revtstmp")
    private long timestamp;

    public int getId() { return id; }
    public long getTimestamp() { return timestamp; }
}
