package com.parkingapp.model;

/**
 * (Tùy chọn) thông tin xe của cư dân
 */
public class Vehicle {
    private final Long id;
    private final String plate;
    private final String model;
    private final Long ownerResidentId; // resident id

    public Vehicle(Long id, String plate, String model, Long ownerResidentId) {
        this.id = id;
        this.plate = plate;
        this.model = model;
        this.ownerResidentId = ownerResidentId;
    }

    // getters
    public Long getId() { return id; }
    public String getPlate() { return plate; }
    public String getModel() { return model; }
    public Long getOwnerResidentId() { return ownerResidentId; }
}