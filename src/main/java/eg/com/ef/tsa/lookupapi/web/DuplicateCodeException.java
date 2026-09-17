package eg.com.ef.tsa.lookupapi.web;

public class DuplicateCodeException extends RuntimeException {
    public DuplicateCodeException(String type, String code) {
        super("Code '" + code + "' is already used by another " + type + " row");
    }
}
