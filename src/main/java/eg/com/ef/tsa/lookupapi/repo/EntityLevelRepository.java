package eg.com.ef.tsa.lookupapi.repo;

import eg.com.ef.tsa.lookupapi.domain.EntityLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EntityLevelRepository extends JpaRepository<EntityLevel, Long>, JpaSpecificationExecutor<EntityLevel> {
}
