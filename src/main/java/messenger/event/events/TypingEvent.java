package messenger.event.events;


import messenger.user.UserDescriptor;

public class TypingEvent extends Event {
    public TypingEvent(UserDescriptor userDescriptor, Long threadID) {
        super(userDescriptor, threadID);
    }
}

