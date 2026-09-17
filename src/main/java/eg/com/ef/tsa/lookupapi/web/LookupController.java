package eg.com.ef.tsa.lookupapi.web;

import eg.com.ef.tsa.lookupapi.dto.LookupDTO;
import eg.com.ef.tsa.lookupapi.service.LookupService;
import eg.com.ef.tsa.lookupapi.service.LookupTypeRegistry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * One controller for every lookup table listed in {@link LookupTypeRegistry}. See that
 * class for how a new lookup type gets added - this class never changes when it happens.
 */
@RestController
@RequestMapping("/api/lookups")
@Tag(name = "Lookups", description = "Generic CRUD over the 20 reference-data tables that previously had no admin screen")
public class LookupController {

    private final LookupService service;
    private final LookupTypeRegistry registry;

    public LookupController(LookupService service, LookupTypeRegistry registry) {
        this.service = service;
        this.registry = registry;
    }

    @GetMapping("/types")
    @Operation(summary = "List the lookup type path segments this API exposes")
    @PreAuthorize("hasAnyRole('ADMIN_USER','SUPER_ADMIN_USER')")
    public List<String> types() {
        return registry.availableTypes();
    }

    @GetMapping("/{type}")
    @Operation(summary = "Search/list rows of one lookup type, paginated (always ordered by id - no sort parameter)")
    @PreAuthorize("hasAnyRole('ADMIN_USER','SUPER_ADMIN_USER')")
    public Page<LookupDTO> list(@PathVariable String type,
                                 @RequestParam(required = false) String q,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "20") int size) {
        // Plain page/size params instead of a Pageable parameter, deliberately: binding
        // Pageable directly makes Swagger auto-generate a "sort" field pre-filled with the
        // literal placeholder text "string", which fails against every entity's real
        // property set if left unedited - see LookupService.search() for the full story.
        return service.search(type, q, PageRequest.of(page, size));
    }

    @GetMapping("/{type}/{id}")
    @Operation(summary = "Get one row by id")
    @PreAuthorize("hasAnyRole('ADMIN_USER','SUPER_ADMIN_USER')")
    public LookupDTO get(@PathVariable String type, @PathVariable String id) {
        return service.get(type, id);
    }

    @PostMapping("/{type}")
    @Operation(summary = "Create a new row")
    @PreAuthorize("hasAnyRole('ADMIN_USER','SUPER_ADMIN_USER')")
    public ResponseEntity<LookupDTO> create(@PathVariable String type, @Valid @RequestBody LookupDTO dto) {
        LookupDTO created = service.create(type, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{type}/{id}")
    @Operation(summary = "Update an existing row")
    @PreAuthorize("hasAnyRole('ADMIN_USER','SUPER_ADMIN_USER')")
    public LookupDTO update(@PathVariable String type, @PathVariable String id, @Valid @RequestBody LookupDTO dto) {
        return service.update(type, id, dto);
    }

    @DeleteMapping("/{type}/{id}")
    @Operation(summary = "Delete a row (rejected with 409 if still referenced elsewhere)")
    @PreAuthorize("hasRole('SUPER_ADMIN_USER')") // deletion gated tighter than read/create/edit
    public ResponseEntity<Void> delete(@PathVariable String type, @PathVariable String id) {
        service.delete(type, id);
        return ResponseEntity.noContent().build();
    }
}
