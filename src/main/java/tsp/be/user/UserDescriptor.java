package tsp.be.user;

public class UserDescriptor {

    private long userID;
    private String userEmail;
    private String userName;

    public UserDescriptor(Long userID, String userEmail, String userName) {
        this.userID = userID;
        this.userEmail = userEmail;
        this.userName = userName;
    }

    public long getUserID() {
        return userID;
    }

    public String getUserEmail() {return userEmail;}

    public String getUserName() {
        return userName;
    }
}
