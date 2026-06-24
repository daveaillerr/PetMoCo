package models;

/**
 * Appointment — Represents a pet service appointment.
 *
 * Maps to the `appointment` table. Fields align with the DB columns.
 * pet_name and owner info are populated via JOINs for display purposes.
 */
public class Appointment {

    private int appointmentId;
    private int petId;
    private int petOwnerId;
    private String appointmentDate;   // YYYY-MM-DD
    private String appointmentTime;   // HH:MM
    private String appointmentStatus; // PENDING | APPROVED | CANCELLED | DONE
    private float totalAmount;

    // Joined fields for display (not stored in appointment table directly)
    private String petName;

    // ── Constructors ─────────────────────────────────────────────────────────

    /** Full constructor — used when reading from DB. */
    public Appointment(int appointmentId, int petId, int petOwnerId,
                       String appointmentDate, String appointmentTime,
                       String appointmentStatus, float totalAmount,
                       String petName) {
        this.appointmentId = appointmentId;
        this.petId = petId;
        this.petOwnerId = petOwnerId;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.appointmentStatus = appointmentStatus;
        this.totalAmount = totalAmount;
        this.petName = petName;
    }

    /** Insert constructor — no ID yet, no joined pet name needed. */
    public Appointment(int petId, int petOwnerId,
                       String appointmentDate, String appointmentTime,
                       float totalAmount) {
        this.petId = petId;
        this.petOwnerId = petOwnerId;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.appointmentStatus = "PENDING";
        this.totalAmount = totalAmount;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public int getAppointmentId()    { return appointmentId; }
    public int getPetId()            { return petId; }
    public int getPetOwnerId()       { return petOwnerId; }
    public String getDate()          { return appointmentDate; }
    public String getTime()          { return appointmentTime; }
    public String getStatus()        { return appointmentStatus; }
    public float getTotalAmount()    { return totalAmount; }
    public String getPetName()       { return petName; }

    // ── Setters ──────────────────────────────────────────────────────────────

    public void setStatus(String status)      { this.appointmentStatus = status; }
    public void setTotalAmount(float amount)   { this.totalAmount = amount; }

    // ── Display ──────────────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "Appointment [ID: " + appointmentId
             + ", Pet: " + (petName != null ? petName : petId)
             + ", Date: " + appointmentDate
             + ", Time: " + appointmentTime
             + ", Status: " + appointmentStatus
             + ", Total: " + String.format("%.2f", totalAmount)
             + "]";
    }
}