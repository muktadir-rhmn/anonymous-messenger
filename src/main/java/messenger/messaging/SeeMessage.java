package messenger.messaging;

import messenger.user.UserDescriptor;
import messenger.user.auth.TokenManager;
import messenger.db.DatabaseExecutor;
import messenger.error.SimpleValidationException;
import messenger.event.EventManager;
import messenger.event.events.MessageSeenEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

class SeeMessageResponse {
    public String message;

    public SeeMessageResponse(String message) {
        this.message = message;
    }
}


@RestController
public class SeeMessage {
    @Autowired
    private DatabaseExecutor databaseExecutor;

    @Autowired
    private EventManager eventManager;

    @RequestMapping(value = "/threads/{threadID}/messages/{messageID}/see-message", method = RequestMethod.POST)
    public SeeMessageResponse seeMessage(
            @RequestAttribute("user") UserDescriptor userDescriptor,
            @PathVariable Long threadID,
            @PathVariable Long messageID
    ) {
        long seenAt = System.currentTimeMillis();
        setMessageStatusToSeen(userDescriptor, threadID, messageID, seenAt);
        eventManager.newEvent(new MessageSeenEvent(userDescriptor, threadID, messageID, seenAt));
        return new SeeMessageResponse("Successful");
    }

    private void setMessageStatusToSeen(UserDescriptor userDescriptor, long threadID, long messageID, long seenAt) {
        int sender = (userDescriptor.isInitiator()? 0 : 1);

        String sql = "UPDATE  message SET status='seen', seen_at = " + seenAt + " WHERE id<=? AND sender=? AND user_id=? AND thread_id=?";
        int rowAffected = databaseExecutor.executeUpdate(sql, preparedStatement -> {
            preparedStatement.setLong(1, messageID);
            preparedStatement.setInt(2, sender);
            preparedStatement.setLong(3, userDescriptor.getUserID());
            preparedStatement.setLong(4, threadID);
        });

        if (rowAffected == 0) throw new SimpleValidationException("Invalid data");
    }

}
