package dao;

import models.Pet;
import utils.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * PetDAO — Database operations for pets and pet types.
 *
 * All SQL goes here. No business logic, no validation.
 */
public class PetDAO {

    // ── Pet Type Operations ──────────────────────────────────────────────────

    /**
     * Inserts a new pet type and returns the generated pet_type_id.
     *
     * @return the generated ID, or -1 on failure
     */
    public int insertPetType(String type, String breed, String size) {
        String sql = "INSERT INTO pet_type (pet_type, pet_breed, pet_size) VALUES (?, ?, ?)";
        try {
            Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, type);
            stmt.setString(2, breed);
            stmt.setString(3, size);
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("[DB] Insert pet type failed: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Finds an existing pet type by type, breed, and size.
     *
     * @return the pet_type_id if found, or -1 if not found
     */
    public int findPetTypeId(String type, String breed, String size) {
        String sql = "SELECT pet_type_id FROM pet_type WHERE pet_type = ? AND pet_breed = ? AND pet_size = ?";
        try {
            Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, type);
            stmt.setString(2, breed);
            stmt.setString(3, size);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("pet_type_id");
            }
        } catch (SQLException e) {
            System.out.println("[DB] Find pet type failed: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Gets or creates a pet type. If the combination already exists, returns
     * the existing ID. Otherwise inserts a new row and returns the new ID.
     */
    public int getOrCreatePetType(String type, String breed, String size) {
        int existing = findPetTypeId(type, breed, size);
        if (existing != -1) {
            return existing;
        }
        return insertPetType(type, breed, size);
    }

    /**
     * Returns pet type details (type, breed, size) for a given pet_type_id.
     *
     * @return a String array [type, breed, size], or null if not found
     */
    public String[] findPetTypeById(int petTypeId) {
        String sql = "SELECT pet_type, pet_breed, pet_size FROM pet_type WHERE pet_type_id = ?";
        try {
            Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, petTypeId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new String[] {
                        rs.getString("pet_type"),
                        rs.getString("pet_breed"),
                        rs.getString("pet_size")
                };
            }
        } catch (SQLException e) {
            System.out.println("[DB] Find pet type by ID failed: " + e.getMessage());
        }
        return null;
    }

    // ── Pet CRUD Operations ──────────────────────────────────────────────────

    /**
     * Inserts a new pet record.
     *
     * @return true if the insert succeeded
     */
    public boolean insert(Pet pet) {
        String sql = "INSERT INTO pet (pet_owner_id, pet_type_id, pet_name, pet_notes) VALUES (?, ?, ?, ?)";
        try {
            Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, pet.getOwnerId());
            stmt.setInt(2, pet.getTypeId());
            stmt.setString(3, pet.getName());
            stmt.setString(4, pet.getNotes());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB] Insert pet failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Returns all pets belonging to a specific owner.
     */
    public List<Pet> findByOwnerId(int ownerId) {
        String sql = "SELECT p.pet_id, p.pet_owner_id, p.pet_type_id, p.pet_name, p.pet_notes "
                + "FROM pet p WHERE p.pet_owner_id = ?";
        List<Pet> pets = new ArrayList<>();
        try {
            Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, ownerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                pets.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("[DB] Find pets by owner failed: " + e.getMessage());
        }
        return pets;
    }

    /**
     * Returns all pets in the system (for admin views).
     */
    public List<Pet> findAll() {
        String sql = "SELECT pet_id, pet_owner_id, pet_type_id, pet_name, pet_notes FROM pet";
        List<Pet> pets = new ArrayList<>();
        try {
            Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                pets.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("[DB] Find all pets failed: " + e.getMessage());
        }
        return pets;
    }

    /**
     * Returns a single pet by ID, or null if not found.
     */
    public Pet findById(int petId) {
        String sql = "SELECT pet_id, pet_owner_id, pet_type_id, pet_name, pet_notes FROM pet WHERE pet_id = ?";
        try {
            Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, petId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            System.out.println("[DB] Find pet by ID failed: " + e.getMessage());
        }
        return null;
    }

    /**
     * Searches pets by name keyword (case-insensitive LIKE match).
     * If ownerId is -1, searches all pets (admin). Otherwise filters by owner.
     */
    public List<Pet> searchByName(String keyword, int ownerId) {
        String sql;
        if (ownerId == -1) {
            sql = "SELECT pet_id, pet_owner_id, pet_type_id, pet_name, pet_notes "
                    + "FROM pet WHERE pet_name LIKE ?";
        } else {
            sql = "SELECT pet_id, pet_owner_id, pet_type_id, pet_name, pet_notes "
                    + "FROM pet WHERE pet_name LIKE ? AND pet_owner_id = ?";
        }

        List<Pet> pets = new ArrayList<>();
        try {
            Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + keyword + "%");
            if (ownerId != -1) {
                stmt.setInt(2, ownerId);
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                pets.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("[DB] Search pets failed: " + e.getMessage());
        }
        return pets;
    }

    /**
     * Updates a pet's name, notes, AND pet_type_id.
     *
     * @return true if the update affected a row
     */
    public boolean updateFull(Pet pet, int newPetTypeId) {
        String sql = "UPDATE pet SET pet_name = ?, pet_notes = ?, pet_type_id = ? WHERE pet_id = ?";
        try {
            Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, pet.getName());
            stmt.setString(2, pet.getNotes());
            stmt.setInt(3, newPetTypeId);
            stmt.setInt(4, pet.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB] Update pet (full) failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes a pet by ID. Cascades to appointments via FK.
     *
     * @return true if the delete affected a row
     */
    public boolean delete(int petId) {
        String sql = "DELETE FROM pet WHERE pet_id = ?";
        try {
            Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, petId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB] Delete pet failed: " + e.getMessage());
            return false;
        }
    }

    // ── Helper ───────────────────────────────────────────────────────────────

    private Pet mapRow(ResultSet rs) throws SQLException {
        return new Pet(
                rs.getInt("pet_id"),
                rs.getInt("pet_owner_id"),
                rs.getInt("pet_type_id"),
                rs.getString("pet_name"),
                rs.getString("pet_notes"));
    }
}
