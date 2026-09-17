package eg.com.ef.tsa.lookupapi.service;

import eg.com.ef.tsa.lookupapi.domain.MutableLookup;
import eg.com.ef.tsa.lookupapi.dto.LookupDTO;

final class LookupMapper {

    private LookupMapper() {
    }

    static LookupDTO toDto(MutableLookup entity) {
        return new LookupDTO(
                entity.getId(),
                entity.getCode(),
                entity.getArName(),
                entity.getEnName(),
                entity.getArDescription(),
                entity.getEnDescription(),
                entity.getCreatedBy(),
                entity.getCreatedDt(),
                entity.getModifiedBy(),
                entity.getModifiedDt()
        );
    }

    /** Applies the editable fields of a DTO onto an entity. Id and audit columns are never touched here. */
    static void applyEditableFields(MutableLookup entity, LookupDTO dto) {
        if (dto.code() != null) {
            entity.setCode(dto.code());
        }
        entity.setArName(dto.arName());
        entity.setEnName(dto.enName());
        entity.setArDescription(dto.arDescription());
        entity.setEnDescription(dto.enDescription());
    }
}
