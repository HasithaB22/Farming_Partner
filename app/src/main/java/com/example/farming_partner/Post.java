package com.example.farming_partner;

public class Post {
    private String postId;
    private String userId;
    private String crop;
    private String quantity;
    private boolean negotiable;
    private String farmerName;
    private String contactNumber;
    private String address;
    private String sellingLocation;
    private double latitude; // Add latitude field
    private double longitude;

    public Post() {
    }

    public Post(String postId, String userId, String crop, String quantity, boolean negotiable, String farmerName, String contactNumber, String address, String sellingLocation, double latitude, double longitude) {
        this.postId = postId;
        this.userId = userId;
        this.crop = crop;
        this.quantity = quantity;
        this.negotiable = negotiable;
        this.farmerName = farmerName;
        this.contactNumber = contactNumber;
        this.address = address;
        this.sellingLocation = sellingLocation;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCrop() {
        return crop;
    }

    public void setCrop(String crop) {
        this.crop = crop;
    }

    public String getquantity() {
        return quantity;
    }

    public void setCapacity(String capacity) {
        this.quantity = quantity;
    }

    public boolean isNegotiable() {
        return negotiable;
    }

    public void setNegotiable(boolean negotiable) {
        this.negotiable = negotiable;
    }

    public String getFarmerName() {
        return farmerName;
    }

    public void setFarmerName(String farmerName) {
        this.farmerName = farmerName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSellingLocation() {
        return sellingLocation;
    }

    public void setSellingLocation(String sellingLocation) {
        this.sellingLocation = sellingLocation;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }


    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

}
