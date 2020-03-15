package messenger.messaging;

import messenger.auth.TokenManager;
import messenger.db.DatabaseExecutor;
import messenger.event.EventManager;
import messenger.event.NewMessageEvent;
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

    @Autowired
    private EventManager eventManager;

    @RequestMapping(value = "/threads/{threadID}/new-message", method = RequestMethod.POST)
    public NewMessageResponse newMessage(@RequestAttribute("userType") String userType, @RequestAttribute(value = "userID", required = false) Long userID, @PathVariable Long threadID, @RequestBody NewMessageRequest request) {
        validate(request);
        newMessage(userType, userID, threadID, request.text);
        eventManager.receive(new NewMessageEvent(userType, userID, threadID));

        NewMessageResponse response = new NewMessageResponse();
        response.message = "Sent Successfully";
        return response;
    }

    private void newMessage(String userType, Long userID, long threadID, String text) {
        int sender = (userType.equals(TokenManager.USER_TYPE_INITIATOR)? 1 : 0);

        String sql = "INSERT INTO message(sender, thread_id, user_id, status, text, created_at) VALUES(?, ?, "+userID+", ?, ?, ?);";
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
