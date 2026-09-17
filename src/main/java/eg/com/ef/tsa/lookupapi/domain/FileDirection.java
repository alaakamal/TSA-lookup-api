package eg.com.ef.tsa.lookupapi.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "FILE_DIRECTION")
@Getter
@Setter
@NoArgsConstructor
public class FileDirection extends Auditable implements MutableLookup {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FILE_DIRECTION_SEQ")
    @SequenceGenerator(name = "FILE_DIRECTION_SEQ", sequenceName = "FILE_DIRECTION_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "CODE")
    private String code;

    @Column(name = "AR_NAME")
    private String arName;

    @Column(name = "EN_NAME")
    private String enName;
}
