package tsp.be.error;

public class SingleMessageValidationException extends ValidationException {

    public SingleMessageValidationException(String message) {
        errors.put("code", errorCodes.SingleMessageValidationError.ordinal());
        errors.put("message", message);

    }
}
