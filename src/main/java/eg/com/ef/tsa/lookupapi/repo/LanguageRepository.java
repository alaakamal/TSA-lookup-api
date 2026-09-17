package eg.com.ef.tsa.lookupapi.repo;

import eg.com.ef.tsa.lookupapi.domain.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LanguageRepository extends JpaRepository<Language, Long>, JpaSpecificationExecutor<Language> {
}
