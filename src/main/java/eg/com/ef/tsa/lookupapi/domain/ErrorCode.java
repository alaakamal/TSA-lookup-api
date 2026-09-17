package eg.com.ef.tsa.lookupapi.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The one entity whose primary key is the business code itself (String, 3 chars) rather
 * than a surrogate numeric id - there is no ID column at all on ERROR_CODE. Otherwise it
 * follows the exact same AR_NAME/EN_NAME/AR_DESCRIPTION/EN_DESCRIPTION shape as every
 * other lookup; an earlier version of this class incorrectly assumed a single MESSAGE
 * column (unverified guess at the time) and failed Hibernate schema validation against
 * the real table - confirmed correct here against the legacy, production-validated
 * eg.com.ef.tsa.jpa.entity.file.ErrorCode entity in TSA-JPA.
 */
@Entity
@Table(name = "ERROR_CODE")
@Getter
@Setter
@NoArgsConstructor
public class ErrorCode extends Auditable implements MutableLookup {

    @Id
    @Column(name = "CODE", length = 3)
    @Getter(AccessLevel.NONE) // getId()/getCode() below are the real accessors for this field
    @Setter(AccessLevel.NONE)
    private String code;

    @Column(name = "AR_NAME", length = 70)
    private String arName;

    @Column(name = "EN_NAME", length = 50)
    private String enName;

    @Column(name = "AR_DESCRIPTION", length = 120)
    private String arDescription;

    @Column(name = "EN_DESCRIPTION", length = 100)
    private String enDescription;

    @Override
    public Object getId() {
        return code;
    }

    // "code" here doubles as the primary key itself - there's no separate business code
    // distinct from it, so it's treated as not applicable for the generic Lookup contract
    // (consistent with how Bank.getCode() returns null for a column it doesn't have).
    @Override
    public String getCode() {
        return null;
    }

    @Override
    public void setCode(String code) {
        // no-op: "code" as a business field is not applicable - see getCode() above.
        // Setting the actual primary key on create goes through assignClientSuppliedId().
    }

    @Override
    public void assignClientSuppliedId(String id) {
        this.code = id;
    }
}
