package messenger.user;

import messenger.user.auth.SigninNotRequired;
import messenger.user.auth.TokenManager;
import messenger.db.DatabaseExecutor;
import messenger.db.models.Thread;
import messenger.error.MappedValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

class InitiatorSigninRequest {
    public Long threadID;
    public String secretKey;
}

class InitiatorSigninResponse {
    public String message;
    public String token;
    public String initiatorName;
    public Long threadID;
}

@RestController
public class InitiatorSignin {
    @Autowired
    private DatabaseExecutor databaseExecutor;

    private TokenManager tokenManager = TokenManager.getInstance();

    @RequestMapping(value = "/initiator/signin", method = RequestMethod.POST)
    @SigninNotRequired
    public InitiatorSigninResponse initiatorSignin(@RequestBody InitiatorSigninRequest request) {
        validate(request);
        InitiatorSigninResponse response = manageSignin(request);
        return response;
    }

    private InitiatorSigninResponse manageSignin(InitiatorSigninRequest request) {
        MappedValidationException exception = new MappedValidationException();
        if (request.threadID == null) {
            exception.put("threadID", "Thread Id is required");
            throw exception;
        }

        Thread thread = new Thread();
        String sql = "SELECT id, user_id, name, initiator_name, secret_key, created_at FROM thread WHERE id=?";
        int nRows = databaseExecutor.executeQuery(
                sql,
                preparedStatement -> {
                    preparedStatement.setLong(1, request.threadID);
                },
                resultSet -> {
                    thread.id = resultSet.getLong("id");
                    thread.userID = resultSet.getLong("user_id");
                    thread.name = resultSet.getString("name");
                    thread.initiatorName = resultSet.getString("initiator_name");
                    thread.secretKey = resultSet.getString("secret_key");
                    thread.createdAt = resultSet.getLong("created_at");
                }
        );

        if (!thread.secretKey.equals(request.secretKey)) {
            exception.put("message", "Thread id and secret key does not match any thread");
            throw exception;
        }

        InitiatorSigninResponse response = new InitiatorSigninResponse();
        response.message = "Signin successful";
        response.initiatorName = thread.initiatorName;
        response.threadID = request.threadID;
        response.token = tokenManager.generateTokenForInitiator(thread.userID, request.threadID, thread.initiatorName);
        return response;
    }

    private void validate(InitiatorSigninRequest request) {
    }
}
