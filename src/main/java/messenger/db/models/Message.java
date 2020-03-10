package messenger.db.models;

public class Message {
    public Long id;
    public int sender;
    public Long threadID;
    public String text;
    public String status;
    public Long seenAt;
    public Long sentAt;
}
