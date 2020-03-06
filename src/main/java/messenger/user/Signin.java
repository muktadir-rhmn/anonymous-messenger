package messenger.user;

import messenger.db.DatabaseManager;
import messenger.db.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
    private DatabaseManager databaseManager;

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
        User user = null;

        String sql = "SELECT id, `name`, email, password, created_at FROM user WHERE email=?";
        Connection connection = databaseManager.getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, email);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                user = new User();
                user.id = resultSet.getLong("id");
                user.name = resultSet.getString("name");
                user.email = resultSet.getString("email");
                user.password = resultSet.getString("password");
                user.createdAt = resultSet.getLong("created_at");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    private void validate(SigninRequest signin) {
    }
}
