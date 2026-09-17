package eg.com.ef.tsa.lookupapi.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "SYSTEM_PARAMETERS_TYPE")
@Getter
@Setter
@NoArgsConstructor
public class SystemParametersType extends Auditable implements MutableLookup {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SYSTEM_PARAMETERS_TYPE_SEQ")
    @SequenceGenerator(name = "SYSTEM_PARAMETERS_TYPE_SEQ", sequenceName = "SYSTEM_PARAMETERS_TYPE_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "CODE")
    private String code;

    @Column(name = "AR_NAME")
    private String arName;

    @Column(name = "EN_NAME")
    private String enName;
}
