package dao;

// Data Access Object will be only responsible for running SQL queries
// It will not contain any logic or validation
// It makes the code cleaner and reusable

import models.User;
import utils.DatabaseConfig;

// Imports needed for SQL operations 
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO {

    public User login(String username, String password) {
        try {
            // Fetch user data
            String query = "SELECT * FROM users WHERE username = ? AND password = ? ";

            // Get connection
            Connection connect = DatabaseConfig.getConnection();

            // Prepared statement for SQL injection prevention
            PreparedStatement prepstate = connect.prepareStatement(query);
            prepstate.setString(1, username);
            prepstate.setString(2, password);

            // Execute query
            ResultSet result = prepstate.executeQuery();

            if (result.next()) {
                String role = result.getString("role");
                if (role.equals("ADMIN")) {
                    System.out.println("Admin found");
                    return null;
                } else if (role.equals("USER")) {
                    System.out.println("User found");
                    return null;
                }
            } else {
                System.out.println("user not found");
            }

        } catch (Exception e) {
            System.out.println("[DB] Connection failed: " + e.getMessage());
            return null;
        }
        return null;
    }

}