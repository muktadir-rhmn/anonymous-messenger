package messenger.event;

import messenger.db.DatabaseExecutor;
import messenger.error.SimpleValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class EventManager {

    private static EventManager eventManager;
    public static EventManager getInstance() {return eventManager;}

    public EventManager() {
        eventManager = this;
    }

    /**
     * I could use class name of each event class (like NewMessageEvent.class.getSimpleName()).
     * But not used, as it will tightly couple event type with the class name. So, if we need to change an event name,
     * we will have to touch database;
     */
    private static Map<Class, Integer> eventType;
    static {
        eventType = new HashMap<>(10);
        eventType.put(NewMessageEvent.class, 0);
    }

    public int getEventType(Event event) {
        return eventType.get(this.getClass());
    }

    private static Map<Integer, EventResponseGenerator> eventInstance;
    static {
        eventInstance = new HashMap<>(10);
        eventInstance.put(0, new NewMessageEvent());
    }
    public EventResponseGenerator getResponseGenerator(int eventType) {
        return eventInstance.get(eventType);
    }

    @Autowired
    private EventProcessor eventProcessor;

    @Autowired
    private DatabaseExecutor databaseExecutor;

    public void receive(Event event) {
        storeInDB(event);
        eventProcessor.enqueueEvent(event);
    }

    private void storeInDB(Event event) {
        int eventType = getEventType(event);
        Long userID = event.userID;
        Long threadID = event.threadID;
        String data = event.encodeEventData();
        long createdAt = System.currentTimeMillis();

        String sql = "INSERT INTO event(type, user_id, thread_id, data, created_at) VALUES(?, ?, ?, ?, ?)";
        DatabaseExecutor.getInstance().executeUpdate(sql, preparedStatement -> {
            preparedStatement.setInt(1, eventType);
            preparedStatement.setLong(2, userID);
            preparedStatement.setLong(3, threadID);
            preparedStatement.setString(4, data);
            preparedStatement.setLong(5, createdAt);
        });
    }


    public List<Object> getEventResponses(Long userID, Long threadID, Map<String, Object> data) {
        Long lastEventID = (Long) data.get("lastEventID");
        if (lastEventID == null) throw new SimpleValidationException("Listener request must contain lastEventID");

        String sql = "SELECT id, type, data, created_at FROM event WHERE id > " + lastEventID + " AND ";
        if (userID != null) sql += " user_id=" + userID;
        else if (threadID != null) sql += " thread_id=" + threadID;
        else throw new RuntimeException("Either userID or threadID must be non-Null");

        List<Object> responses = new LinkedList<>();
        databaseExecutor.executeQuery(sql, resultSet -> {
            EventDescriptor descriptor = new EventDescriptor();
            descriptor.id = resultSet.getLong("id");
            descriptor.type= resultSet.getInt("type");
            descriptor.data = resultSet.getString("data");
            descriptor.createdAt = resultSet.getLong("created_at");

            Object response = getResponseGenerator(descriptor.type).generateResponseData(descriptor, data);
            responses.add(response);
        });

        return responses;
    }
}
