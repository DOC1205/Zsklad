package com.example.zsklad.model;

public class Company {
    private String name;
    private String address;
    private String phone;
    private String email;
    private String bin;  // Business Identification Number (БИН)

    public Company() {
        // Required empty constructor for Firebase
    }

    public Company(String name, String address, String phone, String email, String bin) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.bin = bin;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getBin() { return bin; }
    public void setBin(String bin) { this.bin = bin; }
} 