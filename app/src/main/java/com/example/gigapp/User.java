package com.example.gigapp;

public class User {
    private String fullName, email, phone, address, gender;

    public User() {} // Required for Firebase

    public User(String fullName, String email, String phone, String address, String gender) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.gender = gender;
    }

    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public String getGender() { return gender; }

    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setAddress(String address) { this.address = address; }
    public void setGender(String gender) { this.gender = gender; }
}
