package messenger.event.events;

import messenger.event.EventDescriptor;

import java.util.Map;

class MessageSeenEventResponse {
    public long lastMessageID;
    public long seenAt;
    public long threadID;
}

public class MessageSeenEventResponseGenerator implements EventResponseGenerator {

    @Override
    public EventResponse generateResponseData(EventDescriptor eventDescriptor, Map<String, Object> listenerData) {
        MessageSeenEventResponse response = new MessageSeenEventResponse();
        response.lastMessageID = (int) eventDescriptor.data.get("lastMessageID");
        response.threadID = eventDescriptor.threadID;
        response.seenAt = eventDescriptor.createdAt;
        return new EventResponse(eventDescriptor.type, response, eventDescriptor.createdAt);
    }
}
