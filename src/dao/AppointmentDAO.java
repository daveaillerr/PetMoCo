package dao;

import models.Appointment;
import utils.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * AppointmentDAO — Database operations for appointments, services, and pricing.
 *
 * Handles the appointment table, the appointment_service junction table,
 * and read-only queries against services and pricing catalogs.
 */
public class AppointmentDAO {

    // ── Service & Pricing Catalog Queries ─────────────────────────────────────

    /**
     * Returns all available services as a list of [service_id, name, description, duration].
     */
    public List<String[]> findAllServices() {
        String sql = "SELECT service_id, services_name, service_description, service_duration FROM services";
        List<String[]> services = new ArrayList<>();
        try {
            Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                services.add(new String[]{
                    String.valueOf(rs.getInt("service_id")),
                    rs.getString("services_name"),
                    rs.getString("service_description"),
                    String.valueOf(rs.getInt("service_duration"))
                });
            }
        } catch (SQLException e) {
            System.out.println("[DB] Find services failed: " + e.getMessage());
        }
        return services;
    }

    /**
     * Looks up the price for a given service + pet size combination.
     *
     * @return a float array [pricing_id, price], or null if no pricing exists
     */
    public float[] findPricing(int serviceId, String petSize) {
        String sql = "SELECT pricing_id, price FROM pricing WHERE service_id = ? AND price_size = ?";
        try {
            Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, serviceId);
            stmt.setString(2, petSize);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new float[]{
                    rs.getInt("pricing_id"),
                    rs.getFloat("price")
                };
            }
        } catch (SQLException e) {
            System.out.println("[DB] Find pricing failed: " + e.getMessage());
        }
        return null;
    }

    // ── Appointment CRUD ─────────────────────────────────────────────────────

    /**
     * Inserts a new appointment and returns the generated appointment_id.
     *
     * @return the generated ID, or -1 on failure
     */
    public int insert(Appointment appt) {
        String sql = "INSERT INTO appointment (pet_id, pet_owner_id, appointment_date, "
                   + "appointment_time, appointment_status, total_amount) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        try {
            Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, appt.getPetId());
            stmt.setInt(2, appt.getPetOwnerId());
            stmt.setString(3, appt.getDate());
            stmt.setString(4, appt.getTime());
            stmt.setString(5, appt.getStatus());
            stmt.setFloat(6, appt.getTotalAmount());
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("[DB] Insert appointment failed: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Inserts a row into appointment_service to link an appointment
     * with a specific service and its pricing.
     */
    public boolean insertAppointmentService(int appointmentId, int serviceId, int pricingId) {
        String sql = "INSERT INTO appointment_service (appointment_id, service_id, pricing_id) "
                   + "VALUES (?, ?, ?)";
        try {
            Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, appointmentId);
            stmt.setInt(2, serviceId);
            stmt.setInt(3, pricingId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB] Insert appointment service failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Returns all appointments for a specific owner, JOINing pet name for display.
     */
    public List<Appointment> findByOwnerId(int petOwnerId) {
        String sql = "SELECT a.appointment_id, a.pet_id, a.pet_owner_id, "
                   + "a.appointment_date, a.appointment_time, a.appointment_status, "
                   + "a.total_amount, p.pet_name "
                   + "FROM appointment a "
                   + "JOIN pet p ON a.pet_id = p.pet_id "
                   + "WHERE a.pet_owner_id = ? "
                   + "ORDER BY a.appointment_date DESC, a.appointment_time DESC";
        return executeQuery(sql, petOwnerId);
    }

    /**
     * Returns all appointments for a specific pet.
     */
    public List<Appointment> findByPetId(int petId) {
        String sql = "SELECT a.appointment_id, a.pet_id, a.pet_owner_id, "
                   + "a.appointment_date, a.appointment_time, a.appointment_status, "
                   + "a.total_amount, p.pet_name "
                   + "FROM appointment a "
                   + "JOIN pet p ON a.pet_id = p.pet_id "
                   + "WHERE a.pet_id = ? "
                   + "ORDER BY a.appointment_date DESC, a.appointment_time DESC";
        return executeQuery(sql, petId);
    }

    /**
     * Returns all appointments in the system (admin view).
     */
    public List<Appointment> findAll() {
        String sql = "SELECT a.appointment_id, a.pet_id, a.pet_owner_id, "
                   + "a.appointment_date, a.appointment_time, a.appointment_status, "
                   + "a.total_amount, p.pet_name "
                   + "FROM appointment a "
                   + "JOIN pet p ON a.pet_id = p.pet_id "
                   + "ORDER BY a.appointment_date DESC, a.appointment_time DESC";
        List<Appointment> list = new ArrayList<>();
        try {
            Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("[DB] Find all appointments failed: " + e.getMessage());
        }
        return list;
    }

    /**
     * Returns a single appointment by ID, or null if not found.
     */
    public Appointment findById(int appointmentId) {
        String sql = "SELECT a.appointment_id, a.pet_id, a.pet_owner_id, "
                   + "a.appointment_date, a.appointment_time, a.appointment_status, "
                   + "a.total_amount, p.pet_name "
                   + "FROM appointment a "
                   + "JOIN pet p ON a.pet_id = p.pet_id "
                   + "WHERE a.appointment_id = ?";
        try {
            Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, appointmentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            System.out.println("[DB] Find appointment by ID failed: " + e.getMessage());
        }
        return null;
    }

        /**
     * Searches appointments by date.
     * If petOwnerId is -1, searches all (admin). Otherwise filters by owner.
     */
    public List<Appointment> searchByDate(String date, int petOwnerId) {
        String sql;
        if (petOwnerId == -1) {
            sql = "SELECT a.appointment_id, a.pet_id, a.pet_owner_id, "
                + "a.appointment_date, a.appointment_time, a.appointment_status, "
                + "a.total_amount, p.pet_name "
                + "FROM appointment a "
                + "JOIN pet p ON a.pet_id = p.pet_id "
                + "WHERE a.appointment_date = ? "
                + "ORDER BY a.appointment_time";
        } else {
            sql = "SELECT a.appointment_id, a.pet_id, a.pet_owner_id, "
                + "a.appointment_date, a.appointment_time, a.appointment_status, "
                + "a.total_amount, p.pet_name "
                + "FROM appointment a "
                + "JOIN pet p ON a.pet_id = p.pet_id "
                + "WHERE a.appointment_date = ? AND a.pet_owner_id = ? "
                + "ORDER BY a.appointment_time";
        }

        List<Appointment> list = new ArrayList<>();
        try {
            Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, date);
            if (petOwnerId != -1) {
                stmt.setInt(2, petOwnerId);
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("[DB] Search appointments by date failed: " + e.getMessage());
        }
        return list;
    }

    /**
     * Searches appointments by status keyword (e.g., PENDING, APPROVED, CANCELLED, DONE).
     * If petOwnerId is -1, searches all (admin). Otherwise filters by owner.
     */
    public List<Appointment> searchByStatus(String status, int petOwnerId) {
        String sql;
        if (petOwnerId == -1) {
            sql = "SELECT a.appointment_id, a.pet_id, a.pet_owner_id, "
                + "a.appointment_date, a.appointment_time, a.appointment_status, "
                + "a.total_amount, p.pet_name "
                + "FROM appointment a "
                + "JOIN pet p ON a.pet_id = p.pet_id "
                + "WHERE a.appointment_status = ? "
                + "ORDER BY a.appointment_date DESC";
        } else {
            sql = "SELECT a.appointment_id, a.pet_id, a.pet_owner_id, "
                + "a.appointment_date, a.appointment_time, a.appointment_status, "
                + "a.total_amount, p.pet_name "
                + "FROM appointment a "
                + "JOIN pet p ON a.pet_id = p.pet_id "
                + "WHERE a.appointment_status = ? AND a.pet_owner_id = ? "
                + "ORDER BY a.appointment_date DESC";
        }

        List<Appointment> list = new ArrayList<>();
        try {
            Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, status.toUpperCase());
            if (petOwnerId != -1) {
                stmt.setInt(2, petOwnerId);
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("[DB] Search appointments by status failed: " + e.getMessage());
        }
        return list;
    }


    /**
     * Sets an appointment's status to CANCELLED.
     *
     * @return true if the update affected a row
     */
    public boolean cancel(int appointmentId) {
        String sql = "UPDATE appointment SET appointment_status = 'CANCELLED' WHERE appointment_id = ?";
        try {
            Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, appointmentId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB] Cancel appointment failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Returns the services linked to a given appointment for display.
     * Each element: [service_name, price]
     */
    public List<String[]> findServicesByAppointmentId(int appointmentId) {
        String sql = "SELECT s.services_name, pr.price "
                   + "FROM appointment_service aps "
                   + "JOIN services s ON aps.service_id = s.service_id "
                   + "JOIN pricing pr ON aps.pricing_id = pr.pricing_id "
                   + "WHERE aps.appointment_id = ?";
        List<String[]> results = new ArrayList<>();
        try {
            Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, appointmentId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                results.add(new String[]{
                    rs.getString("services_name"),
                    String.format("%.2f", rs.getFloat("price"))
                });
            }
        } catch (SQLException e) {
            System.out.println("[DB] Find appointment services failed: " + e.getMessage());
        }
        return results;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /** Runs a parameterized query that takes a single int parameter. */
    private List<Appointment> executeQuery(String sql, int param) {
        List<Appointment> list = new ArrayList<>();
        try {
            Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, param);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("[DB] Appointment query failed: " + e.getMessage());
        }
        return list;
    }

    private Appointment mapRow(ResultSet rs) throws SQLException {
        return new Appointment(
            rs.getInt("appointment_id"),
            rs.getInt("pet_id"),
            rs.getInt("pet_owner_id"),
            rs.getString("appointment_date"),
            rs.getString("appointment_time"),
            rs.getString("appointment_status"),
            rs.getFloat("total_amount"),
            rs.getString("pet_name")
        );
    }
}