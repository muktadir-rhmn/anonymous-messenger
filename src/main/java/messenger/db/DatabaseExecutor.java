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
    private DatabaseManager databaseManager;

    private static DatabaseExecutor databaseExecutor;
    public static DatabaseExecutor getInstance(){
        return databaseExecutor;
    }
    public DatabaseExecutor() {
        DatabaseExecutor.databaseExecutor = this;
    }

    public int executeUpdate(String sql, ValuesSetter valuesSetter) {
        System.out.println(sql);
        Connection connection = databaseManager.getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            if (valuesSetter != null) valuesSetter.setValues(preparedStatement);
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

    public int executeUpdate(String sql) {
        return executeUpdate(sql, null);
    }

    public int executeQuery(String sql, ValuesSetter valuesSetter, RowProcessor rowProcessor) {
        int nRows = 0;
        Connection connection = databaseManager.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            if (valuesSetter != null) valuesSetter.setValues(preparedStatement);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                nRows++;
                rowProcessor.processRow(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            databaseManager.closeConnection(connection);
        }
        return nRows;
    }

    public int executeQuery(String sql, RowProcessor rowProcessor) {
        System.out.println(sql);
        return executeQuery(sql, null, rowProcessor);
    }

}
