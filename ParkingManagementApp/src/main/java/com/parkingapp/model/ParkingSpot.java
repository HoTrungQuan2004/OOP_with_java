package com.parkingapp.model;

import com.parkingapp.enums.SpotStatus;
import com.parkingapp.enums.SpotType;

public class ParkingSpot {
    private final Long id;
    private final String code; 
    private final SpotType type;
    private SpotStatus status;
    private Long assignedResidentId; 

    public ParkingSpot(Long id, String code, SpotType type) {
        this.id = id;
        this.code = code;
        this.type = type;
        this.status = SpotStatus.FREE;
        this.assignedResidentId = null;
    }

    public boolean isFree() {
        return status == SpotStatus.FREE;
    }

    public void assignToResident(Long residentId) {
        this.assignedResidentId = residentId;
        this.status = SpotStatus.ASSIGNED;
    }

    public void unassign() {
        this.assignedResidentId = null;
        this.status = SpotStatus.FREE;
    }

    public void occupy() {
        this.status = SpotStatus.OCCUPIED;
    }

    public void release() {
        if (this.assignedResidentId != null) {
            this.status = SpotStatus.ASSIGNED;
        } else {
            this.status = SpotStatus.FREE;
        }
    }

    public void markOutOfService() {
        this.status = SpotStatus.OUT_OF_SERVICE;
        this.assignedResidentId = null;
    }

    public Long getId() { return id; }
    public String getCode() { return code; }
    public SpotType getType() { return type; }
    public SpotStatus getStatus() { return status; }
    public Long getAssignedResidentId() { return assignedResidentId; }
}