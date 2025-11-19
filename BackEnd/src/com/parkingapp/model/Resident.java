package com.parkingapp.model;

/**
 * Thông tin cư dân trong chung cư (người được gán chỗ)
 */
public class Resident {
    private final Long id;
    private final String name;
    private final String apartment; // ví dụ "B2-12A"
    private String phone;

    public Resident(Long id, String name, String apartment, String phone) {
        this.id = id;
        this.name = name;
        this.apartment = apartment;
        this.phone = phone;
    }

    // getters / setters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getApartment() { return apartment; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}