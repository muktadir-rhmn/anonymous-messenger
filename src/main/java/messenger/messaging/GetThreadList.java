package messenger.messaging;

import messenger.db.DatabaseExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

class ThreadDescriptor {
    public Long id;
    public String name;
    public String initiatorName;
    public String lastMessage;
    public Long lastActiveAt;
}

class GetThreadListResponse {
    public List<ThreadDescriptor> threads = new LinkedList<>();
}

@RestController
public class GetThreadList {
    @Autowired
    private DatabaseExecutor databaseExecutor;

    @RequestMapping(value = "/threads", method = RequestMethod.GET)
    public GetThreadListResponse getThreadList(@RequestAttribute("userID") String userID) {
        return fetchThreadList(userID);
    }

    private GetThreadListResponse fetchThreadList(String userID) {
        GetThreadListResponse response = new GetThreadListResponse();

        String sql = "SELECT id, user_id, `name`, initiator_name, created_at FROM thread WHERE user_id=?";
        databaseExecutor.executeQuery(
                sql,
                preparedStatement -> {
                    preparedStatement.setLong(1, Long.parseLong(userID));
                },
                resultSet -> {
                    ThreadDescriptor thread = new ThreadDescriptor();

                    thread.id = resultSet.getLong("id");
                    thread.name = resultSet.getString("name");
                    thread.initiatorName = resultSet.getString("initiator_name");

                    response.threads.add(thread);
                }
        );
        populateLastMessageInfo(response.threads);

        return response;
    }

    private void populateLastMessageInfo(List<ThreadDescriptor> threads) {
        if (threads.size() == 0) return;

        Map<Long, ThreadDescriptor> descriptorMap = new HashMap<>();
        StringBuilder idSetString = new StringBuilder("(");
        for (ThreadDescriptor t: threads) {
            descriptorMap.put(t.id, t);
            idSetString.append(t.id).append(',');
        }
        idSetString.setCharAt(idSetString.length() - 1, ')');

        String sql = " (SELECT thread_id, max(created_at) as last_message_at FROM message WHERE thread_id IN " + idSetString.toString() + " GROUP BY thread_id) ";
        sql = "SELECT thread_id, created_at as last_message_at, text as last_message FROM message WHERE (thread_id, created_at) IN " + sql;
        sql += " ORDER BY last_message_at DESC";
        databaseExecutor.executeQuery(
                sql,
                resultSet -> {
                    ThreadDescriptor d = descriptorMap.get(resultSet.getLong("thread_id"));
                    d.lastMessage = resultSet.getString("last_message");
                    d.lastActiveAt = resultSet.getLong("last_message_at");
                }
        );
    }
}
