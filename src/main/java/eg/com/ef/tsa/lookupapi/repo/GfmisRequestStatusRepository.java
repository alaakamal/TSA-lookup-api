package eg.com.ef.tsa.lookupapi.repo;

import eg.com.ef.tsa.lookupapi.domain.GfmisRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GfmisRequestStatusRepository extends JpaRepository<GfmisRequestStatus, Long>, JpaSpecificationExecutor<GfmisRequestStatus> {
}
