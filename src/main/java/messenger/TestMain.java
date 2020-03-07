package messenger;

import messenger.user.TokenManager;

import java.util.Map;

public class TestMain {
    public static void main(String[] args) {
        TokenManager tokenManager = new TokenManager();
        String token = tokenManager.generateTokenForInitiator("muktadir");
        System.out.println(token);
        Map<String, String> data = tokenManager.verifyToken(token+"kd");
        System.out.println(data);
    }
}
