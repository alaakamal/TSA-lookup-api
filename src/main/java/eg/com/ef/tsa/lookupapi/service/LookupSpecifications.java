package eg.com.ef.tsa.lookupapi.service;

import eg.com.ef.tsa.lookupapi.domain.MutableLookup;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.metamodel.Attribute;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Free-text search across whichever of "code" / "arName" / "enName" each entity
 * actually has. Checked against the JPA metamodel rather than assumed, because one
 * entity (ErrorCode) doesn't have arName/enName as real mapped attributes - it only
 * has "code" and "message". Building the predicate this way means the same search
 * method works for all 20 types without a per-entity branch.
 */
final class LookupSpecifications {

    private static final List<String> SEARCHABLE_CANDIDATES = List.of("code", "arName", "enName");

    private LookupSpecifications() {
    }

    static Specification<MutableLookup> textContains(String query) {
        return (root, criteriaQuery, cb) -> {
            if (query == null || query.isBlank()) {
                return cb.conjunction();
            }
            Set<String> realAttributeNames = root.getModel().getAttributes().stream()
                    .map(Attribute::getName)
                    .collect(Collectors.toSet());

            List<Predicate> predicates = new ArrayList<>();
            String pattern = "%" + query.toLowerCase() + "%";
            for (String candidate : SEARCHABLE_CANDIDATES) {
                if (realAttributeNames.contains(candidate)) {
                    predicates.add(cb.like(cb.lower(root.get(candidate)), pattern));
                }
            }
            return predicates.isEmpty() ? cb.disjunction() : cb.or(predicates.toArray(new Predicate[0]));
        };
    }
}
