package messenger.messaging;

import messenger.db.DatabaseExecutor;
import messenger.error.SimpleValidationException;
import messenger.event.EventManager;
import messenger.event.events.NewMessageEvent;
import messenger.user.UserDescriptor;
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
    public NewMessageResponse newMessage(@RequestAttribute("user") UserDescriptor userDescriptor, @PathVariable Long threadID, @RequestBody NewMessageRequest request) {
        validate(request);
        newMessage(userDescriptor, threadID, request.text);
        eventManager.newEvent(new NewMessageEvent(userDescriptor, threadID));

        NewMessageResponse response = new NewMessageResponse();
        response.message = "Sent Successfully";
        return response;
    }

    private void newMessage(UserDescriptor userDescriptor, long threadID, String text) {
        int sender = (userDescriptor.isInitiator() ? 1 : 0);

        String sql = "INSERT INTO message(sender, thread_id, user_id, status, text, created_at) VALUES(?, ?, " + userDescriptor.getUserID() + ", ?, ?, ?);";
        databaseExecutor.executeUpdate(sql, (ps -> {
            ps.setInt(1, sender);
            ps.setLong(2, threadID);
            ps.setString(3, "unseen");
            ps.setString(4, text);
            ps.setLong(5, System.currentTimeMillis());
        }));
    }

    private void validate(NewMessageRequest request) {
        if (request.text.length() < 1) throw new SimpleValidationException("Message must not be Empty");
    }
}
