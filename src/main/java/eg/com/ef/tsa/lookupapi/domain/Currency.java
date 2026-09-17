package eg.com.ef.tsa.lookupapi.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "CURRENCY")
@Getter
@Setter
@NoArgsConstructor
public class Currency extends Auditable implements MutableLookup {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CURRENCY_SEQ")
    @SequenceGenerator(name = "CURRENCY_SEQ", sequenceName = "CURRENCY_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "CODE")
    private String code;

    @Column(name = "AR_NAME")
    private String arName;

    @Column(name = "EN_NAME")
    private String enName;

    @Column(name = "SYMBOL")
    private String symbol;
}
