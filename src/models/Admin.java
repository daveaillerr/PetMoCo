package models;

public class Admin extends User {
    public Admin(int userId, String username, String password) {
        super(userId, username, password, Role.ADMIN);
    }

    @Override
    public String toString() {
        return "Admin [ID: " + userId + ", Username: " + username + "]";
    }
}
