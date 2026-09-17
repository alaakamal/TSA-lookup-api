package eg.com.ef.tsa.lookupapi.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "FILE_TYPE")
@Getter
@Setter
@NoArgsConstructor
public class FileType extends Auditable implements MutableLookup {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FILE_TYPE_SEQ")
    @SequenceGenerator(name = "FILE_TYPE_SEQ", sequenceName = "FILE_TYPE_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "CODE")
    private String code;

    @Column(name = "AR_NAME")
    private String arName;

    @Column(name = "EN_NAME")
    private String enName;

    @Column(name = "AR_DESCRIPTION")
    private String arDescription;

    @Column(name = "EN_DESCRIPTION")
    private String enDescription;
}
