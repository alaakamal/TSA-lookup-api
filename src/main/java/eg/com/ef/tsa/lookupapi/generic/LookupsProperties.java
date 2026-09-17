package eg.com.ef.tsa.lookupapi.generic;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * The config-driven alternative to {@link eg.com.ef.tsa.lookupapi.service.LookupTypeRegistry}:
 * every exposed lookup table is described here as data instead of as a Java entity class.
 * This is the whitelist for the generic-lookup slice - same security role as the JPA
 * registry, just sourced from application.yml instead of hardcoded.
 */
@ConfigurationProperties(prefix = "tsa")
public record LookupsProperties(List<LookupConfig> lookups) {

    public enum IdType {LONG, STRING}

    public record LookupConfig(
            String path,
            String table,
            String idColumn,
            IdType idType,
            boolean hasCode,
            List<String> extraColumns,
            List<String> nameColumns,
            Boolean hasAudit
    ) {
        public List<String> extraColumns() {
            return extraColumns == null ? List.of() : extraColumns;
        }

        /** Defaults to AR_NAME/EN_NAME for the standard-shape tables. */
        public List<String> nameColumns() {
            return (nameColumns == null || nameColumns.isEmpty()) ? List.of("AR_NAME", "EN_NAME") : nameColumns;
        }

        /**
         * Whether this table has CREATED_BY/CREATED_DT/MODIFIED_BY/MODIFIED_DT columns -
         * true for every lookup table except GFMIS_REQUEST_STATUS, which was scaffolded
         * separately and never got auditing wired up. Defaults to true so existing YAML
         * entries don't all need updating for the one exception.
         *
         * Named differently from the raw "hasAudit" record component (rather than
         * overriding its accessor) because a record's explicit accessor must return
         * exactly the component's declared type - Boolean here, not primitive boolean -
         * and this needs to return a primitive for plain "if (cfg.auditEnabled())" use.
         */
        public boolean auditEnabled() {
            return hasAudit == null || hasAudit;
        }

        public List<String> searchableColumns() {
            List<String> cols = new java.util.ArrayList<>(nameColumns());
            if (hasCode) cols.add("CODE");
            return cols;
        }

        /** All columns a client may write, in a stable order: code (if any) + name columns + extras. */
        public List<String> writableColumns() {
            List<String> cols = new java.util.ArrayList<>();
            if (hasCode) cols.add("CODE");
            cols.addAll(nameColumns());
            cols.addAll(extraColumns());
            return cols;
        }
    }
}
