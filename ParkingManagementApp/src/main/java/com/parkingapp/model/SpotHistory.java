package com.parkingapp.model;

import java.time.LocalDateTime;

public class SpotHistory {
    private Long id;
    private Long parkingSpotId;
    private String changedBy;
    private String changeType;
    private LocalDateTime changeTime;

    public SpotHistory() {
    }

    public SpotHistory(Long id, Long parkingSpotId, String changedBy, String changeType, LocalDateTime changeTime) {
        this.id = id;
        this.parkingSpotId = parkingSpotId;
        this.changedBy = changedBy;
        this.changeType = changeType;
        this.changeTime = changeTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParkingSpotId() {
        return parkingSpotId;
    }

    public void setParkingSpotId(Long parkingSpotId) {
        this.parkingSpotId = parkingSpotId;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }

    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    public LocalDateTime getChangeTime() {
        return changeTime;
    }

    public void setChangeTime(LocalDateTime changeTime) {
        this.changeTime = changeTime;
    }
}
