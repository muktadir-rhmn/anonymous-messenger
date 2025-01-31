package messenger.event.events;

import messenger.db.DatabaseExecutor;
import messenger.db.models.Message;
import messenger.error.SimpleValidationException;
import messenger.event.EventDescriptor;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

class NewMessageEventResponse {
    public List<Message> messages;
}


public class NewMessageEventResponseGenerator implements EventResponseGenerator {

    @Override
    public EventResponse generateResponseData(EventDescriptor eventDescriptor, Map<String, Object> data) {
        Integer lastMessageID = (Integer) data.get("lastMessageID");
        if (lastMessageID == null) throw new SimpleValidationException("newMessageEvent listen request must contain lastMessageID");

        String sql = "SELECT id, sender, thread_id, status, text, seen_at, created_at as sent_at FROM message ";
        sql += " WHERE user_id=" + eventDescriptor.userID + " AND id>" + lastMessageID;
        sql += " ORDER BY sent_at DESC;";
        if (eventDescriptor.userID == null) throw new RuntimeException("userID");

        List<Message> messages = new LinkedList<>();
        DatabaseExecutor.getInstance().executeQuery(sql, resultSet -> {
            Message message = new Message();
            message.id = resultSet.getLong("id");
            message.sender = resultSet.getInt("sender");
            message.threadID = resultSet.getLong("thread_id");
            message.status = resultSet.getString("status");
            message.text = resultSet.getString("text");
            message.seenAt = resultSet.getLong("seen_at");
            message.sentAt = resultSet.getLong("sent_at");

            messages.add(message);
        });

        NewMessageEventResponse response = new NewMessageEventResponse();
        response.messages = messages;

        int eventType = eventDescriptor.type;
        return new EventResponse(eventType, response, eventDescriptor.createdAt);
    }
}
