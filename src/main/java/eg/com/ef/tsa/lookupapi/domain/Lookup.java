package eg.com.ef.tsa.lookupapi.domain;

import java.sql.Timestamp;

/**
 * Marker + accessor contract implemented by every entity exposed through the generic
 * /api/lookups/{type} endpoint. {@link LookupTypeRegistry} only ever resolves classes
 * that implement this interface - the generic layer never reflects over arbitrary entities.
 */
public interface Lookup {
    Object getId();
    String getCode();
    String getArName();
    String getEnName();

    // Only VoucherEntryMode and FileType have a description pair; every other entity
    // keeps the default null rather than forcing every implementor to declare it.
    default String getArDescription() {
        return null;
    }

    default String getEnDescription() {
        return null;
    }

    // Every entity actually gets these from Auditable (via Lombok's generated getters,
    // which override these defaults) - declared here only so the mapper can work
    // against the Lookup/MutableLookup interface without knowing about Auditable.
    default Long getCreatedBy() {
        return null;
    }

    default Timestamp getCreatedDt() {
        return null;
    }

    default Long getModifiedBy() {
        return null;
    }

    default Timestamp getModifiedDt() {
        return null;
    }
}
