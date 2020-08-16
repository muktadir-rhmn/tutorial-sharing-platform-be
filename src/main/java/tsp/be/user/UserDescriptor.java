package tsp.be.user;

public class UserDescriptor {
    public final static String USER_TYPE_ADMIN = "admin";

    private String userID;
    private String userEmail;
    private String userName;
    private String userType;

    public UserDescriptor(String userID, String userEmail, String userName, String userType) {
        this.userID = userID;
        this.userEmail = userEmail;
        this.userName = userName;
        this.userType = userType;
    }

    public String getUserID() {
        return userID;
    }

    public String getUserEmail() {return userEmail;}

    public String getUserName() {
        return userName;
    }

    public boolean hasAccess(String requiredUserType) {
        return this.userType.equals(requiredUserType);
    }
}
