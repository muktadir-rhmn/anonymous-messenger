package messenger.event.events;

import messenger.event.EventDescriptor;

import java.util.Map;

public interface EventResponseGenerator {
    EventResponse generateResponseData(EventDescriptor eventDescriptor, Map<String, Object> listenerData);
}
