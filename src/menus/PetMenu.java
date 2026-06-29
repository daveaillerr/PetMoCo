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

        if (currentUser instanceof PetOwner) {
            UserDAO userDAO = new UserDAO();
            this.petOwnerId = userDAO.findPetOwnerIdByUserId(currentUser.getId());
        } else {
            this.petOwnerId = -1;
        }
    }

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

        String name = toTitleCase(InputValidator.readNonEmptyString(scanner, "Pet name"));
        String type = toTitleCase(InputValidator.readNonEmptyString(scanner, "Pet type (Dog, Cat)"));
        String breed = toTitleCase(InputValidator.readNonEmptyString(scanner, "Breed"));
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

        // Get current type info
        String[] typeInfo = petService.getPetTypeDetails(pet.getTypeId());
        String currentType = (typeInfo != null) ? typeInfo[0] : "?";
        String currentBreed = (typeInfo != null) ? typeInfo[1] : "?";
        String currentSize = (typeInfo != null) ? typeInfo[2] : "?";

        // Name
        ConsoleHelper.printPrompt("New name [" + pet.getName() + "]");
        String newName = scanner.nextLine().trim();
        if (!newName.isEmpty()) {
            pet.setName(toTitleCase(newName));
        }

        // Type
        ConsoleHelper.printPrompt("New type [" + currentType + "]");
        String newType = scanner.nextLine().trim();
        if (newType.isEmpty()) {
            newType = currentType;
        } else {
            newType = toTitleCase(newType);
        }

        // Breed
        ConsoleHelper.printPrompt("New breed [" + currentBreed + "]");
        String newBreed = scanner.nextLine().trim();
        if (newBreed.isEmpty()) {
            newBreed = currentBreed;
        } else {
            newBreed = toTitleCase(newBreed);
        }

        // Size
        ConsoleHelper.printPrompt("New size [" + currentSize + "] (leave blank to keep)");
        System.out.println("    1. SMALL  2. MEDIUM  3. LARGE  4. EXTRA LARGE");
        String sizeInput = scanner.nextLine().trim();
        String newSize = currentSize;
        if (!sizeInput.isEmpty()) {
            try {
                int sizeChoice = Integer.parseInt(sizeInput);
                if (sizeChoice >= 1 && sizeChoice <= 4) {
                    newSize = SIZE_OPTIONS[sizeChoice - 1];
                }
            } catch (NumberFormatException e) {
                // Keep current size
            }
        }

        // Notes
        ConsoleHelper.printPrompt("New notes [" + (pet.getNotes() != null ? pet.getNotes() : "") + "]");
        String newNotes = scanner.nextLine().trim();
        if (!newNotes.isEmpty()) {
            pet.setNotes(newNotes);
        }

        // Update pet type (get or create the new combination)
        boolean success = petService.updatePetFull(pet, newType, newBreed, newSize);
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

    private String readSize() {
        System.out.println("  Select pet size:");
        for (int i = 0; i < SIZE_OPTIONS.length; i++) {
            System.out.println("    " + (i + 1) + ". " + SIZE_OPTIONS[i]);
        }
        int choice = InputValidator.readInt(scanner, "Enter size number", 1, SIZE_OPTIONS.length);
        return SIZE_OPTIONS[choice - 1];
    }

    /**
     * Converts a string to Title Case (first letter uppercase, rest lowercase).
     * Example: "golden retriever" → "Golden Retriever"
     */
    private String toTitleCase(String input) {
        if (input == null || input.isEmpty())
            return input;
        String[] words = input.toLowerCase().split("\\s+");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (word.length() > 0) {
                result.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1))
                        .append(" ");
            }
        }
        return result.toString().trim();
    }
}
