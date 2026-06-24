package menus;

import models.User;
import utils.ConsoleHelper;
import utils.InputValidator;

import java.util.Scanner;

/**
 * MainMenu — Top-level router shown after successful authentication.
 *
 * Receives the logged-in User and routes to sub-menus.
 * Pet and Appointment menus will be wired in here as they are built.
 */
public class MainMenu {

    private final Scanner scanner;
    private final User currentUser;

    public MainMenu(Scanner scanner, User currentUser) {
        this.scanner = scanner;
        this.currentUser = currentUser;
    }

    /**
     * Shows the main menu and loops until the user chooses to log out.
     */
    public void show() {
        while (true) {
            ConsoleHelper.printHeader("Main Menu");
            System.out.println("  Logged in as: " + currentUser.getUsername()
                    + " [" + currentUser.getRole() + "]");
            ConsoleHelper.printThinDivider();
            System.out.println("  1. Manage Pets");
            System.out.println("  2. Manage Appointments");
            System.out.println("  0. Log Out");
            ConsoleHelper.printThinDivider();

            int choice = InputValidator.readInt(scanner, "Choose an option", 0, 2);

            switch (choice) {
                case 1:
                    new PetMenu(scanner, currentUser).show();
                    break;
                case 2:
                    new AppointmentMenu(scanner, currentUser).show();
                    break;
                case 0:
                    ConsoleHelper.printInfo("Logging out...");
                    return;
            }
        }
    }
}
