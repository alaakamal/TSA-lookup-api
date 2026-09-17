package eg.com.ef.tsa.lookupapi.web;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LookupTypeNotFoundException.class)
    public ProblemDetail onUnknownType(LookupTypeNotFoundException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(LookupNotFoundException.class)
    public ProblemDetail onNotFound(LookupNotFoundException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(DuplicateCodeException.class)
    public ProblemDetail onDuplicateCode(DuplicateCodeException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail onFkViolation(DataIntegrityViolationException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,
                "This row is still referenced elsewhere in the system and cannot be deleted.");
    }

    @ExceptionHandler(PropertyReferenceException.class)
    public ProblemDetail onBadSortProperty(PropertyReferenceException e) {
        // Most common cause: Swagger UI's auto-generated Pageable "sort" field defaults
        // to the literal placeholder text "string", which - if not cleared before
        // clicking Execute - gets sent as ?sort=string and fails exactly like this,
        // since no entity has a property actually named "string".
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "Invalid sort field '" + e.getPropertyName() + "' for this lookup type. " +
                "Remove the sort parameter or use a real field name (e.g. enName, code).");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail onValidation(MethodArgumentNotValidException e) {
        String detail = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("Validation failed");
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
    }
}
