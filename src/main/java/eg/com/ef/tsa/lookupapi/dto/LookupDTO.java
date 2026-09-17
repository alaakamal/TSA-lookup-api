package eg.com.ef.tsa.lookupapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.sql.Timestamp;

/**
 * One shared shape for all 20 lookup tables. This is a deliberate trade-off: a single
 * generic DTO is far less boilerplate than 20 near-identical classes, but it can't
 * express per-entity rules (e.g. Bank truly has no code; ErrorCode's "code" is its
 * primary key and immutable on update). Those exceptions are handled in
 * {@link eg.com.ef.tsa.lookupapi.service.LookupMapper} and the service layer, not here.
 *
 * @param id             null on create; present on read/update. String for ErrorCode, numeric for the rest.
 * @param code           business code; null/ignored for entities that don't have one (Bank).
 * @param arName         Arabic display name (or, for ErrorCode, the shared message text).
 * @param enName         English display name (or, for ErrorCode, the shared message text).
 * @param arDescription  optional; only populated for VoucherEntryMode / FileType.
 * @param enDescription  optional; only populated for VoucherEntryMode / FileType.
 */
public record LookupDTO(
        Object id,

        @Size(max = 10)
        String code,

        @NotBlank(message = "arName is required")
        @Size(max = 200)
        String arName,

        @NotBlank(message = "enName is required")
        @Size(max = 200)
        String enName,

        String arDescription,
        String enDescription,

        Long createdBy,
        Timestamp createdDt,
        Long modifiedBy,
        Timestamp modifiedDt
) {
}
