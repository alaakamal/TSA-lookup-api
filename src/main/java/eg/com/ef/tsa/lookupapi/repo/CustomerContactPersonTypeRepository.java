package eg.com.ef.tsa.lookupapi.repo;

import eg.com.ef.tsa.lookupapi.domain.CustomerContactPersonType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CustomerContactPersonTypeRepository extends JpaRepository<CustomerContactPersonType, Long>, JpaSpecificationExecutor<CustomerContactPersonType> {
}
