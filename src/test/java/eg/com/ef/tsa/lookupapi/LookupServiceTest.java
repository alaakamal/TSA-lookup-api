package eg.com.ef.tsa.lookupapi;

import eg.com.ef.tsa.lookupapi.domain.Bank;
import eg.com.ef.tsa.lookupapi.domain.Currency;
import eg.com.ef.tsa.lookupapi.dto.LookupDTO;
import eg.com.ef.tsa.lookupapi.repo.BankRepository;
import eg.com.ef.tsa.lookupapi.repo.CurrencyRepository;
import eg.com.ef.tsa.lookupapi.repo.*;
import eg.com.ef.tsa.lookupapi.service.LookupService;
import eg.com.ef.tsa.lookupapi.service.LookupTypeRegistry;
import eg.com.ef.tsa.lookupapi.web.DuplicateCodeException;
import eg.com.ef.tsa.lookupapi.web.LookupNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Exercises the two rules that matter most for lookup data: rejecting a duplicate
 * code, and treating "no code at all" (Bank) as valid rather than a validation error.
 * Uses a real (in-memory) LookupTypeRegistry + mocked repositories, rather than a
 * Spring context, so this runs in milliseconds with no DB needed.
 */
@ExtendWith(MockitoExtension.class)
class LookupServiceTest {

    @Mock private BankRepository bankRepository;
    @Mock private CurrencyRepository currencyRepository;
    @Mock private InstitutionRepository institutionRepository;
    @Mock private LanguageRepository languageRepository;
    @Mock private TitleRepository titleRepository;
    @Mock private EntityTypeRepository entityTypeRepository;
    @Mock private EntityLevelRepository entityLevelRepository;
    @Mock private EntityCategoryRepository entityCategoryRepository;
    @Mock private AppliedToTypeRepository appliedToTypeRepository;
    @Mock private SystemParametersTypeRepository systemParametersTypeRepository;
    @Mock private StatusRepository statusRepository;
    @Mock private StatusTypeRepository statusTypeRepository;
    @Mock private VoucherEntryModeRepository voucherEntryModeRepository;
    @Mock private FileDirectionRepository fileDirectionRepository;
    @Mock private FileTypeRepository fileTypeRepository;
    @Mock private ErrorCodeRepository errorCodeRepository;
    @Mock private AuditActionTypeRepository auditActionTypeRepository;
    @Mock private OtherEntityRepository otherEntityRepository;
    @Mock private CustomerContactPersonTypeRepository customerContactPersonTypeRepository;
    @Mock private GfmisRequestStatusRepository gfmisRequestStatusRepository;

    private LookupService service;

    @BeforeEach
    void setUp() {
        LookupTypeRegistry registry = new LookupTypeRegistry(
                bankRepository, currencyRepository, institutionRepository, languageRepository,
                titleRepository, entityTypeRepository, entityLevelRepository, entityCategoryRepository,
                appliedToTypeRepository, systemParametersTypeRepository, statusRepository, statusTypeRepository,
                voucherEntryModeRepository, fileDirectionRepository, fileTypeRepository, errorCodeRepository,
                auditActionTypeRepository, otherEntityRepository, customerContactPersonTypeRepository,
                gfmisRequestStatusRepository
        );
        service = new LookupService(registry);
    }

    @Test
    void createRejectsDuplicateCode() {
        Currency existing = new Currency();
        existing.setId(1L);
        existing.setCode("EGP");
        existing.setArName("جنيه");
        existing.setEnName("Egyptian Pound");

        when(currencyRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class)))
                .thenReturn(java.util.List.of(existing));

        LookupDTO incoming = new LookupDTO(null, "EGP", "جنيه جديد", "New Pound", null, null, null, null, null, null);

        assertThatThrownBy(() -> service.create("currencies", incoming))
                .isInstanceOf(DuplicateCodeException.class)
                .hasMessageContaining("EGP");
    }

    @Test
    void createAllowsNullCodeForEntitiesWithoutOne() {
        Bank saved = new Bank();
        saved.setId(5L);
        saved.setArName("البنك الأهلي");
        saved.setEnName("National Bank");
        when(bankRepository.save(any())).thenReturn(saved);

        LookupDTO incoming = new LookupDTO(null, null, "البنك الأهلي", "National Bank", null, null, null, null, null, null);
        LookupDTO result = service.create("banks", incoming);

        assertThat(result.id()).isEqualTo(5L);
        assertThat(result.code()).isNull();
        assertThat(result.enName()).isEqualTo("National Bank");
    }

    @Test
    void getThrowsNotFoundForMissingId() {
        when(currencyRepository.findById(999L)).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> service.get("currencies", "999"))
                .isInstanceOf(LookupNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void unknownTypeIsRejectedBeforeTouchingAnyRepository() {
        assertThatThrownBy(() -> service.get("not-a-real-lookup", "1"))
                .isInstanceOf(eg.com.ef.tsa.lookupapi.web.LookupTypeNotFoundException.class);
    }
}
