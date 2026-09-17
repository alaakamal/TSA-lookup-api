package eg.com.ef.tsa.lookupapi.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ENTITY_LEVEL")
@Getter
@Setter
@NoArgsConstructor
public class EntityLevel extends Auditable implements MutableLookup {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ENTITY_LEVEL_SEQ")
    @SequenceGenerator(name = "ENTITY_LEVEL_SEQ", sequenceName = "ENTITY_LEVEL_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "CODE")
    private String code;

    @Column(name = "AR_NAME")
    private String arName;

    @Column(name = "EN_NAME")
    private String enName;
}
