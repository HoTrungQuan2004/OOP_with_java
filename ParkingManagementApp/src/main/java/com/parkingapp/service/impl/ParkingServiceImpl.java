package com.parkingapp.service.impl;

import com.parkingapp.enums.SpotStatus;
import com.parkingapp.model.ParkingSpot;
import com.parkingapp.model.Resident;
import com.parkingapp.repository.ParkingSpotRepository;
import com.parkingapp.repository.ResidentRepository;
import com.parkingapp.service.ParkingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ParkingServiceImpl implements ParkingService {
    private final ParkingSpotRepository spotRepo;
    private final ResidentRepository residentRepo;

    @Autowired
    public ParkingServiceImpl(ParkingSpotRepository spotRepo, ResidentRepository residentRepo) {
        this.spotRepo = spotRepo;
        this.residentRepo = residentRepo;
    }

    @Override
    public List<ParkingSpot> listAllSpots() {
        return spotRepo.findAll();
    }

    @Override
    public List<ParkingSpot> listFreeSpots() {
        return spotRepo.findByTypeAndStatus(null, SpotStatus.FREE);
    }

    @Override
    public Optional<ParkingSpot> getSpotById(Long id) {
        return spotRepo.findById(id);
    }

    @Override
    public void addSpot(ParkingSpot spot) {
        spotRepo.save(spot);
    }

    @Override
    public void removeSpot(Long spotId) {
        spotRepo.deleteById(spotId);
    }

    @Override
    public void assignSpotToResident(Long spotId, Resident resident) throws Exception {
        ParkingSpot spot = spotRepo.findById(spotId).orElseThrow(() -> new IllegalArgumentException("Spot not found"));
        if (spot.getStatus() == SpotStatus.OUT_OF_SERVICE)
            throw new IllegalStateException("Spot out of service");
        if (spot.getStatus() == SpotStatus.OCCUPIED)
            throw new IllegalStateException("Spot currently occupied");
        
        // Ensure resident saved (repository may assign id externally)
        residentRepo.save(resident);
        spot.assignToResident(resident.getId());
        spotRepo.update(spot);
    }

    @Override
    public void unassignSpot(Long spotId) throws Exception {
        ParkingSpot spot = spotRepo.findById(spotId).orElseThrow(() -> new IllegalArgumentException("Spot not found"));
        spot.unassign();
        spotRepo.update(spot);
    }

    @Override
    public void markSpotOutOfService(Long spotId) {
        spotRepo.findById(spotId).ifPresent(s -> {
            s.markOutOfService();
            spotRepo.update(s);
        });
    }

    @Override
    public void markSpotFree(Long spotId) {
        spotRepo.findById(spotId).ifPresent(s -> {
            s.release();
            spotRepo.update(s);
        });
    }

    @Override
    public Optional<Resident> getResidentById(Long id) {
        if (id == null) return Optional.empty();
        return residentRepo.findById(id);
    }

}