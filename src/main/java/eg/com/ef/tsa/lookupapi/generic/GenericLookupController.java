package eg.com.ef.tsa.lookupapi.generic;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Config-driven twin of {@link eg.com.ef.tsa.lookupapi.web.LookupController}, kept on a
 * separate path so both approaches can be compared side by side rather than one
 * replacing the other outright. See {@link LookupsProperties} for how a new lookup
 * type is added here (a YAML entry - no Java class).
 */
@RestController
@RequestMapping("/api/generic-lookups")
@Tag(name = "Generic Lookups", description = "Same 20 tables, driven by application.yml instead of JPA entities")
public class GenericLookupController {

    private final GenericLookupService service;

    public GenericLookupController(GenericLookupService service) {
        this.service = service;
    }

    @GetMapping("/types")
    @PreAuthorize("hasAnyRole('ADMIN_USER','SUPER_ADMIN_USER')")
    public List<String> types() {
        return service.availableTypes();
    }

    @GetMapping("/{type}")
    @PreAuthorize("hasAnyRole('ADMIN_USER','SUPER_ADMIN_USER')")
    public Page<Map<String, Object>> list(@PathVariable String type,
                                           @RequestParam(required = false) String q,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "20") int size) {
        // Plain page/size, not Pageable - see LookupController for why (Swagger's
        // auto-generated "sort" field for Pageable defaults to a placeholder that breaks
        // things if left unedited). This side never used sort anyway (always orders by id).
        return service.search(type, q, PageRequest.of(page, size));
    }

    @GetMapping("/{type}/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_USER','SUPER_ADMIN_USER')")
    public Map<String, Object> get(@PathVariable String type, @PathVariable String id) {
        return service.get(type, id);
    }

    @PostMapping("/{type}")
    @PreAuthorize("hasAnyRole('ADMIN_USER','SUPER_ADMIN_USER')")
    public ResponseEntity<Map<String, Object>> create(@PathVariable String type, @RequestBody Map<String, Object> body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(type, body));
    }

    @PutMapping("/{type}/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_USER','SUPER_ADMIN_USER')")
    public Map<String, Object> update(@PathVariable String type, @PathVariable String id, @RequestBody Map<String, Object> body) {
        return service.update(type, id, body);
    }

    @DeleteMapping("/{type}/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN_USER')")
    public ResponseEntity<Void> delete(@PathVariable String type, @PathVariable String id) {
        service.delete(type, id);
        return ResponseEntity.noContent().build();
    }
}
