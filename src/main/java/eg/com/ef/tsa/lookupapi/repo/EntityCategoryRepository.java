package eg.com.ef.tsa.lookupapi.repo;

import eg.com.ef.tsa.lookupapi.domain.EntityCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EntityCategoryRepository extends JpaRepository<EntityCategory, Long>, JpaSpecificationExecutor<EntityCategory> {
}
