package com.parkingapp.repository;

import com.parkingapp.model.Resident;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@ConditionalOnProperty(name = "app.use-inmemory", havingValue = "false")
@Repository
public class JdbcResidentRepository implements ResidentRepository {

    private final JdbcTemplate jdbc;

    public JdbcResidentRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private Resident mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Resident(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("apartment"),
            rs.getString("phone")
        );
    }

    @Override
    public Optional<Resident> findById(Long id) {
        try {
            Resident r = jdbc.queryForObject("SELECT * FROM resident WHERE id = ?", this::mapRow, id);
            return Optional.ofNullable(r);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void save(Resident resident) {
        if (resident.getId() == null) {
            // Insert
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbc.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO resident (name, apartment, phone) VALUES (?, ?, ?)", 
                    new String[]{"id"});
                ps.setString(1, resident.getName());
                ps.setString(2, resident.getApartment());
                ps.setString(3, resident.getPhone());
                return ps;
            }, keyHolder);
        } else {
            // Update or Insert with ID (if ID is provided but not in DB, this might fail if we assume update, 
            // but usually save with ID means update. If we want upsert, it's more complex. 
            // For now, let's assume if ID exists, it's an update, or we try update and if 0 rows, insert.)
            int rows = jdbc.update("UPDATE resident SET name=?, apartment=?, phone=? WHERE id=?",
                resident.getName(), resident.getApartment(), resident.getPhone(), resident.getId());
            if (rows == 0) {
                 jdbc.update("INSERT INTO resident (id, name, apartment, phone) VALUES (?, ?, ?, ?)",
                     resident.getId(), resident.getName(), resident.getApartment(), resident.getPhone());
            }
        }
    }

    @Override
    public void deleteById(Long id) {
        jdbc.update("DELETE FROM resident WHERE id = ?", id);
    }

    @Override
    public List<Resident> findAll() {
        return jdbc.query("SELECT * FROM resident ORDER BY name", this::mapRow);
    }

    @Override
    public List<Resident> search(String keyword) {
        String pattern = "%" + keyword + "%";
        return jdbc.query("SELECT * FROM resident WHERE LOWER(name) LIKE LOWER(?) OR LOWER(apartment) LIKE LOWER(?) OR phone LIKE ?", 
            this::mapRow, pattern, pattern, pattern);
    }
}
