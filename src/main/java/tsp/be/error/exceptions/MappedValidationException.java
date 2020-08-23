package tsp.be.error.exceptions;

public class MappedValidationException extends ValidationException {

    public MappedValidationException() {
        this.errors.put("code",  errorCodes.MappedValidationError.ordinal());
    }

    public void addError(String fieldName, String errorMessage) {
        errors.put(fieldName, errorMessage);
    }

    public void throwIfAnyError() {
        if (errors.size() > 1) throw this;
    }
}
