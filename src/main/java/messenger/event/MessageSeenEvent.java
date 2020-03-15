package messenger.event;

public class MessageSeenEvent extends Event{

    public MessageSeenEvent(Long userID, Long threadID, long lastMessageID) {
        super(userID, threadID);
        this.eventDescriptor.data.put("lastMessageID", lastMessageID);
    }

}

class MessageSeenEventResponse {
    public long lastMessageID;
}

class MessageSeenEventResponseGenerator implements EventResponseGenerator {

    @Override
    public EventResponse generateResponseData(EventDescriptor eventDescriptor) {
        MessageSeenEventResponse response = new MessageSeenEventResponse();
        response.lastMessageID = (long) eventDescriptor.data.get("lastMessageID");
        return new EventResponse(eventDescriptor.type, response, eventDescriptor.createdAt);
    }
}
