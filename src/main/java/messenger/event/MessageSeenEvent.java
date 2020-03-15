package messenger.event;

import java.util.Map;

class MessageSeenEventResponse {
    public long lastMessageID;
    public long threadID;
}
public class MessageSeenEvent extends Event{


    public MessageSeenEvent(String userType, Long userID, Long threadID, long lastMessageID) {
        super(userType, userID, threadID);
        this.eventDescriptor.data.put("lastMessageID", lastMessageID);
    }

}

class MessageSeenEventResponseGenerator implements EventResponseGenerator {

    @Override
    public EventResponse generateResponseData(EventDescriptor eventDescriptor, Map<String, Object> listenerData) {
        MessageSeenEventResponse response = new MessageSeenEventResponse();
        response.lastMessageID = (long) eventDescriptor.data.get("lastMessageID");
        response.threadID = eventDescriptor.threadID;
        return new EventResponse(eventDescriptor.type, response, eventDescriptor.createdAt);
    }
}
