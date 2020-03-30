package messenger.event.events;


public class EventResponse {
    public int eventType;
    public Object data;
    public long createdAt;

    public EventResponse(int eventType, Object data, long createdAt) {
        this.eventType = eventType;
        this.data = data;
        this.createdAt = createdAt;
    }
}
