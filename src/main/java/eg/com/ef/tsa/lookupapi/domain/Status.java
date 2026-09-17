package eg.com.ef.tsa.lookupapi.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Heavily FK-referenced across the transactional schema (vouchers, accounts, files...).
 * The service layer's delete guard matters more for this table than almost any other.
 */
@Entity
@Table(name = "STATUS")
@Getter
@Setter
@NoArgsConstructor
public class Status extends Auditable implements MutableLookup {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "STATUS_SEQ")
    @SequenceGenerator(name = "STATUS_SEQ", sequenceName = "STATUS_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "CODE")
    private String code;

    @Column(name = "AR_NAME")
    private String arName;

    @Column(name = "EN_NAME")
    private String enName;
}
