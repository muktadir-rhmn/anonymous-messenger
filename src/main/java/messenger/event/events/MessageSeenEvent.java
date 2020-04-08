package messenger.event.events;


import messenger.user.UserDescriptor;

public class MessageSeenEvent extends Event {

    public MessageSeenEvent(UserDescriptor userDescriptor, Long threadID, long lastMessageID, long seenAt) {
        super(userDescriptor, threadID);
        this.eventDescriptor.data.put("lastMessageID", (int)lastMessageID);
        this.eventDescriptor.data.put("seenAt", (int) seenAt);
    }

}

