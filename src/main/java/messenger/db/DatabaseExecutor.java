package messenger.db;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class DatabaseExecutor {
    @Autowired
    DatabaseManager databaseManager;

    public int executeUpdate(String sql, ValuesSetter valuesSetter) {
        Connection connection = databaseManager.getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            valuesSetter.setValues(preparedStatement);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            databaseManager.closeConnection(connection);

            //todo: handle this case
            e.printStackTrace();
        } finally {
            databaseManager.closeConnection(connection);
        }
        return 0;
    }

    public void executeQuery(String sql, ValuesSetter valuesSetter, RowProcessor rowProcessor) {
        Connection connection = databaseManager.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            if (valuesSetter != null) valuesSetter.setValues(preparedStatement);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                rowProcessor.processRow(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            databaseManager.closeConnection(connection);
        }
    }

    public void executeQuery(String sql, RowProcessor rowProcessor) {
        executeQuery(sql, null, rowProcessor);
    }

}
