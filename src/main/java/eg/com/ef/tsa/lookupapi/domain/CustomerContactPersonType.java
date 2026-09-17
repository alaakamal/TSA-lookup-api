package eg.com.ef.tsa.lookupapi.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Maps to CUSTOMER_CONTACT_PERN_TYPE. This table was NOT found in populateTSALookups.sql
 * during the audit that preceded this API - confirm its actual population source
 * before relying on this entity in production (see the Lookup Tables Register report).
 */
@Entity
@Table(name = "CUSTOMER_CONTACT_PERN_TYPE")
@Getter
@Setter
@NoArgsConstructor
public class CustomerContactPersonType extends Auditable implements MutableLookup {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CUSTOMER_CONTACT_PERN_TYPE_SEQ")
    @SequenceGenerator(name = "CUSTOMER_CONTACT_PERN_TYPE_SEQ", sequenceName = "CUSTOMER_CONTACT_PERN_TYPE_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "CODE")
    private String code;

    @Column(name = "AR_NAME")
    private String arName;

    @Column(name = "EN_NAME")
    private String enName;
}
