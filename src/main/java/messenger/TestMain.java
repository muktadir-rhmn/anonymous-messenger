package messenger;

import messenger.config.ConfigurationManager;
import messenger.config.pojos.DatabaseConfiguration;
import messenger.config.pojos.JWTConfiguration;

public class TestMain {
    public static void main(String[] args) {
        JWTConfiguration configuration = ConfigurationManager.getJWTConfiguration();
        System.out.println(configuration.secretKey);
    }
}
