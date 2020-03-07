package messenger.db;

import messenger.config.ConfigurationManager;
import messenger.config.pojos.DatabaseConfiguration;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
public class DatabaseManager {
    private DatabaseConfiguration configuration;

    public DatabaseManager() {
        try {
            configuration = ConfigurationManager.getDatabaseConfiguration();

            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(configuration.url, configuration.userName, configuration.password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void closeConnection(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
