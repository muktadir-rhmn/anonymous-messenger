package messenger.event.events;


public class MessageSeenEvent extends Event {

    public MessageSeenEvent(String userType, Long userID, Long threadID, long lastMessageID, long seenAt) {
        super(userType, userID, threadID);
        this.eventDescriptor.data.put("lastMessageID", (int)lastMessageID);
        this.eventDescriptor.data.put("seenAt", (int) seenAt);
    }

}

