package messenger.event.events;

import messenger.user.UserDescriptor;

public class NewMessageEvent extends Event {

    public NewMessageEvent(UserDescriptor userDescriptor, long threadID) {
        super(userDescriptor, threadID);
    }
}

