package models;

// User model blueprint
public class User {
    private int user_id;
    private String username;
    private String password;
    private String fullName;
    private String role;

    // Constructor
    public User(int user_id, String username, String password, String fullName, String role) {
        this.user_id = user_id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
    }

    // Getters
    public int getId() {
        return user_id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFullName() {
        return fullName;
    }

    public String getRole() {
        return role;
    }

    // Setters (if needed)
    public void setPassword(String password) {
        this.password = password;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    // Polymorphism ensure correct method override for the toString() method
    @Override
    public String toString() {
        return "User{id=" + user_id + ", username='" + username + "', fullName='" + fullName + "', role='" + role
                + "'}";
    }
}