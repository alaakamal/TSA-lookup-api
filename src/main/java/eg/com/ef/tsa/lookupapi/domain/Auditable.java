package eg.com.ef.tsa.lookupapi.domain;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import jakarta.persistence.EntityListeners;

import java.sql.Timestamp;

/**
 * Common audit columns present on every lookup table in the legacy schema
 * (CREATED_BY / CREATED_DT / MODIFIED_BY / MODIFIED_DT). Stamped automatically by
 * Spring Data JPA auditing - no entity or service method ever sets these by hand,
 * which is exactly the gap the legacy JSF beans had (each one did it manually,
 * and it was easy to forget).
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable {

    @CreatedBy
    @Column(name = "CREATED_BY", updatable = false)
    private Long createdBy;

    @CreatedDate
    @Column(name = "CREATED_DT", updatable = false)
    private Timestamp createdDt;

    @LastModifiedBy
    @Column(name = "MODIFIED_BY")
    private Long modifiedBy;

    @LastModifiedDate
    @Column(name = "MODIFIED_DT")
    private Timestamp modifiedDt;
}
