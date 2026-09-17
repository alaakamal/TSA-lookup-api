package eg.com.ef.tsa.lookupapi.repo;

import eg.com.ef.tsa.lookupapi.domain.AuditActionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AuditActionTypeRepository extends JpaRepository<AuditActionType, Long>, JpaSpecificationExecutor<AuditActionType> {
}
