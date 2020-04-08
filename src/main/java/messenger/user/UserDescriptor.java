package messenger.user;

public class UserDescriptor {

    public static final String USER_TYPE_SINGED_IN = "SIGNED_IN";
    public static final String USER_TYPE_INITIATOR = "INITIATOR";

    private String userType;
    private String userName;
    private Long userID;
    private Long threadID;

    public UserDescriptor(String userType, String userName, Long userID, Long threadID) {
        this.userType = userType;
        this.userName = userName;
        this.userID = userID;
        this.threadID = threadID;
    }

    public boolean isInitiator() {
        return userType.equals(USER_TYPE_INITIATOR);
    }

    public boolean isSignedinUser() {
        return userType.equals(USER_TYPE_SINGED_IN);
    }

    public String getUserType() {
        return userType;
    }

    public String getUserName() {
        return userName;
    }

    public Long getUserID() {
        return userID;
    }

    public Long getThreadID() {
        return threadID;
    }
}
