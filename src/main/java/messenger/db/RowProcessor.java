package messenger.db;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface RowProcessor {
    void processRow(ResultSet resultSet) throws SQLException;
}
