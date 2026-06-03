
import utils.DatabaseConfig;

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

        // ── Step 1: Welcome banner ──────────────────────────

        // ── Step 2: Database connection ─────────────────────
        System.out.println("Connecting to database...");
        if (!DatabaseConfig.testConnection()) {
            System.err.println("\n[ERROR] Could not connect to the database.");
            System.err.println("Please check:");
            System.err.println("  - MySQL server is running");
            System.err.println("  - .env file contains correct DB_URL, DB_USER, DB_PASSWORD");
            System.err.println("  - Database 'petmoco_db' exists (run src/data/script.sql)");
            System.exit(1);
        }

        // ── Step 3: Authentication ──────────────────────────

        // ── Step 4: Main menu ───────────────────────────────

        // ── Step 5: Graceful exit ───────────────────────────
        System.out.println("\nThank you for using PetMoCo. See you next time!");
    }
}
