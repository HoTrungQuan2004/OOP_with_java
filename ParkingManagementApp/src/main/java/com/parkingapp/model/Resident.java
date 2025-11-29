package com.parkingapp.model;

public class Resident {
    private Long id;
    private final String name;
    private final String apartment;
    private String phone;

    public Resident() {
        this.id = null;
        this.name = null;
        this.apartment = null;
        this.phone = null;
    }

    public Resident(Long id, String name, String apartment, String phone) {
        this.id = id;
        this.name = name;
        this.apartment = apartment;
        this.phone = phone;
    }

    public Resident(String name, String apartment, String phone) {
        this(null, name, apartment, phone);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public String getApartment() { return apartment; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}