package eg.com.ef.tsa.lookupapi.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Generic Country / City / Governorate list - the same table backs several different
 * relations elsewhere in the schema (disambiguated by the referencing FK, not by a
 * column on this table itself).
 */
@Entity
@Table(name = "OTHER_ENTITY")
@Getter
@Setter
@NoArgsConstructor
public class OtherEntity extends Auditable implements MutableLookup {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "OTHER_ENTITY_SEQ")
    @SequenceGenerator(name = "OTHER_ENTITY_SEQ", sequenceName = "OTHER_ENTITY_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "CODE")
    private String code;

    @Column(name = "AR_NAME")
    private String arName;

    @Column(name = "EN_NAME")
    private String enName;
}
