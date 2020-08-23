package tsp.be.error.exceptions;

public class SingleMessageValidationException extends ValidationException {

    public SingleMessageValidationException(String message) {
        errors.put("code", errorCodes.SingleMessageValidationError.ordinal());
        errors.put("message", message);

    }
}
