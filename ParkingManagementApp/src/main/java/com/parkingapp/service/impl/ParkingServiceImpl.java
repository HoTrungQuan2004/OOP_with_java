package com.parkingapp.service.impl;

import com.parkingapp.enums.SpotStatus;
import com.parkingapp.model.ParkingSpot;
import com.parkingapp.model.Resident;
import com.parkingapp.model.SpotHistory;
import com.parkingapp.repository.ParkingSpotRepository;
import com.parkingapp.repository.ResidentRepository;
import com.parkingapp.repository.SpotHistoryRepository;
import com.parkingapp.service.ParkingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ParkingServiceImpl implements ParkingService {
    private final ParkingSpotRepository spotRepo;
    private final ResidentRepository residentRepo;
    private final SpotHistoryRepository historyRepo;

    @Autowired
    public ParkingServiceImpl(ParkingSpotRepository spotRepo, ResidentRepository residentRepo, SpotHistoryRepository historyRepo) {
        this.spotRepo = spotRepo;
        this.residentRepo = residentRepo;
        this.historyRepo = historyRepo;
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
    public List<ParkingSpot> searchSpots(String keyword) {
        return spotRepo.search(keyword);
    }

    @Override
    public List<SpotHistory> getSpotHistory(Long spotId) {
        return historyRepo.findBySpotId(spotId);
    }

    @Override
    public Optional<ParkingSpot> getSpotById(Long id) {
        return spotRepo.findById(id);
    }

    @Override
    public void addSpot(ParkingSpot spot) {
        spotRepo.save(spot);
        recordHistory(spot.getId(), "CREATED");
    }

    @Override
    public void removeSpot(Long spotId) {
        spotRepo.deleteById(spotId);
        // History might be kept or deleted depending on requirements. 
        // For now, we just delete the spot. History remains in DB if FK allows or cascades.
        // But since we deleted the spot, we can't record history for it easily unless we do it before delete.
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
        recordHistory(spotId, "ASSIGNED to " + resident.getName());
    }

    @Override
    public void unassignSpot(Long spotId) throws Exception {
        ParkingSpot spot = spotRepo.findById(spotId).orElseThrow(() -> new IllegalArgumentException("Spot not found"));
        spot.unassign();
        spotRepo.update(spot);
        recordHistory(spotId, "UNASSIGNED");
    }

    @Override
    public void markSpotOutOfService(Long spotId) {
        spotRepo.findById(spotId).ifPresent(s -> {
            s.markOutOfService();
            spotRepo.update(s);
            recordHistory(spotId, "MARKED OUT OF SERVICE");
        });
    }

    @Override
    public void markSpotOccupied(Long spotId) {
        spotRepo.findById(spotId).ifPresent(s -> {
            s.occupy();
            spotRepo.update(s);
            recordHistory(spotId, "MARKED OCCUPIED");
        });
    }

    @Override
    public void markSpotFree(Long spotId) {
        spotRepo.findById(spotId).ifPresent(s -> {
            s.release();
            spotRepo.update(s);
            recordHistory(spotId, "MARKED FREE/ASSIGNED");
        });
    }

    @Override
    public Optional<Resident> getResidentById(Long id) {
        if (id == null) return Optional.empty();
        return residentRepo.findById(id);
    }

    @Override
    public List<Resident> getAllResidents() {
        return residentRepo.findAll();
    }

    @Override
    public List<Resident> searchResidents(String keyword) {
        return residentRepo.search(keyword);
    }

    private void recordHistory(Long spotId, String changeType) {
        SpotHistory history = new SpotHistory();
        history.setParkingSpotId(spotId);
        history.setChangedBy("System");
        history.setChangeType(changeType);
        history.setChangeTime(java.time.LocalDateTime.now());
        historyRepo.save(history);
    }

}