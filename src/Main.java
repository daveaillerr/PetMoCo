
import menus.AuthMenu;
import menus.MainMenu;
import models.User;
import utils.ConsoleHelper;
import utils.DatabaseConfig;

import java.util.Scanner;

/**
 * Main — Application entry point for PetMoCo.
 *
 * Flow:
 * 1. Print welcome banner
 * 2. Test database connection
 * 3. Show authentication menu (login / register)
 * 4. Launch main menu loop
 * 5. On exit: close DB connection
 */
public class Main {

    public static void main(String[] args) {

        // Close the DB connection cleanly on any exit
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            DatabaseConfig.closeConnection();
            System.out.println("Application shutdown complete.");
        }));

        Scanner scanner = new Scanner(System.in);

        // ── Step 1: Welcome banner ──────────────────────────
        ConsoleHelper.printBanner();

        // ── Step 2: Database connection ─────────────────────
        System.out.println("Connecting to database...");
        if (!DatabaseConfig.testConnection()) {
            ConsoleHelper.printError("Could not connect to the database.");
            System.err.println("Please check:");
            System.err.println("  - MySQL server is running");
            System.err.println("  - .env file contains correct DB_URL, DB_USER, DB_PASSWORD");
            System.err.println("  - Database 'petmoco_db' exists (run src/data/script.sql)");
            System.exit(1);
        }
        ConsoleHelper.printSuccess("Database connected.");

        // ── Step 3: Authentication ──────────────────────────
        AuthMenu authMenu = new AuthMenu(scanner);
        User currentUser = authMenu.show();

        if (currentUser == null) {
            // User chose "Exit" from the auth menu
            ConsoleHelper.printInfo("Goodbye!");
            scanner.close();
            return;
        }

        // ── Step 4: Main menu ───────────────────────────────
        MainMenu mainMenu = new MainMenu(scanner, currentUser);
        mainMenu.show();

        // ── Step 5: Graceful exit ───────────────────────────
        scanner.close();
        System.out.println("\nThank you for using PetMoCo. See you next time!");
    }
}
