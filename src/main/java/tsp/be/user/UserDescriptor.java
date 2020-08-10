package tsp.be.user;

public class UserDescriptor {

    private String userID;
    private String userEmail;
    private String userName;

    public UserDescriptor(String userID, String userEmail, String userName) {
        this.userID = userID;
        this.userEmail = userEmail;
        this.userName = userName;
    }

    public String getUserID() {
        return userID;
    }

    public String getUserEmail() {return userEmail;}

    public String getUserName() {
        return userName;
    }
}
