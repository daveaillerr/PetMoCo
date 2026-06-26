package menus;

import dao.UserDAO;
import models.Appointment;
import models.Pet;
import models.User;
import models.PetOwner;
import services.AppointmentService;
import services.PetService;
import utils.ConsoleHelper;
import utils.InputValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * AppointmentMenu — Console UI for appointment management.
 *
 * Options: Book / View All / View by Pet / View Details / Cancel
 * Loops until the user picks 0 (Back).
 */
public class AppointmentMenu {

    private final Scanner scanner;
    private final User currentUser;
    private final AppointmentService appointmentService;
    private final PetService petService;
    private final int petOwnerId;

    public AppointmentMenu(Scanner scanner, User currentUser) {
        this.scanner = scanner;
        this.currentUser = currentUser;
        this.appointmentService = new AppointmentService();
        this.petService = new PetService();

        if (currentUser instanceof PetOwner) {
            UserDAO userDAO = new UserDAO();
            this.petOwnerId = userDAO.findPetOwnerIdByUserId(currentUser.getId());
        } else {
            this.petOwnerId = -1;
        }
    }

    /**
     * Shows the appointment management menu.
     */
    public void show() {
        while (true) {
            ConsoleHelper.printHeader("Manage Appointments");
            System.out.println("  1. Book an Appointment");
            System.out.println("  2. View All Appointments");
            System.out.println("  3. View by Pet");
            System.out.println("  4. View Appointment Details");
            System.out.println("  5. Cancel an Appointment");
            System.out.println("  0. Back");
            ConsoleHelper.printThinDivider();

            int choice = InputValidator.readInt(scanner, "Choose an option", 0, 5);

            switch (choice) {
                case 1: handleBook();       break;
                case 2: handleViewAll();    break;
                case 3: handleViewByPet();  break;
                case 4: handleDetails();    break;
                case 5: handleCancel();     break;
                case 0: return;
            }
        }
    }

    // ── 1. Book Appointment ──────────────────────────────────────────────────

    private void handleBook() {
        ConsoleHelper.printHeader("Book an Appointment");

        if (petOwnerId == -1) {
            ConsoleHelper.printError("Admin accounts cannot book appointments.");
            ConsoleHelper.pause(scanner);
            return;
        }

        // Step 1: Select a pet
        List<Pet> pets = petService.getPetsByOwner(petOwnerId);
        if (pets.isEmpty()) {
            ConsoleHelper.printError("You have no registered pets. Register a pet first.");
            ConsoleHelper.pause(scanner);
            return;
        }

        System.out.println("  Your pets:");
        for (Pet pet : pets) {
            String[] typeInfo = petService.getPetTypeDetails(pet.getTypeId());
            String type = (typeInfo != null) ? typeInfo[0] : "?";
            System.out.println("    ID " + pet.getId() + " - " + pet.getName() + " (" + type + ")");
        }
        ConsoleHelper.printThinDivider();

        int petId = InputValidator.readPositiveInt(scanner, "Enter Pet ID");

        // Verify pet belongs to this owner
        Pet selectedPet = petService.getPetById(petId);
        if (selectedPet == null || selectedPet.getOwnerId() != petOwnerId) {
            ConsoleHelper.printError("Pet not found or doesn't belong to you.");
            ConsoleHelper.pause(scanner);
            return;
        }

        // Get pet size for pricing display
        String petSize = appointmentService.getPetSize(petId);
        if (petSize == null) {
            ConsoleHelper.printError("Could not determine pet size for pricing.");
            ConsoleHelper.pause(scanner);
            return;
        }

        // Step 2: Select services
        List<String[]> availableServices = appointmentService.getAvailableServices();
        if (availableServices.isEmpty()) {
            ConsoleHelper.printError("No services are available. Contact an admin.");
            ConsoleHelper.pause(scanner);
            return;
        }

        System.out.println("\n  Available services (prices for " + petSize + " size):");
        ConsoleHelper.printThinDivider();
        System.out.printf("  %-4s %-15s %-35s %8s %5s%n",
                "#", "Service", "Description", "Price", "Min");
        ConsoleHelper.printThinDivider();

        for (String[] svc : availableServices) {
            int svcId = Integer.parseInt(svc[0]);
            float[] pricing = appointmentService.getPrice(svcId, petSize);
            String priceStr = (pricing != null) ? String.format("%.2f", pricing[1]) : "N/A";
            System.out.printf("  %-4s %-15s %-35s %8s %5s%n",
                    svc[0], svc[1], svc[2], priceStr, svc[3]);
        }
        ConsoleHelper.printThinDivider();

        // Collect service selections
        List<Integer> selectedServiceIds = new ArrayList<>();
        ConsoleHelper.printInfo("Enter service numbers one at a time. Type 0 when done.");

        while (true) {
            int svcChoice = InputValidator.readNonNegativeInt(scanner, "Add service (0 to finish)");
            if (svcChoice == 0) {
                break;
            }
            // Validate service exists
            boolean valid = false;
            for (String[] svc : availableServices) {
                if (Integer.parseInt(svc[0]) == svcChoice) {
                    valid = true;
                    break;
                }
            }
            if (!valid) {
                ConsoleHelper.printError("Invalid service number.");
                continue;
            }
            if (selectedServiceIds.contains(svcChoice)) {
                ConsoleHelper.printInfo("Service already selected.");
                continue;
            }
            selectedServiceIds.add(svcChoice);
            ConsoleHelper.printSuccess("Service added.");
        }

        if (selectedServiceIds.isEmpty()) {
            ConsoleHelper.printError("No services selected. Booking cancelled.");
            ConsoleHelper.pause(scanner);
            return;
        }

        // Step 3: Date and time
        String date = InputValidator.readDate(scanner, "Appointment date");
        String time = InputValidator.readTime(scanner, "Appointment time");

        // Step 4: Show summary and confirm
        ConsoleHelper.printThinDivider();
        System.out.println("  Booking Summary:");
        System.out.println("  Pet       : " + selectedPet.getName());
        System.out.println("  Date      : " + date);
        System.out.println("  Time      : " + time);
        System.out.println("  Services  :");

        float previewTotal = 0;
        for (int svcId : selectedServiceIds) {
            float[] pricing = appointmentService.getPrice(svcId, petSize);
            String svcName = "?";
            for (String[] svc : availableServices) {
                if (Integer.parseInt(svc[0]) == svcId) {
                    svcName = svc[1];
                    break;
                }
            }
            float price = (pricing != null) ? pricing[1] : 0;
            System.out.printf("    - %-15s  %.2f%n", svcName, price);
            previewTotal += price;
        }
        System.out.printf("  Total     : %.2f%n", previewTotal);
        ConsoleHelper.printThinDivider();

        String confirm = InputValidator.readNonEmptyString(scanner, "Confirm booking? (YES/NO)");
        if (!confirm.equalsIgnoreCase("YES")) {
            ConsoleHelper.printInfo("Booking cancelled.");
            ConsoleHelper.pause(scanner);
            return;
        }

        // Step 5: Book
        int appointmentId = appointmentService.bookAppointment(
                petId, petOwnerId, date, time, selectedServiceIds);

        if (appointmentId != -1) {
            ConsoleHelper.printSuccess("Appointment booked! ID: " + appointmentId);
        } else {
            ConsoleHelper.printError("Failed to book appointment.");
        }
        ConsoleHelper.pause(scanner);
    }

    // ── 2. View All ──────────────────────────────────────────────────────────

    private void handleViewAll() {
        ConsoleHelper.printHeader("All Appointments");

        List<Appointment> appointments = (petOwnerId != -1)
                ? appointmentService.getAppointmentsByOwner(petOwnerId)
                : appointmentService.getAllAppointments();

        if (appointments.isEmpty()) {
            ConsoleHelper.printInfo("No appointments found.");
        } else {
            printAppointmentTable(appointments);
        }
        ConsoleHelper.pause(scanner);
    }

    // ── 3. View by Pet ───────────────────────────────────────────────────────

    private void handleViewByPet() {
        ConsoleHelper.printHeader("Appointments by Pet");

        int petId = InputValidator.readPositiveInt(scanner, "Enter Pet ID");
        List<Appointment> appointments = appointmentService.getAppointmentsByPet(petId);

        if (appointments.isEmpty()) {
            ConsoleHelper.printInfo("No appointments found for Pet ID " + petId + ".");
        } else {
            printAppointmentTable(appointments);
        }
        ConsoleHelper.pause(scanner);
    }

    // ── 4. View Details ──────────────────────────────────────────────────────

    private void handleDetails() {
        ConsoleHelper.printHeader("Appointment Details");

        int apptId = InputValidator.readPositiveInt(scanner, "Enter Appointment ID");
        Appointment appt = appointmentService.getAppointmentById(apptId);

        if (appt == null) {
            ConsoleHelper.printError("No appointment found with ID " + apptId + ".");
        } else {
            printAppointmentDetails(appt);
        }
        ConsoleHelper.pause(scanner);
    }

    // ── 5. Cancel ────────────────────────────────────────────────────────────

    private void handleCancel() {
        ConsoleHelper.printHeader("Cancel an Appointment");

        int apptId = InputValidator.readPositiveInt(scanner, "Enter Appointment ID to cancel");
        Appointment appt = appointmentService.getAppointmentById(apptId);

        if (appt == null) {
            ConsoleHelper.printError("No appointment found with ID " + apptId + ".");
            ConsoleHelper.pause(scanner);
            return;
        }

        printAppointmentDetails(appt);
        ConsoleHelper.printThinDivider();

        if ("CANCELLED".equalsIgnoreCase(appt.getStatus())) {
            ConsoleHelper.printError("This appointment is already cancelled.");
            ConsoleHelper.pause(scanner);
            return;
        }

        String confirm = InputValidator.readNonEmptyString(scanner, "Type YES to confirm cancellation");
        if (confirm.equalsIgnoreCase("YES")) {
            boolean success = appointmentService.cancelAppointment(apptId);
            if (success) {
                ConsoleHelper.printSuccess("Appointment #" + apptId + " cancelled.");
            } else {
                ConsoleHelper.printError("Failed to cancel appointment.");
            }
        } else {
            ConsoleHelper.printInfo("Cancellation aborted.");
        }
        ConsoleHelper.pause(scanner);
    }

    // ── Display Helpers ──────────────────────────────────────────────────────

    private void printAppointmentTable(List<Appointment> appointments) {
        System.out.printf("  %-5s %-12s %-12s %-6s %-12s %10s%n",
                "ID", "Pet", "Date", "Time", "Status", "Total");
        ConsoleHelper.printThinDivider();

        for (Appointment a : appointments) {
            System.out.printf("  %-5d %-12s %-12s %-6s %-12s %10.2f%n",
                    a.getAppointmentId(),
                    truncate(a.getPetName(), 12),
                    a.getDate(),
                    a.getTime(),
                    a.getStatus(),
                    a.getTotalAmount());
        }
    }

    private void printAppointmentDetails(Appointment appt) {
        System.out.println("  Appointment ID : " + appt.getAppointmentId());
        System.out.println("  Pet            : " + appt.getPetName() + " (ID: " + appt.getPetId() + ")");
        System.out.println("  Date           : " + appt.getDate());
        System.out.println("  Time           : " + appt.getTime());
        System.out.println("  Status         : " + appt.getStatus());
        System.out.printf("  Total Amount   : %.2f%n", appt.getTotalAmount());

        // Show linked services
        List<String[]> services = appointmentService.getAppointmentServices(appt.getAppointmentId());
        if (!services.isEmpty()) {
            System.out.println("  Services       :");
            for (String[] svc : services) {
                System.out.println("    - " + svc[0] + "  (" + svc[1] + ")");
            }
        }
    }

    private String truncate(String text, int maxLen) {
        if (text == null) return "?";
        return text.length() > maxLen ? text.substring(0, maxLen - 1) + "." : text;
    }
}
