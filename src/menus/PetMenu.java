package menus;

import dao.UserDAO;
import models.Pet;
import models.User;
import models.PetOwner;
import services.PetService;
import utils.ConsoleHelper;
import utils.InputValidator;

import java.util.List;
import java.util.Scanner;

/**
 * PetMenu — Console UI for pet management.
 *
 * Options: Register / View All / Details / Update / Remove
 * Loops until the user picks 0 (Back).
 */
public class PetMenu {

    private static final String[] SIZE_OPTIONS = { "SMALL", "MEDIUM", "LARGE", "EXTRA LARGE" };

    private final Scanner scanner;
    private final User currentUser;
    private final PetService petService;
    private final int petOwnerId;

    public PetMenu(Scanner scanner, User currentUser) {
        this.scanner = scanner;
        this.currentUser = currentUser;
        this.petService = new PetService();

        // Resolve the pet_owner_id from the users.user_id
        if (currentUser instanceof PetOwner) {
            UserDAO userDAO = new UserDAO();
            this.petOwnerId = userDAO.findPetOwnerIdByUserId(currentUser.getId());
        } else {
            // Admins can view all pets but don't own pets themselves
            this.petOwnerId = -1;
        }
    }

    /**
     * Shows the pet management menu and loops until the user goes back.
     */
    public void show() {
        while (true) {
            ConsoleHelper.printHeader("Manage Pets");
            System.out.println("  1. Register a Pet");
            System.out.println("  2. View All Pets");
            System.out.println("  3. View Pet Details");
            System.out.println("  4. Update Pet Info");
            System.out.println("  5. Remove a Pet");
            System.out.println("  0. Back");
            ConsoleHelper.printThinDivider();

            int choice = InputValidator.readInt(scanner, "Choose an option", 0, 5);

            switch (choice) {
                case 1:
                    handleRegister();
                    break;
                case 2:
                    handleViewAll();
                    break;
                case 3:
                    handleDetails();
                    break;
                case 4:
                    handleUpdate();
                    break;
                case 5:
                    handleRemove();
                    break;
                case 0:
                    return;
            }
        }
    }

    // ── 1. Register Pet ──────────────────────────────────────────────────────

    private void handleRegister() {
        ConsoleHelper.printHeader("Register a New Pet");

        if (petOwnerId == -1) {
            ConsoleHelper.printError("Admin accounts cannot register pets. Log in as a pet owner.");
            ConsoleHelper.pause(scanner);
            return;
        }

        String name = InputValidator.readNonEmptyString(scanner, "Pet name");
        String type = InputValidator.readNonEmptyString(scanner, "Pet type (Dog, Cat)");
        String breed = InputValidator.readNonEmptyString(scanner, "Breed");
        String size = readSize();
        String notes = InputValidator.readOptionalString(scanner, "Notes");

        boolean success = petService.registerPet(petOwnerId, name, type, breed, size, notes);

        if (success) {
            ConsoleHelper.printSuccess("Pet \"" + name + "\" registered successfully!");
        } else {
            ConsoleHelper.printError("Failed to register pet.");
        }
        ConsoleHelper.pause(scanner);
    }

    // ── 2. View All Pets ─────────────────────────────────────────────────────

    private void handleViewAll() {
        ConsoleHelper.printHeader("Your Pets");

        List<Pet> pets = (petOwnerId != -1)
                ? petService.getPetsByOwner(petOwnerId)
                : petService.getAllPets();

        if (pets.isEmpty()) {
            ConsoleHelper.printInfo("No pets found. Register one first!");
        } else {
            printPetTable(pets);
        }
        ConsoleHelper.pause(scanner);
    }

    // ── 3. View Details ──────────────────────────────────────────────────────

    private void handleDetails() {
        ConsoleHelper.printHeader("Pet Details");

        int petId = InputValidator.readPositiveInt(scanner, "Enter Pet ID");
        Pet pet = petService.getPetById(petId);

        if (pet == null || (petOwnerId != -1 && pet.getOwnerId() != petOwnerId)) {
            ConsoleHelper.printError("No pet found with ID " + petId + ".");
        } else {
            printPetDetails(pet);
        }
        ConsoleHelper.pause(scanner);
    }

    // ── 4. Update Pet ────────────────────────────────────────────────────────

    private void handleUpdate() {
        ConsoleHelper.printHeader("Update Pet Info");

        int petId = InputValidator.readPositiveInt(scanner, "Enter Pet ID to update");
        Pet pet = petService.getPetById(petId);

        if (pet == null || (petOwnerId != -1 && pet.getOwnerId() != petOwnerId)) {
            ConsoleHelper.printError("No pet found with ID " + petId + ".");
            ConsoleHelper.pause(scanner);
            return;
        }

        // Show current details before editing
        printPetDetails(pet);
        ConsoleHelper.printThinDivider();
        ConsoleHelper.printInfo("Leave a field blank to keep the current value.");

        // Name
        ConsoleHelper.printPrompt("New name [" + pet.getName() + "]");
        String newName = scanner.nextLine().trim();
        if (!newName.isEmpty()) {
            pet.setName(newName);
        }

        // Notes
        ConsoleHelper.printPrompt("New notes [" + (pet.getNotes() != null ? pet.getNotes() : "") + "]");
        String newNotes = scanner.nextLine().trim();
        if (!newNotes.isEmpty()) {
            pet.setNotes(newNotes);
        }

        boolean success = petService.updatePet(pet);
        if (success) {
            ConsoleHelper.printSuccess("Pet updated successfully!");
        } else {
            ConsoleHelper.printError("Failed to update pet.");
        }
        ConsoleHelper.pause(scanner);
    }

    // ── 5. Remove Pet ────────────────────────────────────────────────────────

    private void handleRemove() {
        ConsoleHelper.printHeader("Remove a Pet");

        int petId = InputValidator.readPositiveInt(scanner, "Enter Pet ID to remove");
        Pet pet = petService.getPetById(petId);

        if (pet == null || (petOwnerId != -1 && pet.getOwnerId() != petOwnerId)) {
            ConsoleHelper.printError("No pet found with ID " + petId + ".");
            ConsoleHelper.pause(scanner);
            return;
        }

        printPetDetails(pet);
        ConsoleHelper.printThinDivider();
        ConsoleHelper.printInfo("This will also delete all appointments for this pet.");

        String confirm = InputValidator.readNonEmptyString(scanner, "Type YES to confirm deletion");
        if (confirm.equalsIgnoreCase("YES")) {
            boolean success = petService.deletePet(petId);
            if (success) {
                ConsoleHelper.printSuccess("Pet \"" + pet.getName() + "\" removed.");
            } else {
                ConsoleHelper.printError("Failed to remove pet.");
            }
        } else {
            ConsoleHelper.printInfo("Deletion cancelled.");
        }
        ConsoleHelper.pause(scanner);
    }

    // ── Display Helpers ──────────────────────────────────────────────────────

    /**
     * Prints a table of pets with their type details.
     */
    private void printPetTable(List<Pet> pets) {
        System.out.printf("  %-5s %-15s %-10s %-15s %-10s%n",
                "ID", "Name", "Type", "Breed", "Size");
        ConsoleHelper.printThinDivider();

        for (Pet pet : pets) {
            String[] typeInfo = petService.getPetTypeDetails(pet.getTypeId());
            String type = (typeInfo != null) ? typeInfo[0] : "?";
            String breed = (typeInfo != null) ? typeInfo[1] : "?";
            String size = (typeInfo != null) ? typeInfo[2] : "?";

            System.out.printf("  %-5d %-15s %-10s %-15s %-10s%n",
                    pet.getId(), pet.getName(), type, breed, size);
        }
    }

    /**
     * Prints full details of a single pet.
     */
    private void printPetDetails(Pet pet) {
        String[] typeInfo = petService.getPetTypeDetails(pet.getTypeId());
        String type = (typeInfo != null) ? typeInfo[0] : "?";
        String breed = (typeInfo != null) ? typeInfo[1] : "?";
        String size = (typeInfo != null) ? typeInfo[2] : "?";

        System.out.println("  Pet ID    : " + pet.getId());
        System.out.println("  Name      : " + pet.getName());
        System.out.println("  Type      : " + type);
        System.out.println("  Breed     : " + breed);
        System.out.println("  Size      : " + size);
        System.out.println("  Notes     : " + (pet.getNotes() != null ? pet.getNotes() : "-"));
    }

    /**
     * Presents the size options as a numbered list and returns the selection.
     */
    private String readSize() {
        System.out.println("  Select pet size:");
        for (int i = 0; i < SIZE_OPTIONS.length; i++) {
            System.out.println("    " + (i + 1) + ". " + SIZE_OPTIONS[i]);
        }
        int choice = InputValidator.readInt(scanner, "Enter size number", 1, SIZE_OPTIONS.length);
        return SIZE_OPTIONS[choice - 1];
    }
}
