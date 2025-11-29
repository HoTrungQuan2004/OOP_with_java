package com.parkingapp.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParkingLot {
    private final Long id;
    private final String name;
    private final List<ParkingSpot> spots = new ArrayList<>();

    public ParkingLot(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public void addSpot(ParkingSpot spot) {
        spots.add(spot);
    }

    public void removeSpotById(Long spotId) {
        spots.removeIf(s -> s.getId().equals(spotId));
    }

    public List<ParkingSpot> getSpots() {
        return Collections.unmodifiableList(spots);
    }

    public long countFreeSpots() {
        return spots.stream().filter(ParkingSpot::isFree).count();
    }

    public Long getId() { return id; }
    public String getName() { return name; }
}