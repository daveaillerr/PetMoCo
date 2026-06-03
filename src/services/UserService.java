package services;

import dao.UserDAO;
import models.User;
import utils.HashPassword;

public class UserService {
    private final UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    /**
     * Step-by-Step Login Process:
     * 1. Check if input is valid (not null/empty).
     * 2. Hash the plain-text password using SHA-256 (to match DB storage).
     * 3. Call UserDAO to query the database with the hashed password.
     * 4. Return the logged-in User object (which is polymorphically an Admin or
     * PetOwner).
     */
    public User login(String username, String password) {
        // Step 1: Input Validation
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            System.out.println("[Validation] Username and password cannot be empty.");
            return null;
        }

        // Step 2: Hash the password
        String hashedPassword = HashPassword.hashPassword(password);

        // Step 3 & 4: Attempt Login via DAO
        return userDAO.login(username.trim(), hashedPassword);
    }

    /**
     * Step-by-Step Admin Registration Process:
     * 1. Validate inputs.
     * 2. Hash the password.
     * 3. Call UserDAO to insert the Admin credentials.
     */
    public boolean registerAdmin(String username, String password) {
        // Step 1: Input Validation
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            System.out.println("[Validation] Credentials cannot be empty.");
            return false;
        }

        // Step 2: Hash the password
        String hashedPassword = HashPassword.hashPassword(password);

        // Step 3: Insert into Database via DAO
        return userDAO.registerAdmin(username.trim(), hashedPassword);
    }

    /**
     * Step-by-Step PetOwner (User) Registration Process:
     * 1. Validate credentials and profile details.
     * 2. Hash the password.
     * 3. Call UserDAO to insert both the credentials and the profile details inside
     * a transaction.
     */
    public boolean registerPetOwner(String username, String password, String name,
            String email, String contact, String address) {
        // Step 1: Input Validation if email and/or password is null
        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty() ||
                name == null || name.trim().isEmpty()) {
            System.out.println("[Validation] Username, password, and name are required.");
            return false;
        }

        // Step 2: Hash the password
        String hashedPassword = HashPassword.hashPassword(password);

        // Step 3: Insert into Database via DAO
        return userDAO.registerPetOwner(
                username.trim(),
                hashedPassword,
                name.trim(),
                email != null ? email.trim() : "",
                contact != null ? contact.trim() : "",
                address != null ? address.trim() : "");
    }
}
