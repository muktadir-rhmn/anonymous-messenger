package messenger.user;

import messenger.user.auth.SigninNotRequired;
import messenger.db.DatabaseExecutor;
import messenger.error.MappedValidationException;
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

    @SigninNotRequired
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
        MappedValidationException mappedValidationException = new MappedValidationException();

        if (signup.email.length() == 0) mappedValidationException.put("email", "You must put email address");
        if (signup.name.length() == 0) mappedValidationException.put("name", "You must give a user name");
        if (signup.password.length() < 8) mappedValidationException.put("password", "Password must conatain at least 8 characters");

        if (!mappedValidationException.isEmpty()) throw mappedValidationException;
    }
}
