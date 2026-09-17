package eg.com.ef.tsa.lookupapi.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "VOUCHER_ENTRY_MODE")
@Getter
@Setter
@NoArgsConstructor
public class VoucherEntryMode extends Auditable implements MutableLookup {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "VOUCHER_ENTRY_MODE_SEQ")
    @SequenceGenerator(name = "VOUCHER_ENTRY_MODE_SEQ", sequenceName = "VOUCHER_ENTRY_MODE_SEQ", allocationSize = 1)
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
