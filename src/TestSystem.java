import dao.UserDAO;
import models.*;
import utils.DatabaseConfig;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TestSystem {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("           PETMOCO SYSTEM AUTH TEST               ");
        System.out.println("==================================================");

        // 1. Connect to Database
        if (!DatabaseConfig.testConnection()) {
            System.err.println("[FAIL] Cannot connect to database. Please make sure MySQL is running.");
            System.exit(1);
        }
        System.out.println("[PASS] Database connected successfully.");

        // Automatically initialize database schema from script.sql
        initializeDatabaseSchema();

        UserDAO userDAO = new UserDAO();

        // Generate unique usernames to avoid duplicate key issues on repeated runs
        long timestamp = System.currentTimeMillis();
        String adminUser = "test_admin_" + timestamp;
        String adminPass = "pass123";

        String petOwnerUser = "test_owner_" + timestamp;
        String petOwnerPass = "pass123";

        System.out.println("\n--- Step 1: Testing Registration ---");

        // 2. Register Admin
        System.out.println("Registering Admin: " + adminUser);
        boolean adminRegistered = userDAO.registerAdmin(adminUser, adminPass);
        if (adminRegistered) {
            System.out.println("[PASS] Admin registered successfully.");
        } else {
            System.out.println("[FAIL] Admin registration failed.");
        }

        // 3. Register PetOwner (User)
        System.out.println("\nRegistering PetOwner: " + petOwnerUser);
        boolean ownerRegistered = userDAO.registerPetOwner(
                petOwnerUser,
                petOwnerPass,
                "Test Owner Full Name",
                "testowner@example.com",
                "+639123456789",
                "123 Main St, Quezon City");
        if (ownerRegistered) {
            System.out.println("[PASS] PetOwner registered successfully.");
        } else {
            System.out.println("[FAIL] PetOwner registration failed.");
        }

        System.out.println("\n--- Step 2: Testing Login & Polymorphism ---");

        // 4. Test Admin Login
        System.out.println("Logging in as Admin: " + adminUser);
        User loggedAdmin = userDAO.login(adminUser, adminPass);
        if (loggedAdmin != null) {
            System.out.println("[PASS] Admin login succeeded.");
            System.out.println("Returned object string: " + loggedAdmin);

            // Check Polymorphism
            if (loggedAdmin instanceof Admin) {
                System.out.println("[PASS] Success: Object is an instance of Admin!");
            } else {
                System.out.println("[FAIL] Error: Object is not an instance of Admin.");
            }
        } else {
            System.out.println("[FAIL] Admin login failed.");
        }

        // 5. Test PetOwner Login
        System.out.println("\nLogging in as PetOwner: " + petOwnerUser);
        User loggedOwner = userDAO.login(petOwnerUser, petOwnerPass);
        if (loggedOwner != null) {
            System.out.println("[PASS] PetOwner login succeeded.");
            System.out.println("Returned object string: " + loggedOwner);

            // Check Polymorphism & access subclass details
            if (loggedOwner instanceof PetOwner) {
                System.out.println("[PASS] Success: Object is an instance of PetOwner!");
                PetOwner owner = (PetOwner) loggedOwner;
                System.out.println("      Name:    " + owner.getFullName());
                System.out.println("      Email:   " + owner.getEmailAddress());
                System.out.println("      Contact: " + owner.getContactNumber());
                System.out.println("      Address: " + owner.getHomeAddress());
            } else {
                System.out.println("[FAIL] Error: Object is not an instance of PetOwner.");
            }
        } else {
            System.out.println("[FAIL] PetOwner login failed.");
        }

        System.out.println("\n--- Step 3: Cleaning up test records ---");
        cleanupUser(adminUser);
        cleanupUser(petOwnerUser);
        System.out.println("[PASS] Cleaned up test records from database.");

        DatabaseConfig.closeConnection();
        System.out.println("\n==================================================");
        System.out.println("               ALL TESTS COMPLETED                ");
        System.out.println("==================================================");
    }

    private static void cleanupUser(String username) {
        String getUserIdQuery = "SELECT user_id FROM users WHERE username = ?";
        String deleteOwnerQuery = "DELETE FROM pet_owner WHERE user_id = ?";
        String deleteUserQuery = "DELETE FROM users WHERE username = ?";

        try (Connection connect = DatabaseConfig.getConnection()) {
            int userId = -1;
            try (PreparedStatement getStmt = connect.prepareStatement(getUserIdQuery)) {
                getStmt.setString(1, username);
                try (var rs = getStmt.executeQuery()) {
                    if (rs.next()) {
                        userId = rs.getInt("user_id");
                    }
                }
            }

            if (userId != -1) {
                try (PreparedStatement delOwnerStmt = connect.prepareStatement(deleteOwnerQuery)) {
                    delOwnerStmt.setInt(1, userId);
                    delOwnerStmt.executeUpdate();
                }
            }

            try (PreparedStatement delUserStmt = connect.prepareStatement(deleteUserQuery)) {
                delUserStmt.setString(1, username);
                delUserStmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Cleanup error for " + username + ": " + e.getMessage());
        }
    }

    private static void initializeDatabaseSchema() {
        System.out.println("Initializing database schema from src/data/script.sql...");

        java.io.File sqlFile = new java.io.File("src/data/script.sql");
        if (!sqlFile.exists()) {
            System.err.println("[FAIL] SQL script file src/data/script.sql not found.");
            return;
        }

        try (Connection connect = DatabaseConfig.getConnection();
                java.sql.Statement statement = connect.createStatement()) {

            // Read lines and strip single-line comments
            java.util.List<String> lines = java.nio.file.Files.readAllLines(sqlFile.toPath(),
                    java.nio.charset.StandardCharsets.UTF_8);
            StringBuilder sb = new StringBuilder();
            for (String line : lines) {
                String trimmedLine = line.trim();
                if (!trimmedLine.startsWith("--") && !trimmedLine.startsWith("#")) {
                    sb.append(line).append("\n");
                }
            }
            String sqlContent = sb.toString();

            // Split by semicolon to execute separate statements
            String[] queries = sqlContent.split(";");
            for (String query : queries) {
                String trimmedQuery = query.trim();

                // Skip empty blocks or commands we don't need to run in JDBC
                if (trimmedQuery.isEmpty() ||
                        trimmedQuery.toUpperCase().startsWith("SHOW") ||
                        trimmedQuery.toUpperCase().startsWith("DESCRIBE")) {
                    continue;
                }

                try {
                    statement.execute(trimmedQuery);
                } catch (SQLException e) {
                    // It is perfectly normal to get warnings or "table already exists" errors.
                    // We print other genuine errors for info, but continue execution.
                    if (!e.getMessage().contains("already exists")
                            && !e.getMessage().contains("Database already exists")) {
                        System.out.println("Executing SQL fragment info: " + e.getMessage());
                    }
                }
            }
            System.out.println("[PASS] Database schema initialization complete.");
        } catch (Exception e) {
            System.err.println("[FAIL] Failed to read or execute schema: " + e.getMessage());
        }
    }
}
