package utils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * InputValidator — Provides typed, validated input readers for PetMoCo menus.
 *
 * Every method loops until the user provides a valid value.
 * Menus should never call scanner.nextLine() directly for user input;
 * always use these helpers to enforce correct format and non-empty checks.
 */
public class InputValidator {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    // ── String Readers ───────────────────────────────────────────────────────

    /**
     * Reads a non-empty string from the user.
     * Loops until the user enters at least one non-whitespace character.
     *
     * @param scanner the shared Scanner instance
     * @param prompt  the field label, e.g. "Username"
     * @return the trimmed, non-empty input string
     */
    public static String readNonEmptyString(Scanner scanner, String prompt) {
        while (true) {
            ConsoleHelper.printPrompt(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            ConsoleHelper.printError(prompt + " cannot be empty.");
        }
    }

    /**
     * Reads an optional string from the user.
     * Returns an empty string if the user presses Enter without typing anything.
     *
     * @param scanner the shared Scanner instance
     * @param prompt  the field label with hint, e.g. "Notes (optional)"
     * @return the trimmed input string, possibly empty
     */
    public static String readOptionalString(Scanner scanner, String prompt) {
        ConsoleHelper.printPrompt(prompt + " (optional, press Enter to skip)");
        return scanner.nextLine().trim();
    }

    // ── Integer Readers ──────────────────────────────────────────────────────

    /**
     * Reads an integer within an inclusive range [min, max].
     * Loops until the user enters a valid number in range.
     *
     * @param scanner the shared Scanner instance
     * @param prompt  the prompt label
     * @param min     the minimum acceptable value (inclusive)
     * @param max     the maximum acceptable value (inclusive)
     * @return the validated integer
     */
    public static int readInt(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            ConsoleHelper.printPrompt(prompt + " [" + min + "-" + max + "]");
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
                ConsoleHelper.printError("Please enter a number between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                ConsoleHelper.printError("Invalid input. Please enter a whole number.");
            }
        }
    }

    /**
     * Reads a positive integer (minimum value of 1).
     *
     * @param scanner the shared Scanner instance
     * @param prompt  the prompt label, e.g. "Pet age (years)"
     * @return a positive integer >= 1
     */
    public static int readPositiveInt(Scanner scanner, String prompt) {
        while (true) {
            ConsoleHelper.printPrompt(prompt);
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value > 0) {
                    return value;
                }
                ConsoleHelper.printError("Please enter a positive number (greater than 0).");
            } catch (NumberFormatException e) {
                ConsoleHelper.printError("Invalid input. Please enter a whole number.");
            }
        }
    }

    /**
     * Reads a non-negative integer (minimum value of 0).
     * Useful for pet age where 0 is valid (less than 1 year old).
     *
     * @param scanner the shared Scanner instance
     * @param prompt  the prompt label
     * @return a non-negative integer >= 0
     */
    public static int readNonNegativeInt(Scanner scanner, String prompt) {
        while (true) {
            ConsoleHelper.printPrompt(prompt);
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value >= 0) {
                    return value;
                }
                ConsoleHelper.printError("Please enter 0 or a positive number.");
            } catch (NumberFormatException e) {
                ConsoleHelper.printError("Invalid input. Please enter a whole number.");
            }
        }
    }

    // ── Date & Time Readers ──────────────────────────────────────────────────

    /**
     * Reads a date string in YYYY-MM-DD format.
     * Validates that the date is a real calendar date (not e.g. 2024-02-30).
     * Also rejects dates in the past.
     *
     * @param scanner the shared Scanner instance
     * @param prompt  the prompt label
     * @return a valid date string in YYYY-MM-DD format
     */
    public static String readDate(Scanner scanner, String prompt) {
        while (true) {
            ConsoleHelper.printPrompt(prompt + " (YYYY-MM-DD)");
            String input = scanner.nextLine().trim();
            try {
                LocalDate date = LocalDate.parse(input, DATE_FMT);
                if (date.isBefore(LocalDate.now())) {
                    ConsoleHelper.printError("Appointment date cannot be in the past.");
                    continue;
                }
                return date.format(DATE_FMT);
            } catch (DateTimeParseException e) {
                ConsoleHelper.printError("Invalid date. Use the format YYYY-MM-DD (e.g. 2025-08-15).");
            }
        }
    }

    /**
     * Reads a time string in HH:MM (24-hour) format.
     * Validates that the time represents a real clock time.
     *
     * @param scanner the shared Scanner instance
     * @param prompt  the prompt label
     * @return a valid time string in HH:MM format
     */
    public static String readTime(Scanner scanner, String prompt) {
        while (true) {
            ConsoleHelper.printPrompt(prompt + " (HH:MM, 24-hour)");
            String input = scanner.nextLine().trim();
            try {
                LocalTime.parse(input, TIME_FMT);
                return input;
            } catch (DateTimeParseException e) {
                ConsoleHelper.printError("Invalid time. Use 24-hour format HH:MM (e.g. 14:30).");
            }
        }
    }

    // ── Service Type Reader ──────────────────────────────────────────────────

    /**
     * Presents a numbered menu of available service types and returns
     * the user's selection as an uppercase string.
     *
     * Valid services are defined here to match AppointmentService.VALID_SERVICES.
     *
     * @param scanner the shared Scanner instance
     * @return one of "GROOMING", "SITTING", "WALKING"
     */
    public static String readServiceType(Scanner scanner) {
        String[] services = {"GROOMING", "SITTING", "WALKING"};

        System.out.println("  Select a service:");
        for (int i = 0; i < services.length; i++) {
            System.out.println("    " + (i + 1) + ". " + services[i]);
        }

        int choice = readInt(scanner, "Enter service number", 1, services.length);
        return services[choice - 1];
    }

    // ── Password Reader ──────────────────────────────────────────────────────

    /**
     * Reads a password from the user.
     *
     * Uses System.console().readPassword() when running in a real terminal —
     * this hides the typed characters. Falls back to plain Scanner input for
     * IDE environments where System.console() returns null.
     *
     * @param scanner the shared Scanner instance (used as fallback)
     * @param prompt  the prompt label, e.g. "Password"
     * @return the plain-text password string (not yet hashed)
     */
    public static String readPassword(Scanner scanner, String prompt) {
        java.io.Console console = System.console();

        while (true) {
            String password;
            if (console != null) {
                // Real terminal: characters are hidden as you type
                char[] chars = console.readPassword("%s: ", prompt);
                if (chars == null) {
                    throw new java.util.NoSuchElementException("Console input closed.");
                }
                password = new String(chars);
            } else {
                // IDE / redirected input: plain text fallback
                ConsoleHelper.printPrompt(prompt + " (visible — IDE mode)");
                password = scanner.nextLine();
            }

            if (password == null || password.trim().isEmpty()) {
                ConsoleHelper.printError("Password cannot be empty.");
                continue;
            }
            if (password.length() < 6) {
                ConsoleHelper.printError("Password must be at least 6 characters.");
                continue;
            }
            return password;
        }
    }

    /**
     * Reads a password with confirmation (prompts twice and checks they match).
     * Used during registration.
     *
     * @param scanner the shared Scanner instance (used as fallback)
     * @return the confirmed plain-text password string
     */
    public static String readPasswordWithConfirmation(Scanner scanner) {
        while (true) {
            String password = readPassword(scanner, "Password");
            String confirm  = readPassword(scanner, "Confirm password");
            if (password.equals(confirm)) {
                return password;
            }
            ConsoleHelper.printError("Passwords do not match. Please try again.");
        }
    }
}
