package messenger.messaging;

import messenger.auth.TokenManager;
import messenger.db.DatabaseExecutor;
import messenger.error.SimpleValidationException;
import messenger.event.EventManager;
import messenger.event.MessageSeenEvent;
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

    @RequestMapping(value = "/thread/{threadID}/messages/{messageID}/seeMessage", method = RequestMethod.POST)
    public SeeMessageResponse seeMessage(
            @RequestAttribute("userType") String userType,
            @RequestAttribute("userID") Long userID,
            @PathVariable Long threadID,
            @PathVariable Long messageID
    ) {
        setMessageStatusToSeen(userType, userID, threadID, messageID);
        eventManager.receive(new MessageSeenEvent(userType, userID, threadID, messageID));
        return new SeeMessageResponse("Successful");
    }

    private void setMessageStatusToSeen(String userType, long userID, long threadID, long messageID) {
        int sender = (userType.equals(TokenManager.USER_TYPE_INITIATOR)? 0 : 1);

        String sql = "UPDATE TABLE message SET status='seen' WHERE id<=? AND sender=? AND user_id=? AND thread_id=?";
        int rowAffected = databaseExecutor.executeUpdate(sql, preparedStatement -> {
            preparedStatement.setLong(1, messageID);
            preparedStatement.setInt(2, sender);
            preparedStatement.setLong(3, userID);
            preparedStatement.setLong(4, threadID);
        });

        if (rowAffected != 1) throw new SimpleValidationException("Invalid data");
    }

}
