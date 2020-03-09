package messenger.user;

import messenger.auth.SigninNotRequired;
import messenger.auth.TokenManager;
import messenger.db.DatabaseExecutor;
import messenger.db.models.User;
import messenger.error.SimpleValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

class SigninRequest {
    public String email;
    public String password;
}

class SigninResponse {
    public String message;
    public String token;

    public SigninResponse(String msg, String token) {
        this.message = msg;
        this.token = token;
    }
}

@RestController
public class Signin {
    @Autowired
    private DatabaseExecutor databaseExecutor;

    private TokenManager tokenManager = TokenManager.getInstance();

    @RequestMapping(value = "/signin", method = RequestMethod.POST)
    @SigninNotRequired
    public SigninResponse signin(@RequestBody SigninRequest signin) {
        validate(signin);
        String token = manageSignin(signin);
        if (token == null) throw new SimpleValidationException("Email & password does not match any account");
        return new SigninResponse("Signin successful", token);
    }

    private String manageSignin(SigninRequest signin) {
        User user = getUserByEmail(signin.email);
        if (user == null || !user.password.equals(signin.password)) return null;

        String token = tokenManager.generateTokenForUser(user.id, user.name, user.email);
        return token;
    }

    private User getUserByEmail(String email) {
        User user = new User();

        String sql = "SELECT id, `name`, email, password, created_at FROM user WHERE email=?";
        int nRows = databaseExecutor.executeQuery(
                sql,
                preparedStatement -> {
                    preparedStatement.setString(1, email);
                },
                resultSet -> {
                    user.id = resultSet.getLong("id");
                    user.name = resultSet.getString("name");
                    user.email = resultSet.getString("email");
                    user.password = resultSet.getString("password");
                    user.createdAt = resultSet.getLong("created_at");
                }
        );

        return nRows == 0 ? null : user;
    }

    private void validate(SigninRequest signin) {
    }
}
