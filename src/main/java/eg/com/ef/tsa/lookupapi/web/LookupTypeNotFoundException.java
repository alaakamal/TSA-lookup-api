package eg.com.ef.tsa.lookupapi.web;

import java.util.Set;

public class LookupTypeNotFoundException extends RuntimeException {
    public LookupTypeNotFoundException(String requested, Set<String> known) {
        super("Unknown lookup type '" + requested + "'. Available types: " + String.join(", ", known.stream().sorted().toList()));
    }
}
