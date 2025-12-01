package com.parkingapp.repository;

import com.parkingapp.model.SpotHistory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@ConditionalOnProperty(name = "app.use-inmemory", havingValue = "true", matchIfMissing = true)
@Repository
public class InMemorySpotHistoryRepository implements SpotHistoryRepository {

    private final List<SpotHistory> historyList = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public List<SpotHistory> findBySpotId(Long spotId) {
        return historyList.stream()
                .filter(h -> h.getParkingSpotId().equals(spotId))
                .sorted((h1, h2) -> h2.getChangeTime().compareTo(h1.getChangeTime()))
                .collect(Collectors.toList());
    }

    @Override
    public void save(SpotHistory history) {
        if (history.getId() == null) {
            history.setId(idGenerator.getAndIncrement());
        }
        historyList.add(history);
    }

    @Override
    public void deleteBySpotId(Long spotId) {
        historyList.removeIf(h -> h.getParkingSpotId().equals(spotId));
    }
}
