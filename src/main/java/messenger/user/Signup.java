package messenger.user;

import messenger.db.DatabaseExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

class SignupRequest {
    public String name;
    public String email;
    public String password;
}

class SignupResponse {
    public String message;
}

@RestController
public class Signup {
    @Autowired
    private DatabaseExecutor databaseExecutor;

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public SignupResponse signup(@RequestBody SignupRequest signup) {
        validate(signup);
        createUser(signup.name, signup.email, signup.password);

        SignupResponse response = new SignupResponse();
        response.message = "Signup Success";
        return response;
    }

    private void createUser(String name, String email, String password) {
        String sql = "INSERT INTO USER(`name`, email, password, created_at) VALUES(?, ?, ?, ?)";

        databaseExecutor.executeUpdate(sql, statement -> {
            statement.setString(1, name);
            statement.setString(2, email);
            statement.setString(3, password);
            statement.setLong(4, System.currentTimeMillis());
        });
    }

    private void validate(SignupRequest signup) {

    }
}
