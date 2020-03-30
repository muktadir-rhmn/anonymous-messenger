package messenger.event.events;

import messenger.event.EventDescriptor;

import java.util.Map;

class SetTypingResponse {
    public String typingUserType;
}

public class TypingEventResponseGenerator implements EventResponseGenerator {

    @Override
    public EventResponse generateResponseData(EventDescriptor eventDescriptor, Map<String, Object> listenerDescriptor) {
        SetTypingResponse response = new SetTypingResponse();
        response.typingUserType = eventDescriptor.userType;
        return new EventResponse(eventDescriptor.type, response, System.currentTimeMillis());
    }
}
