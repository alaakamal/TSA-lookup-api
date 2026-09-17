package eg.com.ef.tsa.lookupapi.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Confirmed against the legacy eg.com.ef.tsa.jpa.entity.core.gfmis.GfmisRequestStatus
 * entity - this table does NOT follow the standard shape every other lookup does:
 *  - a single NAME/DESCRIPTION, not a bilingual AR_NAME/EN_NAME/AR_DESCRIPTION/EN_DESCRIPTION pair
 *  - no CREATED_BY/CREATED_DT/MODIFIED_BY/MODIFIED_DT columns at all (so this class does
 *    NOT extend Auditable, unlike every other entity in this package)
 *  - NO GFMIS_REQUEST_STATUS_SEQ sequence exists in the real schema (confirmed by a failed
 *    schema-validation run against it) - the legacy entity's id has no @GeneratedValue at
 *    all, matching that: ids are assigned some other way (manually, or possibly a DB
 *    trigger), never by application-managed sequence.
 * It was evidently scaffolded separately (NetBeans-generated, no auditing wired up) and,
 * per the Lookup Tables Register audit, is also the one table not populated by
 * populateTSALookups.sql - its real population source is still unconfirmed.
 *
 * PRACTICAL EFFECT: GET/list/search work normally. CREATE (POST) does not - there is no
 * id-generation strategy configured here, matching the legacy code, so a new row's id
 * must be supplied by the caller until someone confirms the real mechanism with whoever
 * owns this table.
 */
@Entity
@Table(name = "GFMIS_REQUEST_STATUS")
@Getter
@Setter
@NoArgsConstructor
public class GfmisRequestStatus implements MutableLookup {

    @Id
    private Long id;

    @Column(name = "CODE")
    private String code;

    @Column(name = "NAME")
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @Override
    public String getArName() {
        return name;
    }

    @Override
    public String getEnName() {
        return name;
    }

    @Override
    public void setArName(String arName) {
        this.name = arName;
    }

    @Override
    public void setEnName(String enName) {
        this.name = enName;
    }

    @Override
    public String getArDescription() {
        return description;
    }

    @Override
    public String getEnDescription() {
        return description;
    }

    @Override
    public void setArDescription(String value) {
        this.description = value;
    }

    @Override
    public void setEnDescription(String value) {
        this.description = value;
    }
}
