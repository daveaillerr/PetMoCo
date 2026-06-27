package utils;

import java.util.Scanner;

/**
 * ConsoleHelper — Centralizes all console output for PetMoCo.
 *
 * All user-visible status/feedback strings go through here.
 * Never use raw System.out.println for status/error messages in menus.
 *
 * ANSI color codes are used for terminals that support them.
 * They are harmless (invisible) in environments that do not.
 */
public class ConsoleHelper {

    // ── ANSI Color Codes ────────────────────────────────────────────────────
    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";
    private static final String GREEN = "\u001B[32m";
    private static final String RED = "\u001B[31m";
    private static final String YELLOW = "\u001B[33m";
    private static final String CYAN = "\u001B[36m";
    private static final String WHITE = "\u001B[97m";

    // ── Width constant ───────────────────────────────────────────────────────
    private static final int WIDTH = 54;
    private static final String LINE = "=".repeat(WIDTH);
    private static final String THIN = "-".repeat(WIDTH);

    // ── Banner ───────────────────────────────────────────────────────────────

    /**
     * Prints the PetMoCo welcome banner at application startup.
     */
    public static void printBanner() {
        System.out.println(CYAN + BOLD);
        System.out.println(LINE);
        System.out.println("      █████ █████ █████ █   █  ███   ███   ███ ");
        System.out.println("      █   █ █       █   ██ ██ █   █ █     █   █");
        System.out.println("      █████ ████    █   █ █ █ █   █ █     █   █");
        System.out.println("      █     █       █   █   █ █   █ █     █   █");
        System.out.println("      █     █████   █   █   █  ███   ███   ███ ");
        System.out.println();
        System.out.println(YELLOW + center("~ Book a Paw-fect Day~") + RESET);
        System.out.println(CYAN + BOLD + LINE);
        System.out.println(RESET);
    }

    // ── Section Headers ──────────────────────────────────────────────────────

    /**
     * Prints a titled section header.
     *
     * @param title the section title to display
     */
    public static void printHeader(String title) {
        System.out.println();
        System.out.println(CYAN + LINE + RESET);
        System.out.println(CYAN + BOLD + center(title) + RESET);
        System.out.println(CYAN + LINE + RESET);
    }

    // ── Dividers ─────────────────────────────────────────────────────────────

    /** Prints a thick divider line. */
    public static void printDivider() {
        System.out.println(CYAN + LINE + RESET);
    }

    /** Prints a thin divider line. */
    public static void printThinDivider() {
        System.out.println(THIN);
    }

    // ── Status Messages ──────────────────────────────────────────────────────

    /**
     * Prints a green success message.
     *
     * @param message the success text to display
     */
    public static void printSuccess(String message) {
        System.out.println(GREEN + BOLD + "[✔] " + message + RESET);
    }

    /**
     * Prints a red error message.
     *
     * @param message the error text to display
     */
    public static void printError(String message) {
        System.out.println(RED + BOLD + "[✘] " + message + RESET);
    }

    /**
     * Prints a yellow informational/warning message.
     *
     * @param message the info text to display
     */
    public static void printInfo(String message) {
        System.out.println(YELLOW + "[!] " + message + RESET);
    }

    /**
     * Prints a plain prompt label (no newline) for inline Scanner input.
     *
     * @param prompt the prompt label, e.g. "Enter username"
     */
    public static void printPrompt(String prompt) {
        System.out.print(WHITE + prompt + ": " + RESET);
    }

    // ── Pause ────────────────────────────────────────────────────────────────

    /**
     * Waits for the user to press Enter before continuing.
     * Used after displaying details or a result so the user can read it.
     *
     * @param scanner the shared Scanner instance
     */
    public static void pause(Scanner scanner) {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    /**
     * Centers a string within the console WIDTH.
     *
     * @param text the text to center
     * @return the padded, centered string
     */
    private static String center(String text) {
        // Strip ANSI codes for length calculation
        int visibleLength = text.replaceAll("\u001B\\[[;\\d]*m", "").length();
        int padding = Math.max(0, (WIDTH - visibleLength) / 2);
        return " ".repeat(padding) + text;
    }

    /** Clears the screen (works in most real terminals). */
    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
