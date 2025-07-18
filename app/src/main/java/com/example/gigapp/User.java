package com.example.gigapp;

public class User {
    private String fullName, email, phone, companyName, position;

    public User() {} // Required for Firebase

    public User(String fullName, String email, String phone, String companyName, String position) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.companyName = companyName;
        this.position = position;
    }

    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getCompanyName() { return companyName; }
    public String getPosition() { return position; }

    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public void setPosition(String position) { this.position = position; }
}
