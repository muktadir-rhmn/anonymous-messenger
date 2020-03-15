package messenger.event;

import java.util.Map;

class MessageSeenEventResponse {
    public long lastMessageID;
    public long seenAt;
    public long threadID;
}
public class MessageSeenEvent extends Event{

    public MessageSeenEvent(String userType, Long userID, Long threadID, long lastMessageID, long seenAt) {
        super(userType, userID, threadID);
        this.eventDescriptor.data.put("lastMessageID", (int)lastMessageID);
        this.eventDescriptor.data.put("seenAt", (int) seenAt);
    }

}

class MessageSeenEventResponseGenerator implements EventResponseGenerator {

    @Override
    public EventResponse generateResponseData(EventDescriptor eventDescriptor, Map<String, Object> listenerData) {
        MessageSeenEventResponse response = new MessageSeenEventResponse();
        response.lastMessageID = (int) eventDescriptor.data.get("lastMessageID");
        response.threadID = eventDescriptor.threadID;
        response.seenAt = eventDescriptor.createdAt;
        return new EventResponse(eventDescriptor.type, response, eventDescriptor.createdAt);
    }
}
