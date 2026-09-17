package eg.com.ef.tsa.lookupapi.generic;

import eg.com.ef.tsa.lookupapi.generic.LookupsProperties.LookupConfig;
import eg.com.ef.tsa.lookupapi.web.DuplicateCodeException;
import eg.com.ef.tsa.lookupapi.web.LookupNotFoundException;
import eg.com.ef.tsa.lookupapi.web.LookupTypeNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GenericLookupService {

    private final LookupsProperties properties;
    private final GenericLookupRepository repository;

    public GenericLookupService(LookupsProperties properties, GenericLookupRepository repository) {
        this.properties = properties;
        this.repository = repository;
    }

    public List<String> availableTypes() {
        return properties.lookups().stream().map(LookupConfig::path).sorted().toList();
    }

    @Transactional(readOnly = true)
    public Page<Map<String, Object>> search(String type, String q, Pageable pageable) {
        LookupConfig cfg = resolve(type);
        List<Map<String, Object>> rows = repository.findAll(cfg, q, (int) pageable.getOffset(), pageable.getPageSize());
        long total = repository.count(cfg, q);
        return new PageImpl<>(rows, pageable, total);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> get(String type, String rawId) {
        LookupConfig cfg = resolve(type);
        Object id = coerceId(cfg, rawId);
        return repository.findById(cfg, id).orElseThrow(() -> new LookupNotFoundException(type, rawId));
    }

    @Transactional
    public Map<String, Object> create(String type, Map<String, Object> body) {
        LookupConfig cfg = resolve(type);
        validateRequired(cfg, body);
        String code = (String) body.get("CODE");
        if (repository.existsByCode(cfg, code, null)) {
            throw new DuplicateCodeException(type, code);
        }
        Map<String, Object> values = new HashMap<>(body);
        Timestamp now = Timestamp.from(Instant.now());
        values.put("CREATED_BY", currentUserId());
        values.put("CREATED_DT", now);
        if (cfg.idType() == LookupsProperties.IdType.STRING) {
            values.putIfAbsent(cfg.idColumn(), body.get(cfg.idColumn()));
        }
        Object id = repository.insert(cfg, values);
        return repository.findById(cfg, id).orElseThrow();
    }

    @Transactional
    public Map<String, Object> update(String type, String rawId, Map<String, Object> body) {
        LookupConfig cfg = resolve(type);
        Object id = coerceId(cfg, rawId);
        repository.findById(cfg, id).orElseThrow(() -> new LookupNotFoundException(type, rawId));
        validateRequired(cfg, body);
        String code = (String) body.get("CODE");
        if (repository.existsByCode(cfg, code, id)) {
            throw new DuplicateCodeException(type, code);
        }
        Map<String, Object> values = new HashMap<>(body);
        values.put("MODIFIED_BY", currentUserId());
        values.put("MODIFIED_DT", Timestamp.from(Instant.now()));
        repository.update(cfg, id, values);
        return repository.findById(cfg, id).orElseThrow();
    }

    @Transactional
    public void delete(String type, String rawId) {
        LookupConfig cfg = resolve(type);
        Object id = coerceId(cfg, rawId);
        if (repository.findById(cfg, id).isEmpty()) {
            throw new LookupNotFoundException(type, rawId);
        }
        repository.deleteById(cfg, id); // FK violation, if any, surfaces as DataIntegrityViolationException -> 409
    }

    private void validateRequired(LookupConfig cfg, Map<String, Object> body) {
        for (String nameColumn : cfg.nameColumns()) {
            Object v = body.get(nameColumn);
            if (v == null || v.toString().isBlank()) {
                throw new IllegalArgumentException(nameColumn + " is required for " + cfg.table());
            }
        }
    }

    private LookupConfig resolve(String type) {
        return properties.lookups().stream()
                .filter(c -> c.path().equals(type))
                .findFirst()
                .orElseThrow(() -> new LookupTypeNotFoundException(type,
                        properties.lookups().stream().map(LookupConfig::path).collect(java.util.stream.Collectors.toSet())));
    }

    private Object coerceId(LookupConfig cfg, String rawId) {
        return cfg.idType() == LookupsProperties.IdType.STRING ? rawId : Long.valueOf(rawId);
    }

    private Long currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt && jwt.getClaim("systemUserId") != null) {
            return Long.valueOf(jwt.getClaim("systemUserId").toString());
        }
        return null;
    }
}
