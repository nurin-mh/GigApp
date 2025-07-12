package com.example.gigapp;

public class Gig {
    private String gigName;
    private String location;
    private String details;
    private String posterUrl;
    private String ownerId;

    public Gig() {
        // Default constructor required for Firebase
    }

    public Gig(String gigName, String location, String details, String posterUrl, String ownerId) {
        this.gigName = gigName;
        this.location = location;
        this.details = details;
        this.posterUrl = posterUrl;
        this.ownerId = ownerId;
    }

    // Getters and setters
    public String getGigName() { return gigName; }
    public void setGigName(String gigName) { this.gigName = gigName; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }

    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }
}
