package messenger.messaging;

import messenger.db.DatabaseExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

class CreateThreadRequest {
    public String name;
    public String initiatorName;
    public String secretKey;
}

class CreateThreadResponse {
}


@RestController
public class CreateThread {
    @Autowired
    private DatabaseExecutor databaseExecutor;

    @RequestMapping(value = "/threads/create", method = RequestMethod.POST)
    public CreateThreadResponse createThread(@CookieValue("userID") String userID, @RequestBody CreateThreadRequest request) {
        validate(request);
        createThread(Long.parseLong(userID), request.name, request.initiatorName, request.secretKey);

        return new CreateThreadResponse();
    }

    private void createThread(long userID, String name, String initiatorName, String secretKey) {
        String sql = "INSERT INTO thread(user_id, `name`, initiator_name, secret_key, created_at) VALUES(?, ?, ?, ?, ?);";

        databaseExecutor.executeUpdate(sql, preparedStatement -> {
            preparedStatement.setLong(1, userID);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, initiatorName);
            preparedStatement.setString(4, secretKey);
            preparedStatement.setLong(5, System.currentTimeMillis());
        });
    }

    private void validate(CreateThreadRequest request) {
    }

}
