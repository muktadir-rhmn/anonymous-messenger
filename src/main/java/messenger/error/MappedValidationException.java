package messenger.error;

import java.util.HashMap;
import java.util.Map;

public class MappedValidationException extends ValidationException {
    private Map<String, String> map = null;

    @Override
    public Object getErrorObject() {
        return map;
    }

    public void put(String fieldName, String errorMessage) {
        if (map == null) map = new HashMap<>();
        map.put(fieldName, errorMessage);
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }
}
