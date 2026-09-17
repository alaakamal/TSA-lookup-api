package eg.com.ef.tsa.lookupapi.repo;

import eg.com.ef.tsa.lookupapi.domain.VoucherEntryMode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface VoucherEntryModeRepository extends JpaRepository<VoucherEntryMode, Long>, JpaSpecificationExecutor<VoucherEntryMode> {
}
