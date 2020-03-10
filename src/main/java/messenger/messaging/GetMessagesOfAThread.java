package messenger.messaging;

import messenger.db.DatabaseExecutor;
import messenger.db.models.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;


class GetMessagesOfAThreadResponse {
    public List<Message> messages = new LinkedList<>();
}

@RestController
public class GetMessagesOfAThread {
    @Autowired
    private DatabaseExecutor databaseExecutor;

    @RequestMapping(value = "/threads/{threadID}", method = RequestMethod.GET)
    public GetMessagesOfAThreadResponse getMessagesOfAThread(@PathVariable Long threadID, @RequestParam Map<String, String> queryParams) {
        GetMessagesOfAThreadResponse response = new GetMessagesOfAThreadResponse();

        response.messages = getMessagesOfAThread(threadID);

        return response;
    }

    private List<Message> getMessagesOfAThread(Long threadID) {
        List<Message> messages = new LinkedList<>();

        String sql = "SELECT id, sender, status, text, seen_at, created_at as sent_at FROM message WHERE thread_id=?";
        sql += " ORDER BY sent_at ASC";
        databaseExecutor.executeQuery(sql,
                preparedStatement -> {
                    preparedStatement.setLong(1, threadID);
                },
                resultSet -> {
                    Message message = new Message();

                    message.id = resultSet.getLong("id");
                    message.sender = resultSet.getInt("sender");
                    message.status = resultSet.getString("status");
                    message.text = resultSet.getString("text");
                    message.seenAt = resultSet.getLong("seen_at");
                    message.sentAt = resultSet.getLong("sent_at");

                    messages.add(message);
                });

        return messages;
    }
}
