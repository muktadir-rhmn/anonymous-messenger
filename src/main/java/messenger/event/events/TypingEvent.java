package messenger.event.events;


public class TypingEvent extends Event {
    public TypingEvent(String userType, Long userID, Long threadID) {
        super(userType, userID, threadID);
    }
}

