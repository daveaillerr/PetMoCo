package dao;

// Data Access Object will be only responsible for running SQL queries
// It will not contain any logic or validation
// It makes the code cleaner and reusable

import models.*;
import utils.DatabaseConfig;

// Imports needed for SQL operations 
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO {

    public User login(String username, String password) {
        String query = "SELECT u.user_id, u.username, u.password, u.role, " +
                       "       po.name, po.email_address, po.contact_number, po.home_address " +
                       "FROM users u " +
                       "LEFT JOIN pet_owner po ON u.user_id = po.user_id " +
                       "WHERE u.username = ? AND u.password = ?";

        try {
            Connection connect = DatabaseConfig.getConnection();
            PreparedStatement prepstate = connect.prepareStatement(query);
            prepstate.setString(1, username);
            prepstate.setString(2, password);

            ResultSet result = prepstate.executeQuery();

            if (result.next()) {
                int id = result.getInt("user_id");
                String roleStr = result.getString("role");
                Role role = Role.valueOf(roleStr.toUpperCase());

                if (role == Role.ADMIN) {
                    System.out.println("Admin found");
                    return new Admin(id, username, password);
                } else if (role == Role.USER) {
                    System.out.println("User/PetOwner found");
                    return new PetOwner(
                        id,
                        username,
                        password,
                        result.getString("name"),
                        result.getString("email_address"),
                        result.getString("contact_number"),
                        result.getString("home_address")
                    );
                }
            } else {
                System.out.println("User not found");
            }

        } catch (Exception e) {
            System.out.println("[DB] Connection failed: " + e.getMessage());
            return null;
        }
        return null;
    }

    public boolean registerAdmin(String username, String password) {
        String query = "INSERT INTO users (username, password, role) VALUES (?, ?, 'ADMIN')";
        try {
            Connection connect = DatabaseConfig.getConnection();
            PreparedStatement prepstate = connect.prepareStatement(query);
            prepstate.setString(1, username);
            prepstate.setString(2, password);
            return prepstate.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("[DB] Admin registration failed: " + e.getMessage());
            return false;
        }
    }

    public boolean registerPetOwner(String username, String password, String name, String email, String contact, String address) {
        String insertUserQuery = "INSERT INTO users (username, password, role) VALUES (?, ?, 'USER')";
        String insertOwnerQuery = "INSERT INTO pet_owner (user_id, name, email_address, contact_number, home_address) VALUES (?, ?, ?, ?, ?)";
        
        Connection connect = null;
        try {
            connect = DatabaseConfig.getConnection();
            connect.setAutoCommit(false); // Start transaction
            
            // 1. Insert into users
            PreparedStatement userStmt = connect.prepareStatement(insertUserQuery, java.sql.Statement.RETURN_GENERATED_KEYS);
            userStmt.setString(1, username);
            userStmt.setString(2, password);
            userStmt.executeUpdate();
            
            // Get user_id
            ResultSet generatedKeys = userStmt.getGeneratedKeys();
            if (!generatedKeys.next()) {
                throw new SQLException("Failed to retrieve generated user ID.");
            }
            int userId = generatedKeys.getInt(1);
            
            // 2. Insert into pet_owner
            PreparedStatement ownerStmt = connect.prepareStatement(insertOwnerQuery);
            ownerStmt.setInt(1, userId);
            ownerStmt.setString(2, name);
            ownerStmt.setString(3, email);
            ownerStmt.setString(4, contact);
            ownerStmt.setString(5, address);
            ownerStmt.executeUpdate();
            
            connect.commit(); // Commit transaction
            return true;
            
        } catch (Exception e) {
            if (connect != null) {
                try {
                    connect.rollback(); // Rollback transaction on failure
                } catch (SQLException rollbackEx) {
                    System.out.println("[DB] Rollback failed: " + rollbackEx.getMessage());
                }
            }
            System.out.println("[DB] PetOwner registration failed: " + e.getMessage());
            return false;
        } finally {
            if (connect != null) {
                try {
                    connect.setAutoCommit(true); // Reset auto-commit state
                } catch (SQLException e) {
                    // Ignore
                }
            }
        }
    }

    /**
     * Looks up the pet_owner_id for a given user_id.
     * Needed because the pet table FK references pet_owner.pet_owner_id,
     * not users.user_id directly.
     *
     * @return the pet_owner_id, or -1 if not found
     */
    public int findPetOwnerIdByUserId(int userId) {
        String query = "SELECT pet_owner_id FROM pet_owner WHERE user_id = ?";
        try {
            Connection connect = DatabaseConfig.getConnection();
            PreparedStatement stmt = connect.prepareStatement(query);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("pet_owner_id");
            }
        } catch (SQLException e) {
            System.out.println("[DB] Find pet owner ID failed: " + e.getMessage());
        }
        return -1;
    }
}