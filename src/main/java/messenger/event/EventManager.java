package messenger.event;

import messenger.db.DatabaseExecutor;
import messenger.user.UserDescriptor;
import messenger.utils.JsonConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.*;

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
    private static Map<String, Integer> eventType;
    static {
        eventType = new HashMap<>(10);
        eventType.put(NewMessageEvent.class.getSimpleName(), 0);
        eventType.put(MessageSeenEvent.class.getSimpleName(), 1);
        eventType.put(TypingEvent.class.getSimpleName(), 2);
    }

    public int getEventType(Event event) {
        return eventType.get(event.getClass().getSimpleName());
    }

    public static Collection<Integer> eventTypes() {
        return eventType.values();
    }

    private static Map<Integer, EventResponseGenerator> eventInstance;
    static {
        eventInstance = new HashMap<>(10);
        eventInstance.put(eventType.get(NewMessageEvent.class.getSimpleName()), new NewMessageEventResponseGenerator());
        eventInstance.put(eventType.get(MessageSeenEvent.class.getSimpleName()), new MessageSeenEventResponseGenerator());
        eventInstance.put(eventType.get(TypingEvent.class.getSimpleName()), new TypingEventResponseGenerator());
    }
    public EventResponseGenerator getResponseGenerator(int eventType) {
        return eventInstance.get(eventType);
    }

    @Autowired
    private AsyncEventProcessor asyncEventProcessor;

    @Autowired
    private DatabaseExecutor databaseExecutor;

    private JsonConverter jsonConverter = new JsonConverter();

    public void newEvent(Event event) {
        event.invalidatePreviousEvents();
        storeInDB(event);
        asyncEventProcessor.enqueueEvent(event);
    }

    public void addEventListener(DeferredResult<ListenResponse> deferredResult, UserDescriptor userDescriptor, Integer eventType, Map<String, Object> eventData) {
        asyncEventProcessor.addEventListener(deferredResult, userDescriptor, eventType, eventData);
    }

    public List<Object> getEventResponses(UserDescriptor userDescriptor, Long lastEventTime, Integer eventType, Map<String, Object> listenerData) {
        String sql = "SELECT id, type, creator_type, user_id, thread_id, data, created_at FROM event WHERE created_at > " + lastEventTime + " AND invalid=0 AND type=" + eventType + " AND";
        if (userDescriptor.isSignedinUser()) sql += " user_id=" + userDescriptor.userID;
        else if (userDescriptor.isInitiator()) sql += " thread_id=" + userDescriptor.threadID;
        else throw new RuntimeException("Either userID or threadID must be non-Null");

        List<Object> responses = new LinkedList<>();
        databaseExecutor.executeQuery(sql, resultSet -> {
            EventDescriptor descriptor = new EventDescriptor();
            descriptor.id = resultSet.getLong("id");
            descriptor.userType = resultSet.getString("creator_type");
            descriptor.threadID = resultSet.getLong("thread_id");
            descriptor.userID = resultSet.getLong("user_id");
            descriptor.type= resultSet.getInt("type");
            descriptor.data = jsonConverter.jsonToMap(resultSet.getString("data"));
            descriptor.createdAt = resultSet.getLong("created_at");

            Object response = getResponseGenerator(descriptor.type).generateResponseData(descriptor, listenerData);
            responses.add(response);
        });

        return responses;
    }

    private void storeInDB(Event event) {
        int eventType = getEventType(event);

        String sql = "INSERT INTO event(type, creator_type, user_id, thread_id, data, created_at) VALUES(?, ?, " + event.eventDescriptor.userID + ", ?, ?, ?)";
        DatabaseExecutor.getInstance().executeUpdate(sql, preparedStatement -> {
            preparedStatement.setInt(1, eventType);
            preparedStatement.setString(2, event.eventDescriptor.userType);
            preparedStatement.setLong(3, event.eventDescriptor.threadID);
            preparedStatement.setString(4, jsonConverter.mapToJson(event.eventDescriptor.data));
            preparedStatement.setLong(5, event.eventDescriptor.createdAt);
        });
    }
}
