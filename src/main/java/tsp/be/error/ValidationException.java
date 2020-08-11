package tsp.be.error;

import java.util.HashMap;
import java.util.Map;

public class ValidationException extends RuntimeException {
    public enum errorCodes {SingleMessageValidationError, MappedValidationError, DataIntegrityError};
    protected Map<String, Object> errors = new HashMap<>();

    public Map<String, Object> getErrorObject() {
        return errors;
    }
}
