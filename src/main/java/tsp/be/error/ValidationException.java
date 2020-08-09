package tsp.be.error;

public class ValidationException extends RuntimeException {
    public Object getErrorObject() {
        return null;
    }
}
