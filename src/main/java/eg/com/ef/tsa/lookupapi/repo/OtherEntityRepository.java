package eg.com.ef.tsa.lookupapi.repo;

import eg.com.ef.tsa.lookupapi.domain.OtherEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OtherEntityRepository extends JpaRepository<OtherEntity, Long>, JpaSpecificationExecutor<OtherEntity> {
}
