package models;

public class Pet {
    private int pet_id;
    private int pet_owner_id;
    private int pet_type_id;
    private String pet_name;
    private String notes;

    // Constructor
    public Pet(int pet_id, int pet_owner_id, int pet_type_id, String pet_name, String notes) {
        this.pet_id = pet_id;
        this.pet_owner_id = pet_owner_id;
        this.pet_type_id = pet_type_id;
        this.pet_name = pet_name;
        this.notes = notes;
    }

    // Getters
    public int getId() {
        return pet_id;
    }

    public int getOwnerId() {
        return pet_owner_id;
    }

    public int getTypeId() {
        return pet_type_id;
    }

    public String getName() {
        return pet_name;
    }

    public String getNotes() {
        return notes;
    }

    // Setters
    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setName(String pet_name) {
        this.pet_name = pet_name;
    }

    @Override
    public String toString() {
        return pet_id + "," + pet_owner_id + "," + pet_type_id + "," + pet_name + "," + notes;
    }

    public class Pet_type {
        private String type;
        private String breed;
        private String size;

        // Constructor
        public Pet_type(String type, String breed, String size) {
            this.type = type;
            this.breed = breed;
            this.size = size;
        }

        // Getters

        public String getType() {
            return type;
        }

        public String getBreed() {
            return breed;
        }

        public String getSize() {
            return size;
        }

        // Setters

        public void setType(String type) {
            this.type = type;
        }

        public void setBreed(String breed) {
            this.breed = breed;
        }

        public void setSize(String size) {
            this.size = size;
        }

        @Override
        public String toString() {
            return type + breed + size;
        }
    }
}