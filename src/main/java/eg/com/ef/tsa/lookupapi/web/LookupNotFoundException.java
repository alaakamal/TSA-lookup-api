package eg.com.ef.tsa.lookupapi.web;

public class LookupNotFoundException extends RuntimeException {
    public LookupNotFoundException(String type, Object id) {
        super("No '" + type + "' row found with id " + id);
    }
}
