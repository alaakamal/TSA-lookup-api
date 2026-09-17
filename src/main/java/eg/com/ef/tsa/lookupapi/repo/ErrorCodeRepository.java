package eg.com.ef.tsa.lookupapi.repo;

import eg.com.ef.tsa.lookupapi.domain.ErrorCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ErrorCodeRepository extends JpaRepository<ErrorCode, String>, JpaSpecificationExecutor<ErrorCode> {
}
