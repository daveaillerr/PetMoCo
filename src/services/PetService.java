package services;

import dao.PetDAO;
import models.Pet;

import java.util.List;

/**
 * PetService — Business logic and validation for pet management.
 *
 * Validates inputs before delegating to PetDAO.
 * Business rules belong here, never in menus or DAOs.
 */
public class PetService {

    private final PetDAO petDAO;

    public PetService() {
        this.petDAO = new PetDAO();
    }

    /**
     * Registers a new pet after validating inputs.
     *
     * @param ownerId   the pet_owner_id of the logged-in pet owner
     * @param petName   the name of the pet (must not be empty)
     * @param petType   the animal type, e.g. "Dog" (must not be empty)
     * @param petBreed  the breed, e.g. "Labrador" (must not be empty)
     * @param petSize   the size category, e.g. "MEDIUM" (must not be empty)
     * @param notes     optional notes (may be empty)
     * @return true if the pet was registered successfully
     */
    public boolean registerPet(int ownerId, String petName, String petType,
                               String petBreed, String petSize, String notes) {
        // Validate required fields
        if (petName == null || petName.trim().isEmpty()) {
            System.out.println("[Validation] Pet name cannot be empty.");
            return false;
        }
        if (petType == null || petType.trim().isEmpty()) {
            System.out.println("[Validation] Pet type cannot be empty.");
            return false;
        }
        if (petBreed == null || petBreed.trim().isEmpty()) {
            System.out.println("[Validation] Pet breed cannot be empty.");
            return false;
        }
        if (petSize == null || petSize.trim().isEmpty()) {
            System.out.println("[Validation] Pet size cannot be empty.");
            return false;
        }

        // Get or create the pet type combination
        int petTypeId = petDAO.getOrCreatePetType(
                petType.trim().toUpperCase(),
                petBreed.trim(),
                petSize.trim().toUpperCase());

        if (petTypeId == -1) {
            System.out.println("[Validation] Failed to resolve pet type.");
            return false;
        }

        Pet pet = new Pet(0, ownerId, petTypeId, petName.trim(), notes != null ? notes.trim() : "");
        return petDAO.insert(pet);
    }

    /**
     * Returns all pets belonging to a given owner.
     */
    public List<Pet> getPetsByOwner(int ownerId) {
        return petDAO.findByOwnerId(ownerId);
    }

    /**
     * Returns all pets in the system (admin use).
     */
    public List<Pet> getAllPets() {
        return petDAO.findAll();
    }

    /**
     * Returns a single pet by ID, or null if not found.
     */
    public Pet getPetById(int petId) {
        return petDAO.findById(petId);
    }

    /**
     * Returns pet type details (type, breed, size) for display.
     *
     * @return a String array [type, breed, size], or null if not found
     */
    public String[] getPetTypeDetails(int petTypeId) {
        return petDAO.findPetTypeById(petTypeId);
    }

    /**
     * Updates a pet's name and notes.
     */
    public boolean updatePet(Pet pet) {
        if (pet.getName() == null || pet.getName().trim().isEmpty()) {
            System.out.println("[Validation] Pet name cannot be empty.");
            return false;
        }
        return petDAO.update(pet);
    }

    /**
     * Deletes a pet by ID. Database cascade removes its appointments.
     */
    public boolean deletePet(int petId) {
        return petDAO.delete(petId);
    }
}
