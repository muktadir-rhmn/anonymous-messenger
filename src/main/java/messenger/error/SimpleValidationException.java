package messenger.error;

import java.util.HashMap;
import java.util.Map;

public class SimpleValidationException extends ValidationException {
    public String message;

    public SimpleValidationException(String message) {
        this.message = message;
    }

    public Object getErrorObject() {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", message);
        return errors;
    }
}
