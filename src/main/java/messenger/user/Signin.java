package messenger.user;

import messenger.db.DatabaseExecutor;
import messenger.db.models.User;
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

    public SigninResponse(String msg) {
        message = msg;
    }
}

@RestController
public class Signin {
    @Autowired
    private DatabaseExecutor databaseExecutor;

    @RequestMapping(value = "/signin", method = RequestMethod.POST)
    public SigninResponse signin(HttpServletResponse response, @RequestBody SigninRequest signin) {
        validate(signin);
        boolean success = manageSignin(response, signin);

        String msg;
        if (success) msg = "Signin successful";
        else msg = "Email & password does not match any account";
        return new SigninResponse("Signin successful");
    }

    private boolean manageSignin(HttpServletResponse response, SigninRequest signin) {
        User user = getUserByEmail(signin.email);
        if (user == null) return false;

        response.addCookie(new Cookie("userID", user.id + ""));
        response.addCookie(new Cookie("name", user.name));
        response.addCookie(new Cookie("email", user.email));
        return true;
    }

    private User getUserByEmail(String email) {
        User user = new User();;

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
