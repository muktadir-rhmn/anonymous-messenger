package messenger.event;

import java.util.Map;

class SetTypingResponse {
    public String typingUserType;
}

public class TypingEvent extends Event {
    public TypingEvent(String userType, Long userID, Long threadID) {
        super(userType, userID, threadID);
    }
}

class TypingEventResponseGenerator implements EventResponseGenerator {

    @Override
    public EventResponse generateResponseData(EventDescriptor eventDescriptor, Map<String, Object> listenerDescriptor) {
        SetTypingResponse response = new SetTypingResponse();
        response.typingUserType = eventDescriptor.userType;
        return new EventResponse(eventDescriptor.type, response, System.currentTimeMillis());
    }
}
