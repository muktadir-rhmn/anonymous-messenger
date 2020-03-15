package messenger.event;

import java.util.Map;

public interface EventResponseGenerator {
    EventResponse generateResponseData(EventDescriptor eventDescriptor, Map<String, Object> listenerData);
}
