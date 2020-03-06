package messenger.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface ValuesSetter {
    void setValues(PreparedStatement preparedStatement) throws SQLException;
}
