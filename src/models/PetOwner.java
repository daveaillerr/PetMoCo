
package models;

public class PetOwner extends User {
    private String fullName;
    private String emailAddress;
    private String contactNumber;
    private String homeAddress;

    public PetOwner(int userId, String username, String password, 
                    String fullName, String emailAddress, String contactNumber, String homeAddress) {
        super(userId, username, password, Role.USER);
        this.fullName = fullName;
        this.emailAddress = emailAddress;
        this.contactNumber = contactNumber;
        this.homeAddress = homeAddress;
    }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmailAddress() { return emailAddress; }
    public void setEmailAddress(String emailAddress) { this.emailAddress = emailAddress; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getHomeAddress() { return homeAddress; }
    public void setHomeAddress(String homeAddress) { this.homeAddress = homeAddress; }

    @Override
    public String getDisplayInfo() {
        return "Pet Owner: " + fullName + " | Contact: " + contactNumber + " | Address: " + homeAddress;
    }

    @Override
    public String toString() {
        return "PetOwner [ID: " + userId + ", Username: " + username + ", Name: " + fullName + 
               ", Contact: " + contactNumber + ", Address: " + homeAddress + "]";
    }
}

