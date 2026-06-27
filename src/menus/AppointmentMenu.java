
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

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

    public void show() {
        while (true) {
            ConsoleHelper.printHeader("Manage Appointments");
            System.out.println("  1. Book an Appointment");
            System.out.println("  2. View All Appointments");
            System.out.println("  3. View by Pet");
            System.out.println("  4. View Appointment Details");
            System.out.println("  5. Cancel an Appointment");
            System.out.println("  6. Search Appointment");
            if (petOwnerId == -1) {
                System.out.println("  7. Generate Report");
            }
            System.out.println("  0. Back");
            ConsoleHelper.printThinDivider();

            int choice = InputValidator.readInt(scanner, "Choose an option", 0, 7);

            switch (choice) {
                case 1: handleBook();       break;
                case 2: handleViewAll();    break;
                case 3: handleViewByPet();  break;
                case 4: handleDetails();    break;
                case 5: handleCancel();     break;
                case 6: handleSearch();     break;
                case 7: handleReport();     break;
                case 0: return;
            }
        }
    }

    private void handleBook() {
        ConsoleHelper.printHeader("Book an Appointment");

        if (petOwnerId == -1) {
            ConsoleHelper.printError("Admin accounts cannot book appointments.");
            ConsoleHelper.pause(scanner);
            return;
        }

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

        Pet selectedPet = petService.getPetById(petId);
        if (selectedPet == null || selectedPet.getOwnerId() != petOwnerId) {
            ConsoleHelper.printError("Pet not found or doesn't belong to you.");
            ConsoleHelper.pause(scanner);
            return;
        }

        String petSize = appointmentService.getPetSize(petId);
        if (petSize == null) {
            ConsoleHelper.printError("Could not determine pet size for pricing.");
            ConsoleHelper.pause(scanner);
            return;
        }

        List<String[]> availableServices = appointmentService.getAvailableServices();
        if (availableServices.isEmpty()) {
            ConsoleHelper.printError("No services are available. Contact an admin.");
            ConsoleHelper.pause(scanner);
            return;
        }

        System.out.println("\n  Services (prices for " + petSize + " size):");
        ConsoleHelper.printThinDivider();
        System.out.printf("  %-3s %-13s %-35s %8s %4s%n",
                "#", "Service", "Description", "Price", "Min");
        ConsoleHelper.printThinDivider();

        for (String[] svc : availableServices) {
            int svcId = Integer.parseInt(svc[0]);
            float[] pricing = appointmentService.getPrice(svcId, petSize);
            String priceStr = (pricing != null) ? String.format("%.2f", pricing[1]) : "N/A";
            System.out.printf("  %-3s %-13s %-35s %8s %4s%n",
                    svc[0], svc[1], svc[2], priceStr, svc[3]);
        }
        ConsoleHelper.printThinDivider();

        List<Integer> selectedServiceIds = new ArrayList<>();
        ConsoleHelper.printInfo("Enter service numbers one at a time. Type 0 when done.");

        while (true) {
            int svcChoice = InputValidator.readNonNegativeInt(scanner, "Add service (0 to finish)");
            if (svcChoice == 0) {
                break;
            }
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

        String date = InputValidator.readDate(scanner, "Appointment date");
        String time = InputValidator.readTime(scanner, "Appointment time");

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

        int appointmentId = appointmentService.bookAppointment(
                petId, petOwnerId, date, time, selectedServiceIds);

        if (appointmentId != -1) {
            ConsoleHelper.printSuccess("Appointment booked! ID: " + appointmentId);
        } else {
            ConsoleHelper.printError("Failed to book appointment.");
        }
        ConsoleHelper.pause(scanner);
    }

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

    private void handleViewByPet() {
        ConsoleHelper.printHeader("Appointments by Pet");

        int petId = InputValidator.readPositiveInt(scanner, "Enter Pet ID");

        Pet pet = petService.getPetById(petId);
        if (pet == null || (petOwnerId != -1 && pet.getOwnerId() != petOwnerId)) {
            ConsoleHelper.printError("Pet not found or does not belong to you.");
            ConsoleHelper.pause(scanner);
            return;
        }

        List<Appointment> appointments = appointmentService.getAppointmentsByPet(petId);

        if (appointments.isEmpty()) {
            ConsoleHelper.printInfo("No appointments found for Pet ID " + petId + ".");
        } else {
            printAppointmentTable(appointments);
        }
        ConsoleHelper.pause(scanner);
    }

    private void handleDetails() {
        ConsoleHelper.printHeader("Appointment Details");

        int apptId = InputValidator.readPositiveInt(scanner, "Enter Appointment ID");
        Appointment appt = appointmentService.getAppointmentById(apptId);

        if (appt == null || (petOwnerId != -1 && appt.getPetOwnerId() != petOwnerId)) {
            ConsoleHelper.printError("No appointment found with ID " + apptId + ".");
        } else {
            printAppointmentDetails(appt);
        }
        ConsoleHelper.pause(scanner);
    }

    private void handleCancel() {
        ConsoleHelper.printHeader("Cancel an Appointment");

        int apptId = InputValidator.readPositiveInt(scanner, "Enter Appointment ID to cancel");
        Appointment appt = appointmentService.getAppointmentById(apptId);

        if (appt == null || (petOwnerId != -1 && appt.getPetOwnerId() != petOwnerId)) {
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

    private void handleSearch() {
        ConsoleHelper.printHeader("Search Appointments");

        System.out.println("  Search by:");
        System.out.println("    1. Date");
        System.out.println("    2. Status");
        ConsoleHelper.printThinDivider();

        int searchChoice = InputValidator.readInt(scanner, "Choose search type", 1, 2);

        List<Appointment> results;

        if (searchChoice == 1) {
            String date = InputValidator.readNonEmptyString(scanner, "Enter date (YYYY-MM-DD)");
            results = appointmentService.searchAppointmentsByDate(date, petOwnerId);
        } else {
            System.out.println("  Status options: PENDING, APPROVED, CANCELLED, DONE");
            String status = InputValidator.readNonEmptyString(scanner, "Enter status");
            results = appointmentService.searchAppointmentsByStatus(status, petOwnerId);
        }

        if (results.isEmpty()) {
            ConsoleHelper.printInfo("No appointments found matching your search.");
        } else {
            ConsoleHelper.printSuccess(results.size() + " appointment(s) found:");
            printAppointmentTable(results);
        }
        ConsoleHelper.pause(scanner);
    }

    private void handleReport() {
        if (petOwnerId != -1) {
            ConsoleHelper.printError("Only admins can generate reports.");
            ConsoleHelper.pause(scanner);
            return;
        }

        ConsoleHelper.printHeader("Appointment Reports");

        System.out.println("  Report Type:");
        System.out.println("    1. Summary by Status");
        System.out.println("    2. Revenue Summary");
        System.out.println("    3. Appointments per Pet");
        ConsoleHelper.printThinDivider();

        int reportChoice = InputValidator.readInt(scanner, "Choose report type", 1, 3);

        List<Appointment> appointments = appointmentService.getAllAppointments();

        if (appointments.isEmpty()) {
            ConsoleHelper.printInfo("No appointment data available for report.");
            ConsoleHelper.pause(scanner);
            return;
        }

        switch (reportChoice) {
            case 1: reportByStatus(appointments);  break;
            case 2: reportRevenue(appointments);   break;
            case 3: reportByPet(appointments);     break;
        }
        ConsoleHelper.pause(scanner);
    }

    private void reportByStatus(List<Appointment> appointments) {
        ConsoleHelper.printHeader("Report: Appointments by Status");

        int pending = 0, approved = 0, cancelled = 0, done = 0;

        for (Appointment a : appointments) {
            switch (a.getStatus().toUpperCase()) {
                case "PENDING":   pending++;   break;
                case "APPROVED":  approved++;  break;
                case "CANCELLED": cancelled++; break;
                case "DONE":      done++;      break;
            }
        }

        int total = appointments.size();
        System.out.println("  Total Appointments : " + total);
        ConsoleHelper.printThinDivider();
        System.out.printf("  %-12s %5s %10s%n", "Status", "Count", "Percentage");
        ConsoleHelper.printThinDivider();
        System.out.printf("  %-12s %5d %9.1f%%%n", "PENDING", pending, (pending * 100.0 / total));
        System.out.printf("  %-12s %5d %9.1f%%%n", "APPROVED", approved, (approved * 100.0 / total));
        System.out.printf("  %-12s %5d %9.1f%%%n", "CANCELLED", cancelled, (cancelled * 100.0 / total));
        System.out.printf("  %-12s %5d %9.1f%%%n", "DONE", done, (done * 100.0 / total));
        ConsoleHelper.printThinDivider();
    }

    private void reportRevenue(List<Appointment> appointments) {
        ConsoleHelper.printHeader("Report: Revenue Summary");

        float totalRevenue = 0;
        float pendingRevenue = 0;
        float completedRevenue = 0;
        float cancelledRevenue = 0;

        for (Appointment a : appointments) {
            switch (a.getStatus().toUpperCase()) {
                case "PENDING":
                case "APPROVED":
                    pendingRevenue += a.getTotalAmount();
                    break;
                case "DONE":
                    completedRevenue += a.getTotalAmount();
                    break;
                case "CANCELLED":
                    cancelledRevenue += a.getTotalAmount();
                    break;
            }
            totalRevenue += a.getTotalAmount();
        }

        System.out.printf("  %-22s : PHP %,.2f%n", "Total Booked Revenue", totalRevenue);
        System.out.printf("  %-22s : PHP %,.2f%n", "Completed (Earned)", completedRevenue);
        System.out.printf("  %-22s : PHP %,.2f%n", "Pending/Approved", pendingRevenue);
        System.out.printf("  %-22s : PHP %,.2f%n", "Cancelled (Lost)", cancelledRevenue);
        ConsoleHelper.printThinDivider();
    }

    private void reportByPet(List<Appointment> appointments) {
        ConsoleHelper.printHeader("Report: Appointments per Pet");

        Map<String, int[]> petStats = new LinkedHashMap<>();

        for (Appointment a : appointments) {
            String petName = a.getPetName() != null ? a.getPetName() : "Unknown";
            petStats.computeIfAbsent(petName, k -> new int[]{0});
            petStats.get(petName)[0]++;
        }

        System.out.printf("  %-18s %8s%n", "Pet Name", "Bookings");
        ConsoleHelper.printThinDivider();

        for (Map.Entry<String, int[]> entry : petStats.entrySet()) {
            System.out.printf("  %-18s %8d%n", entry.getKey(), entry.getValue()[0]);
        }
        ConsoleHelper.printThinDivider();
        System.out.println("  Total Pets: " + petStats.size());
    }

    private void printAppointmentTable(List<Appointment> appointments) {
        System.out.printf("  %-4s %-10s %-11s %-6s %-10s %8s%n",
                "ID", "Pet", "Date", "Time", "Status", "Total");
        ConsoleHelper.printThinDivider();

        for (Appointment a : appointments) {
            System.out.printf("  %-4d %-10s %-11s %-6s %-10s %8.2f%n",
                    a.getAppointmentId(),
                    truncate(a.getPetName(), 10),
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

