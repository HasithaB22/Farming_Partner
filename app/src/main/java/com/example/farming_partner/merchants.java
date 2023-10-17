package com.example.farming_partner;
public class merchants {
    private String userId;
    private String name;
    private String username;
    private String nic;
    private String email;
    private String city;
    private String contact;
    private String profilePicture; // Base64 encoded image string
    private String profileImageBase64;

    // Required default constructor for Firebase
    public merchants() {
    }

    public merchants(String userId, String name, String username, String nic, String email, String city, String contact) {
        this.userId = userId;
        this.name = name;
        this.username = username;
        this.nic = nic;
        this.email = email;
        this.city = city;
        this.contact = contact;
        // Initialize profileImageBase64 to an empty string initially
        this.profilePicture = "";
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setname(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }


    public String getprofileImageBase64() { return profilePicture;
    }

    public void setProfileImageBase64(String profileImageBase64) {
        this.profilePicture = profileImageBase64;
    }
}
