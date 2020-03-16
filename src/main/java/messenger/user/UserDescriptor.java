package messenger.user;

import messenger.auth.TokenManager;

public class UserDescriptor {
    private String userType;
    public Long userID;
    public Long threadID; //identifies an initiator

    public UserDescriptor(String userType, Long userID, Long threadID) {
        this.userType = userType;
        this.userID = userID;
        this.threadID = threadID;
    }

    public boolean isInitiator() {
        return userType.equals(TokenManager.USER_TYPE_INITIATOR);
    }

    public boolean isSignedinUser() {
        return userType.equals(TokenManager.USER_TYPE_SINGED_IN);
    }
}
