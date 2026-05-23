package system.config;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestConnection {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        String url = dotenv.get("DB_URL");
        String username = dotenv.get("DB_USER");
        String password = dotenv.get("DB_PASSWORD");

        System.out.println("Testing connection...");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver loaded!");

            Connection conn = DriverManager.getConnection(url, username, password);
            System.out.println("Connected to database!");

            conn.close();
            System.out.println("Connection closed!");
            System.out.println("ALL GOOD - proceed with project!");

        } catch (ClassNotFoundException e) {
            System.err.println("Driver not found - check lib/ folder and settings.json");
        } catch (SQLException e) {
            System.err.println("SQL Error " + e.getErrorCode() + ": " + e.getMessage());
            if (e.getErrorCode() == 1045)
                System.err.println("Wrong password");
            if (e.getErrorCode() == 1049)
                System.err.println("Database doesn't exist yet, run: CREATE DATABASE petmoco_db;");
            if (e.getErrorCode() == 0)
                System.err.println("MySQL server not running");
        }
    }
}