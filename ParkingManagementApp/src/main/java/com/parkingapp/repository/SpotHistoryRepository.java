package com.parkingapp.repository;

import com.parkingapp.model.SpotHistory;
import java.util.List;

public interface SpotHistoryRepository {
    List<SpotHistory> findBySpotId(Long spotId);
    void save(SpotHistory history);
}
