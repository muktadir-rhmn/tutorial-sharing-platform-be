package tsp.be.error;

import java.util.HashMap;
import java.util.Map;

public class MappedValidationException extends ValidationException {
    private Map<String, String> map = new HashMap<>();

    @Override
    public Object getErrorObject() {
        return map;
    }

    public void addError(String fieldName, String errorMessage) {
        map.put(fieldName, errorMessage);
    }

    private boolean isEmpty() {
        return map.isEmpty();
    }

    public void throwIfAnyError() {
        if (!isEmpty()) throw this;
    }
}
