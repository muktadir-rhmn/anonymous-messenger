package messenger.event;

import java.util.Map;

public interface EventResponseGenerator {
    Object generateResponseData(EventDescriptor eventDescriptor, Map<String, Object> data);
}
