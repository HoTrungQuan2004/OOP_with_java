package com.parkingapp.repository;

import com.parkingapp.model.SpotHistory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@ConditionalOnProperty(name = "app.use-inmemory", havingValue = "false")
@Repository
public class JdbcSpotHistoryRepository implements SpotHistoryRepository {

    private final JdbcTemplate jdbc;

    public JdbcSpotHistoryRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<SpotHistory> findBySpotId(Long spotId) {
        String sql = "SELECT id, parking_spot_id, changed_by, change_type, change_time FROM spot_history WHERE parking_spot_id = ? ORDER BY change_time DESC";
        return jdbc.query(sql, (rs, rowNum) -> {
            SpotHistory history = new SpotHistory();
            history.setId(rs.getLong("id"));
            history.setParkingSpotId(rs.getLong("parking_spot_id"));
            history.setChangedBy(rs.getString("changed_by"));
            history.setChangeType(rs.getString("change_type"));
            Timestamp ts = rs.getTimestamp("change_time");
            if (ts != null) {
                history.setChangeTime(ts.toLocalDateTime());
            }
            return history;
        }, spotId);
    }

    @Override
    public void save(SpotHistory history) {
        String sql = "INSERT INTO spot_history (parking_spot_id, changed_by, change_type, change_time) VALUES (?, ?, ?, ?)";
        jdbc.update(sql,
                history.getParkingSpotId(),
                history.getChangedBy(),
                history.getChangeType(),
                Timestamp.valueOf(history.getChangeTime())
        );
    }
}
