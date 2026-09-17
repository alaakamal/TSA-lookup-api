package eg.com.ef.tsa.lookupapi.service;

import eg.com.ef.tsa.lookupapi.domain.MutableLookup;
import eg.com.ef.tsa.lookupapi.dto.LookupDTO;
import eg.com.ef.tsa.lookupapi.service.LookupTypeRegistry.Descriptor;
import eg.com.ef.tsa.lookupapi.web.DuplicateCodeException;
import eg.com.ef.tsa.lookupapi.web.LookupNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;

@Service
public class LookupService {

    private final LookupTypeRegistry registry;

    public LookupService(LookupTypeRegistry registry) {
        this.registry = registry;
    }

    @Transactional(readOnly = true)
    public Page<LookupDTO> search(String type, String query, Pageable pageable) {
        Descriptor d = registry.resolve(type);
        Specification<MutableLookup> spec = LookupSpecifications.textContains(query);
        // Sort is deliberately dropped, not passed through: no single sort-field name is
        // valid across all 20 differently-shaped entities, and Swagger's auto-generated
        // "sort" field defaults to the literal placeholder text "string", which fails
        // schema validation the moment it's left unedited. The generic-lookups side never
        // had this problem because it never used the sort at all (always orders by id) -
        // matching that behavior here removes the whole bug class instead of just
        // reporting it more cleanly. Page number/size are still honored.
        Pageable unsorted = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        return d.specExecutor().findAll(spec, unsorted).map(LookupMapper::toDto);
    }

    @Transactional(readOnly = true)
    public LookupDTO get(String type, String rawId) {
        Descriptor d = registry.resolve(type);
        Object id = coerceId(d, rawId);
        MutableLookup entity = d.repository().findById(id)
                .orElseThrow(() -> new LookupNotFoundException(type, rawId));
        return LookupMapper.toDto(entity);
    }

    @Transactional
    public LookupDTO create(String type, LookupDTO dto) {
        Descriptor d = registry.resolve(type);
        assertCodeUnique(d, dto.code(), null);
        MutableLookup entity = instantiate(d);
        if (d.idType() == String.class) {
            // e.g. ErrorCode: there's no DB-generated id to wait for - the caller supplies
            // it up front (reusing the "code" field of the DTO as the new primary key).
            entity.assignClientSuppliedId(dto.code());
        }
        LookupMapper.applyEditableFields(entity, dto);
        MutableLookup saved = d.repository().save(entity);
        return LookupMapper.toDto(saved);
    }

    @Transactional
    public LookupDTO update(String type, String rawId, LookupDTO dto) {
        Descriptor d = registry.resolve(type);
        Object id = coerceId(d, rawId);
        MutableLookup entity = d.repository().findById(id)
                .orElseThrow(() -> new LookupNotFoundException(type, rawId));
        assertCodeUnique(d, dto.code(), id);
        LookupMapper.applyEditableFields(entity, dto);
        MutableLookup saved = d.repository().save(entity);
        return LookupMapper.toDto(saved);
    }

    @Transactional
    public void delete(String type, String rawId) {
        Descriptor d = registry.resolve(type);
        Object id = coerceId(d, rawId);
        if (!d.repository().existsById(id)) {
            throw new LookupNotFoundException(type, rawId);
        }
        try {
            // Deliberately no per-type "is this still referenced" query here - that would mean
            // hand-writing and maintaining a reference-check for every FK that points at each
            // of the 20 tables. Instead we let Oracle's own FK constraint fire and translate it
            // into a clean 409 via GlobalExceptionHandler, rather than a raw 500/stack trace.
            d.repository().deleteById(id);
            d.repository().flush();
        } catch (DataIntegrityViolationException e) {
            throw e; // handled centrally - see GlobalExceptionHandler
        }
    }

    private void assertCodeUnique(Descriptor d, String code, Object excludeId) {
        if (code == null || code.isBlank()) {
            return; // e.g. Bank, which has no code column at all
        }
        Specification<MutableLookup> spec = LookupSpecifications.textContains(code);
        boolean clash = d.specExecutor().findAll(spec).stream()
                .anyMatch(e -> code.equalsIgnoreCase(e.getCode())
                        && (excludeId == null || !excludeId.equals(e.getId())));
        if (clash) {
            throw new DuplicateCodeException(d.pathSegment(), code);
        }
    }

    private Object coerceId(Descriptor d, String rawId) {
        if (d.idType() == String.class) {
            return rawId;
        }
        try {
            return Long.valueOf(rawId);
        } catch (NumberFormatException e) {
            throw new LookupNotFoundException(d.pathSegment(), rawId);
        }
    }

    private MutableLookup instantiate(Descriptor d) {
        try {
            return d.entityClass().getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalStateException("Lookup entity " + d.entityClass() + " must have a no-arg constructor", e);
        }
    }
}
