package messenger.event;

import messenger.db.DatabaseExecutor;
import messenger.db.models.Message;
import messenger.error.SimpleValidationException;

import java.util.LinkedList;
import java.util.List;

class NewMessageEventResponse {
    public List<Message> messages;
}

public class NewMessageEvent extends Event {

    public NewMessageEvent(String userType, Long userID, long threadID) {
        super(userType, userID, threadID);
    }
}

class NewMessageEventResponseGenerator implements EventResponseGenerator {

    @Override
    public EventResponse generateResponseData(EventDescriptor eventDescriptor) {
        Integer lastMessageID = (Integer) eventDescriptor.data.get("lastMessageID");
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