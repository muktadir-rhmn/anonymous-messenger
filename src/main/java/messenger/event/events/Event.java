package messenger.event.events;


import messenger.db.DatabaseExecutor;
import messenger.event.EventDescriptor;
import messenger.event.EventManager;
import messenger.user.UserDescriptor;

import java.util.HashMap;

public abstract class Event {
    public EventDescriptor eventDescriptor = new EventDescriptor();

    Event(UserDescriptor userDescriptor, Long threadID) {
        eventDescriptor.type = EventManager.getInstance().getEventType(this);
        eventDescriptor.userType = userDescriptor.getUserType();
        eventDescriptor.userID = userDescriptor.getUserID();
        eventDescriptor.threadID = threadID;
        eventDescriptor.createdAt = System.currentTimeMillis();

        eventDescriptor.data = new HashMap<>();
    }

    public int getEventType() {
        return EventManager.getInstance().getEventType(this);
    }

    public void invalidatePreviousEvents() {
        int eventType = EventManager.getInstance().getEventType(this);
        String sql = "UPDATE event SET invalid=1 WHERE type=" + eventType + " AND ";
        if (eventDescriptor.userID != null) sql += " user_id=" + eventDescriptor.userID;
        else if (eventDescriptor.threadID != null) sql += " thread_id=" + eventDescriptor.threadID;
        DatabaseExecutor.getInstance().executeUpdate(sql);
    }
}
