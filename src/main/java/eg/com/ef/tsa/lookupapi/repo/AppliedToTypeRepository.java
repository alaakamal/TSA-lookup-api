package eg.com.ef.tsa.lookupapi.repo;

import eg.com.ef.tsa.lookupapi.domain.AppliedToType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AppliedToTypeRepository extends JpaRepository<AppliedToType, Long>, JpaSpecificationExecutor<AppliedToType> {
}
