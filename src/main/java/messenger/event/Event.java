package messenger.event;


public abstract class Event implements EventResponseGenerator{
    public Long userID = null;
    public Long threadID = null;
    public long createdAt;

    public abstract String encodeEventData();
    public int getEventType() {
        return EventManager.getInstance().getEventType(this);
    }

    public abstract void invalidatePreviousEvents();
}
