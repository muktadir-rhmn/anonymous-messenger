package messenger.event;

import messenger.db.DatabaseExecutor;
import messenger.db.models.Message;
import messenger.error.SimpleValidationException;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

class NewMessageEventResponse{
    public int eventType;
    public List<Message> messages;
}

public class NewMessageEvent extends Event {

    NewMessageEvent(){

    }
    public NewMessageEvent(Long userID, long threadID) {
        this.threadID = threadID;
        this.userID = userID;
    }

    @Override
    public String encodeEventData() {
        return null;
    }

    @Override
    public void invalidatePreviousEvents() {
        int eventType = EventManager.getInstance().getEventType(this);
        String sql = "UPDATE event SET invalid=1 WHERE type=" + eventType + " AND user_id=" + userID;
        DatabaseExecutor.getInstance().executeUpdate(sql);
    }


    @Override
    public Object generateResponseData(EventDescriptor eventDescriptor, Map<String, Object> data) {
        Integer lastMessageID = (Integer) data.get("lastMessageID");
        if (lastMessageID == null) throw new SimpleValidationException("newMessageEvent listen request must contain lastMessageID");

        String sql = "SELECT id, sender, thread_id, status, text, seen_at, created_at as sent_at FROM message ";
        sql += " WHERE user_id=" + eventDescriptor.userID + " AND id>" + lastMessageID;
        sql += " ORDER BY sent_at DESC;";

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
        response.eventType = EventManager.getInstance().getEventType(this);
        response.messages = messages;
        return response;
    }
}

