package com.parkingapp.model;

public class Resident extends Person {
    private final String apartment;

    public Resident() {
        super();
        this.apartment = null;
    }

    public Resident(Long id, String name, String apartment, String phone) {
        super(id, name, phone);
        this.apartment = apartment;
    }

    public Resident(String name, String apartment, String phone) {
        this(null, name, apartment, phone);
    }

    public String getApartment() {
        return apartment;
    }
}