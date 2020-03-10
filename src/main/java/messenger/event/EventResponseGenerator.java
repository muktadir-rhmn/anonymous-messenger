package messenger.event;

import java.util.Map;

public interface EventResponseGenerator {
    EventResponse generateResponseData(EventDescriptor eventDescriptor, Long createdAt, Map<String, Object> data);
}
