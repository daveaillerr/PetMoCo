package services;

import dao.AppointmentDAO;
import dao.PetDAO;
import models.Appointment;
import models.Pet;

import java.util.List;

/**
 * AppointmentService — Business logic for booking and managing appointments.
 *
 * Validates that the pet exists, calculates pricing based on pet size,
 * and delegates all DB operations to AppointmentDAO.
 */
public class AppointmentService {

    private final AppointmentDAO appointmentDAO;
    private final PetDAO petDAO;

    public AppointmentService() {
        this.appointmentDAO = new AppointmentDAO();
        this.petDAO = new PetDAO();
    }

    // ── Catalog Queries ──────────────────────────────────────────────────────

    /**
     * Returns all available services.
     * Each element: [service_id, name, description, duration_minutes]
     */
    public List<String[]> getAvailableServices() {
        return appointmentDAO.findAllServices();
    }

    /**
     * Looks up the price for a service + pet size combination.
     *
     * @return float array [pricing_id, price], or null if not found
     */
    public float[] getPrice(int serviceId, String petSize) {
        return appointmentDAO.findPricing(serviceId, petSize);
    }

    /**
     * Returns the pet size string for a given pet by looking up its pet_type.
     *
     * @return the size string (e.g. "MEDIUM"), or null if not found
     */
    public String getPetSize(int petId) {
        Pet pet = petDAO.findById(petId);
        if (pet == null) {
            return null;
        }
        String[] typeInfo = petDAO.findPetTypeById(pet.getTypeId());
        if (typeInfo == null) {
            return null;
        }
        return typeInfo[2]; // index 2 = size
    }

    // ── Booking ──────────────────────────────────────────────────────────────

    /**
     * Books a complete appointment with one or more services.
     *
     * Steps:
     * 1. Validate the pet exists
     * 2. Get pet size for pricing lookup
     * 3. Calculate total from selected services
     * 4. Insert appointment record
     * 5. Insert appointment_service junction rows
     *
     * @param petId       the pet's ID
     * @param petOwnerId  the pet owner's ID
     * @param date        appointment date (YYYY-MM-DD)
     * @param time        appointment time (HH:MM)
     * @param serviceIds  list of selected service_id values
     * @return the created appointment ID, or -1 on failure
     */
    public int bookAppointment(int petId, int petOwnerId, String date, String time,
                               List<Integer> serviceIds) {
        // Validate pet exists
        Pet pet = petDAO.findById(petId);
        if (pet == null) {
            System.out.println("[Validation] Pet not found.");
            return -1;
        }

        // Get pet size for pricing
        String petSize = getPetSize(petId);
        if (petSize == null) {
            System.out.println("[Validation] Could not determine pet size.");
            return -1;
        }

        // Calculate total and collect pricing IDs
        float totalAmount = 0;
        int[] pricingIds = new int[serviceIds.size()];

        for (int i = 0; i < serviceIds.size(); i++) {
            float[] pricing = appointmentDAO.findPricing(serviceIds.get(i), petSize);
            if (pricing == null) {
                System.out.println("[Validation] No pricing found for service "
                        + serviceIds.get(i) + " with size " + petSize + ".");
                return -1;
            }
            pricingIds[i] = (int) pricing[0]; // pricing_id
            totalAmount += pricing[1];          // price
        }

        // Insert appointment
        Appointment appt = new Appointment(petId, petOwnerId, date, time, totalAmount);
        int appointmentId = appointmentDAO.insert(appt);

        if (appointmentId == -1) {
            System.out.println("[Validation] Failed to create appointment.");
            return -1;
        }

        // Insert appointment_service junction rows
        for (int i = 0; i < serviceIds.size(); i++) {
            appointmentDAO.insertAppointmentService(appointmentId, serviceIds.get(i), pricingIds[i]);
        }

        return appointmentId;
    }

    // ── Queries ──────────────────────────────────────────────────────────────

    public List<Appointment> getAppointmentsByOwner(int petOwnerId) {
        return appointmentDAO.findByOwnerId(petOwnerId);
    }

    public List<Appointment> getAppointmentsByPet(int petId) {
        return appointmentDAO.findByPetId(petId);
    }

    public List<Appointment> getAllAppointments() {
        return appointmentDAO.findAll();
    }

    public Appointment getAppointmentById(int appointmentId) {
        return appointmentDAO.findById(appointmentId);
    }

    /**
     * Returns services linked to a specific appointment.
     * Each element: [service_name, price]
     */
    public List<String[]> getAppointmentServices(int appointmentId) {
        return appointmentDAO.findServicesByAppointmentId(appointmentId);
    }

    // ── Cancellation ─────────────────────────────────────────────────────────

    /**
     * Cancels an appointment. Rejects already-cancelled appointments.
     *
     * @return true if cancellation succeeded
     */
    public boolean cancelAppointment(int appointmentId) {
        Appointment appt = appointmentDAO.findById(appointmentId);
        if (appt == null) {
            System.out.println("[Validation] Appointment not found.");
            return false;
        }
        if ("CANCELLED".equalsIgnoreCase(appt.getStatus())) {
            System.out.println("[Validation] Appointment is already cancelled.");
            return false;
        }
        return appointmentDAO.cancel(appointmentId);
    }
}
