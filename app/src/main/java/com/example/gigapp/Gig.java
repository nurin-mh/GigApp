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

    //New constructor for notification use only
    public Gig(String gigName, String location, String datetimeStr) {
        this.gigName = gigName;
        this.location = location;

        try {
            String[] parts = datetimeStr.split(" ");
            if (parts.length == 2) {
                String datePart = parts[0]; // dd-MM-yyyy
                String timePart = parts[1]; // HH:mm

                this.schedule = new HashMap<>();
                this.schedule.put(datePart, timePart);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    // Returns the gig name as title for the notification
    public String getTitle() {
        return gigName;
    }

    // Formats the first date & time into a readable string
    public String getFormattedDateTime() {
        if (schedule != null && !schedule.isEmpty()) {
            String date = schedule.keySet().iterator().next(); // get first date
            String time = schedule.get(date);
            return date + " at " + time;
        }
        return "No schedule available";
    }

    // Converts the first date & time into milliseconds for AlarmManager
    public long getDateTimeMillis() {
        if (schedule != null && !schedule.isEmpty()) {
            String date = schedule.keySet().iterator().next(); // get first date
            String time = schedule.get(date);
            try {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
                java.util.Date datetime = sdf.parse(date + " " + time);
                return datetime.getTime();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return System.currentTimeMillis(); // fallback
    }
}
