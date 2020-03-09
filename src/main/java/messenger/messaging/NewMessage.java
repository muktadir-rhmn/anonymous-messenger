package messenger.messaging;

import messenger.db.DatabaseExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

class NewMessageRequest {
    public String text;
}

class NewMessageResponse {
    public String message;
}

@RestController
public class NewMessage {
    @Autowired
    private DatabaseExecutor databaseExecutor;

    @RequestMapping(value = "/threads/{threadID}/new-message", method = RequestMethod.POST)
    public NewMessageResponse newMessage(@RequestAttribute("userID") Long userID, @PathVariable Long threadID, @RequestBody NewMessageRequest request) {
        validate(request);
        newMessage(userID, threadID, request.text);

        NewMessageResponse response = new NewMessageResponse();
        response.message = "Sent Successfully";
        return response;
    }

    private void newMessage(Long userID, long threadID, String text) {
        int sender = (userID == null ? 1 : 0);

        String sql = "INSERT INTO message(sender, thread_id, status, text, created_at) VALUES(?, ?, ?, ?, ?);";
        databaseExecutor.executeUpdate(sql, (ps -> {
            ps.setInt(1, sender);
            ps.setLong(2, threadID);
            ps.setString(3, "unseen");
            ps.setString(4, text);
            ps.setLong(5, System.currentTimeMillis());
        }));
    }

    private void validate(NewMessageRequest request) {
    }
}
