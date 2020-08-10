package tsp.be.user.models;

public class User {
    public static final String USER_TYPE_MEMBER = "member";
    public static final String USER_TYPE_ADMIN = "admin";

    public String id;
    public String name;
    public String email;
    public String password;
    public String userType;
    public Long createdAt;
}
