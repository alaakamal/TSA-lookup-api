package eg.com.ef.tsa.lookupapi.repo;

import eg.com.ef.tsa.lookupapi.domain.SystemParametersType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SystemParametersTypeRepository extends JpaRepository<SystemParametersType, Long>, JpaSpecificationExecutor<SystemParametersType> {
}
