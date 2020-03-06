package messenger.messaging;

import messenger.db.DatabaseManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

class NewMessageRequest {
    public String text;
}

class NewMessageResponse {

}

@RestController
public class NewMessage {
    @Autowired
    private DatabaseManager databaseManager;

    @RequestMapping(value = "/threads/{threadID}/new-message", method = RequestMethod.POST)
    public NewMessageResponse newMessage(@CookieValue("userID") Long userID, @PathVariable Long threadID, @RequestBody NewMessageRequest request) {
        validate(request);
        newMessage(userID, threadID, request.text);

        NewMessageResponse response = new NewMessageResponse();
        return response;
    }

    private void newMessage(Long userID, long threadID, String text) {
        int sender = (userID == null ? 1 : 0);

        String sql = "INSERT INTO message(sender, status, text, created_at) VALUES(?, ?, ?, ?);";
        databaseManager.executeUpdate(sql, (ps -> {
            ps.setInt(1, sender);
            ps.setString(2, "unseen");
            ps.setString(3, text);
            ps.setLong(4, System.currentTimeMillis());
        }));
    }

    private void validate(NewMessageRequest request) {
    }
}
