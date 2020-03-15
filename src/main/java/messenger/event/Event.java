package messenger.event;


import messenger.db.DatabaseExecutor;

import java.util.HashMap;

public abstract class Event {
    public EventDescriptor eventDescriptor = new EventDescriptor();

    Event(String userType, Long userID, Long threadID) {
        eventDescriptor.userType = userType;
        eventDescriptor.userID = userID;
        eventDescriptor.threadID = threadID;

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
