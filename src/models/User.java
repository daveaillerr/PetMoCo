
package models;

public abstract class User {
    protected int userId;
    protected String username;
    protected String password;
    protected Role role;

    public User(int userId, String username, String password, Role role) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Common Getters
    public int getId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Abstract method — forces each subclass to define its own display format.
     * Demonstrates abstraction: subclasses MUST implement this method.
     */
    public abstract String getDisplayInfo();
}

