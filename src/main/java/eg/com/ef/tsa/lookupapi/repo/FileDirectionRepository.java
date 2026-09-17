package eg.com.ef.tsa.lookupapi.repo;

import eg.com.ef.tsa.lookupapi.domain.FileDirection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FileDirectionRepository extends JpaRepository<FileDirection, Long>, JpaSpecificationExecutor<FileDirection> {
}
