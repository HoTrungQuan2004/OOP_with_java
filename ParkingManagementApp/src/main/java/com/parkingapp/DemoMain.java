
// Chỉ để test nhanh, không phải production code

package com.parkingapp;

import com.parkingapp.enums.SpotType;
import com.parkingapp.model.ParkingSpot;
import com.parkingapp.model.Resident;
import com.parkingapp.repository.InMemoryParkingSpotRepository;
import com.parkingapp.repository.ParkingSpotRepository;
import com.parkingapp.service.ParkingService;
import com.parkingapp.service.impl.ParkingServiceImpl;
import java.sql.Connection;

public class DemoMain {
    public static void main(String[] args) {

    // Test kết nối JDBC

    Connection connection = com.parkingapp.repository.OracleJdbcHelper.getConnection();
    System.out.println(connection);

    com.parkingapp.repository.OracleJdbcHelper.printInfo(connection);

    com.parkingapp.repository.OracleJdbcHelper.closeConnection(connection);
    System.out.println(connection);

    
    // Test in-memory repo và service

    /* 
        // Khởi tạo in-memory repo và service
        ParkingSpotRepository repo = new InMemoryParkingSpotRepository();
        ParkingService service = new ParkingServiceImpl(repo);

        // Seed vài chỗ đỗ
        repo.save(new ParkingSpot(1L, "B1-01", SpotType.CAR));
        repo.save(new ParkingSpot(2L, "B1-02", SpotType.CAR));
        repo.save(new ParkingSpot(3L, "B1-03", SpotType.MOTORBIKE));
        repo.save(new ParkingSpot(4L, "B2-01", SpotType.CAR));

        // Hiển thị tất cả chỗ
        System.out.println("=== Tất cả chỗ ===");
        service.listAllSpots().forEach(
                s -> System.out.println(s.getId() + " - " + s.getCode() + " - " + s.getType() + " - " + s.getStatus()));

        // Hiển thị chỗ rảnh
        System.out.println("\n=== Chỗ rảnh ===");
        service.listFreeSpots().forEach(s -> System.out.println(s.getId() + " - " + s.getCode()));

        // Gán chỗ cho cư dân
        Resident resident = new Resident(101L, "Nguyen Van A", "B1-12A", "0900000000");
        try {
            service.assignSpotToResident(2L, resident);
            System.out.println("\nGán chỗ B1-02 cho " + resident.getName());
        } catch (Exception e) {
            System.err.println("Gán thất bại: " + e.getMessage());
        }

        // Đánh dấu 1 chỗ đang bảo trì
        service.markSpotOutOfService(5L);
        System.out.println("\nĐánh dấu B2-02 out-of-service");

        // Hiển thị trạng thái sau thay đổi
        System.out.println("\n=== Trạng thái sau cập nhật ===");
        service.listAllSpots()
                .forEach(s -> System.out.println(s.getCode() + " - " + s.getType() + " - " + s.getStatus() +
                        (s.getAssignedResidentId() != null ? " - assignedTo:" + s.getAssignedResidentId() : "")));
    
    */
    }
}