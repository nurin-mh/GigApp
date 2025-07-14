package com.example.gigapp;

import java.util.HashMap;

public class Gig {
    private String id;
    private String gigName;
    private String location;
    private String details;
    private String posterUrl;
    private String ownerId;
    private String salary;
    private String workers;
    private HashMap<String, String> schedule; // Key = date, value = time range

    public Gig() {
    }

    public Gig(String id, String gigName, String location, String details, String posterUrl,
               String ownerId, String salary, String workers, HashMap<String, String> schedule) {
        this.id = id;
        this.gigName = gigName;
        this.location = location;
        this.details = details;
        this.posterUrl = posterUrl;
        this.ownerId = ownerId;
        this.salary = salary;
        this.workers = workers;
        this.schedule = schedule;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

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

    public String getSalary() { return salary; }
    public void setSalary(String salary) { this.salary = salary; }

    public String getWorkers() { return workers; }
    public void setWorkers(String workers) { this.workers = workers; }

    public HashMap<String, String> getSchedule() { return schedule; }
    public void setSchedule(HashMap<String, String> schedule) { this.schedule = schedule; }
}
