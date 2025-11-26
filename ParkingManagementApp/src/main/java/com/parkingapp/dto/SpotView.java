package com.parkingapp.dto;

import com.parkingapp.model.ParkingSpot;
import com.parkingapp.model.Resident;

public class SpotView {
    private final ParkingSpot spot;
    private final Resident resident; // may be null

    public SpotView(ParkingSpot spot, Resident resident) {
        this.spot = spot;
        this.resident = resident;
    }

    public ParkingSpot getSpot() { return spot; }
    public Resident getResident() { return resident; }
}
