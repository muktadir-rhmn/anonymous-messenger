package messenger.event;

import java.util.Map;

class EventDescriptor {
    public Long id;
    public String userType;
    public Long userID;
    public Long threadID;
    public Integer type;
    public Map<String, Object> data;
    public Long createdAt;
}
