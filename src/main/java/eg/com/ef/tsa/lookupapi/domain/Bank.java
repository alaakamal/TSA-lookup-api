package eg.com.ef.tsa.lookupapi.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Maps to the existing BANK table. Unlike every other lookup, this table has no CODE
 * column (confirmed against the legacy TSA-JPA entity) - getCode() is intentionally null.
 */
@Entity
@Table(name = "BANK")
@Getter
@Setter
@NoArgsConstructor
public class Bank extends Auditable implements MutableLookup {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BANK_SEQ")
    // NOTE: sequence name follows the project-wide <TABLE>_SEQ convention but was not
    // directly confirmed for BANK - verify against the DB before first deploy.
    @SequenceGenerator(name = "BANK_SEQ", sequenceName = "BANK_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "AR_NAME")
    private String arName;

    @Column(name = "EN_NAME")
    private String enName;

    @Override
    public String getCode() {
        return null;
    }

    @Override
    public void setCode(String code) {
        // no-op: BANK has no CODE column
    }
}
