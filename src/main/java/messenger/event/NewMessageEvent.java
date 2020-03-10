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
    public Object generateResponseData(EventDescriptor eventDescriptor, Map<String, Object> data) {
        Long lastMessageID = (Long) data.get("lastMessageID");
        if (lastMessageID == null) throw new SimpleValidationException("newMessageEvent listen request must contain lastMessageID");

        String sql = "SELECT id, sender, status, text, seen_at, created_at as sent_at FROM message ";
        sql += " WHERE thread_id=" + eventDescriptor.threadID + " AND id>" + lastMessageID;

        List<Message> messages = new LinkedList<>();
        DatabaseExecutor.getInstance().executeQuery(sql, resultSet -> {
            Message message = new Message();
            message.id = resultSet.getLong("id");
            message.sender = resultSet.getInt("sender");
            message.status = resultSet.getString("status");
            message.text = resultSet.getString("text");
            message.seenAt = resultSet.getLong("seen_at");
            System.out.println(message.seenAt);
            message.sentAt = resultSet.getLong("sent_at");

            messages.add(message);
        });

        NewMessageEventResponse response = new NewMessageEventResponse();
        response.eventType = EventManager.getInstance().getEventType(this);
        response.messages = messages;
        return response;
    }
}

