package utils;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Manages the single shared JDBC connection to the MySQL database.
 * Reads credentials from the .env file in the project root.
 */
public class DatabaseConfig {

    private static Connection connection = null;

    // Load environment variables from .env file
    private static final Dotenv dotenv = Dotenv.configure()
            .directory("./")
            .ignoreIfMissing()
            .load();

    private static final String URL = dotenv.get("DB_URL");
    private static final String USERNAME = dotenv.get("DB_USER");
    private static final String PASSWORD = dotenv.get("DB_PASSWORD");

    // Return an active connection, creating one if needed
    public static Connection getConnection() {
        try {

            // Create connection if needed
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            }

            return connection;

        } catch (SQLException e) {
            System.err.println("[DB] Connection failed: " + e.getMessage());
            return null;
        }
    }

    // Close the shared connection if it is open
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("[DB] Error closing connection: " + e.getMessage());
        }
    }

    // Returns true if the database is reachable
    public static boolean testConnection() {
        try {
            Connection connect = getConnection();
            return connect != null && !connect.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
