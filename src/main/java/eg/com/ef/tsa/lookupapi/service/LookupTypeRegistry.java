package eg.com.ef.tsa.lookupapi.service;

import eg.com.ef.tsa.lookupapi.domain.*;
import eg.com.ef.tsa.lookupapi.repo.*;
import eg.com.ef.tsa.lookupapi.web.LookupTypeNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * The single whitelist of lookup types exposed by this API, keyed by URL path segment.
 * This is the security boundary: {@link eg.com.ef.tsa.lookupapi.web.LookupController}
 * never resolves an entity class from raw user input except through this map - there is
 * no reflection over the wider JPA model, and no way to reach an entity that isn't
 * listed here.
 *
 * To expose a new lookup table: add the entity + one-line repository (see the existing
 * 20), then add one line here. Nothing else changes.
 */
@Component
public class LookupTypeRegistry {

    public record Descriptor(
            String pathSegment,
            Class<? extends MutableLookup> entityClass,
            JpaRepository<MutableLookup, Object> repository,
            // findAll(Specification) lives on THIS interface, not on JpaRepository above -
            // both fields are unchecked re-views of the same proxy object, which is safe
            // because every concrete repository really does implement JpaSpecificationExecutor<T>
            // for its own T, and every T here really does implement MutableLookup.
            JpaSpecificationExecutor<MutableLookup> specExecutor,
            Class<?> idType
    ) {
    }

    private final Map<String, Descriptor> descriptors;

    @SuppressWarnings("unchecked")
    public LookupTypeRegistry(
            BankRepository bank, CurrencyRepository currency, InstitutionRepository institution,
            LanguageRepository language, TitleRepository title, EntityTypeRepository entityType,
            EntityLevelRepository entityLevel, EntityCategoryRepository entityCategory,
            AppliedToTypeRepository appliedToType, SystemParametersTypeRepository systemParametersType,
            StatusRepository status, StatusTypeRepository statusType,
            VoucherEntryModeRepository voucherEntryMode, FileDirectionRepository fileDirection,
            FileTypeRepository fileType, ErrorCodeRepository errorCode,
            AuditActionTypeRepository auditActionType, OtherEntityRepository otherEntity,
            CustomerContactPersonTypeRepository customerContactPersonType,
            GfmisRequestStatusRepository gfmisRequestStatus
    ) {
        descriptors = Map.ofEntries(
                entry("banks", Bank.class, bank, Long.class),
                entry("currencies", Currency.class, currency, Long.class),
                entry("institutions", Institution.class, institution, Long.class),
                entry("languages", Language.class, language, Long.class),
                entry("titles", Title.class, title, Long.class),
                entry("entity-types", EntityType.class, entityType, Long.class),
                entry("entity-levels", EntityLevel.class, entityLevel, Long.class),
                entry("entity-categories", EntityCategory.class, entityCategory, Long.class),
                entry("applied-to-types", AppliedToType.class, appliedToType, Long.class),
                entry("system-parameters-types", SystemParametersType.class, systemParametersType, Long.class),
                entry("statuses", Status.class, status, Long.class),
                entry("status-types", StatusType.class, statusType, Long.class),
                entry("voucher-entry-modes", VoucherEntryMode.class, voucherEntryMode, Long.class),
                entry("file-directions", FileDirection.class, fileDirection, Long.class),
                entry("file-types", FileType.class, fileType, Long.class),
                entry("error-codes", ErrorCode.class, errorCode, String.class),
                entry("audit-action-types", AuditActionType.class, auditActionType, Long.class),
                entry("other-entities", OtherEntity.class, otherEntity, Long.class),
                entry("customer-contact-person-types", CustomerContactPersonType.class, customerContactPersonType, Long.class),
                entry("gfmis-request-statuses", GfmisRequestStatus.class, gfmisRequestStatus, Long.class)
        );
    }

    @SuppressWarnings("unchecked")
    private static Map.Entry<String, Descriptor> entry(String path, Class<? extends MutableLookup> cls,
                                                         Object repo, Class<?> idType) {
        JpaRepository<MutableLookup, Object> asRepo = (JpaRepository<MutableLookup, Object>) repo;
        JpaSpecificationExecutor<MutableLookup> asSpec = (JpaSpecificationExecutor<MutableLookup>) repo;
        return Map.entry(path, new Descriptor(path, cls, asRepo, asSpec, idType));
    }

    public Descriptor resolve(String pathSegment) {
        Descriptor d = descriptors.get(pathSegment);
        if (d == null) {
            throw new LookupTypeNotFoundException(pathSegment, descriptors.keySet());
        }
        return d;
    }

    public List<String> availableTypes() {
        return descriptors.keySet().stream().sorted().toList();
    }
}
