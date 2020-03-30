package messenger.event.events;

import messenger.db.models.Message;

import java.util.List;

public class NewMessageEvent extends Event {

    public NewMessageEvent(String userType, Long userID, long threadID) {
        super(userType, userID, threadID);
    }
}

