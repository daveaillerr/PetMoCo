package menus;

import models.User;
import services.UserService;
import utils.ConsoleHelper;
import utils.InputValidator;

import java.util.Scanner;

/**
 * AuthMenu — Login / Register / Exit screen.
 *
 * This is the first interactive screen after the app connects to the database.
 * Returns a logged-in User object on success, or null if the user chooses to exit.
 */
public class AuthMenu {

    private final Scanner scanner;
    private final UserService userService;

    public AuthMenu(Scanner scanner) {
        this.scanner = scanner;
        this.userService = new UserService();
    }

    /**
     * Shows the authentication menu and loops until the user either
     * logs in successfully, registers a new account, or chooses to exit.
     *
     * @return the authenticated User (Admin or PetOwner), or null on exit
     */
    public User show() {
        while (true) {
            ConsoleHelper.printHeader("Authentication");
            System.out.println("  1. Log In");
            System.out.println("  2. Register");
            System.out.println("  0. Exit");
            ConsoleHelper.printThinDivider();

            int choice = InputValidator.readInt(scanner, "Choose an option", 0, 2);

            switch (choice) {
                case 1:
                    User loggedIn = handleLogin();
                    if (loggedIn != null) {
                        return loggedIn;
                    }
                    break;
                case 2:
                    User registered = handleRegister();
                    if (registered != null) {
                        return registered;
                    }
                    break;
                case 0:
                    return null;
            }
        }
    }

    // ── Login ────────────────────────────────────────────────────────────────

    private User handleLogin() {
        ConsoleHelper.printHeader("Log In");

        String username = InputValidator.readNonEmptyString(scanner, "Username");
        String password = InputValidator.readPassword(scanner, "Password");

        User user = userService.login(username, password);

        if (user != null) {
            ConsoleHelper.printSuccess("Welcome back, " + user.getUsername() + "!");
        } else {
            ConsoleHelper.printError("Invalid username or password.");
        }

        ConsoleHelper.pause(scanner);
        return user;
    }

    // ── Register ─────────────────────────────────────────────────────────────

    private User handleRegister() {
        ConsoleHelper.printHeader("Register New Account");

        String username = InputValidator.readNonEmptyString(scanner, "Username");
        String password = InputValidator.readPasswordWithConfirmation(scanner);

        // Collect pet-owner profile details
        String fullName = InputValidator.readNonEmptyString(scanner, "Full name");
        String email    = InputValidator.readOptionalString(scanner, "Email address");
        String contact  = InputValidator.readOptionalString(scanner, "Contact number");
        String address  = InputValidator.readOptionalString(scanner, "Home address");

        boolean success = userService.registerPetOwner(
                username, password, fullName, email, contact, address);

        if (success) {
            ConsoleHelper.printSuccess("Account created! Logging you in...");
            ConsoleHelper.pause(scanner);

            // Auto-login after successful registration
            return userService.login(username, password);
        } else {
            ConsoleHelper.printError("Registration failed. Username may already be taken.");
            ConsoleHelper.pause(scanner);
            return null;
        }
    }
}
