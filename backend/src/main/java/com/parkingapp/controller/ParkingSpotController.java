package com.parkingapp.controller;

import com.parkingapp.enums.SpotType;
import com.parkingapp.model.ParkingSpot;
import com.parkingapp.model.Resident;
import com.parkingapp.service.ParkingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Web Controller for Parking Management GUI
 * Handles all HTTP requests and renders HTML pages
 */
@Controller
@RequestMapping("/parking")
public class ParkingSpotController {

    @Autowired
    private ParkingService parkingService;

    /**
     * GUI Screenshot 1: Dashboard - Overview of all parking spots
     * URL: http://localhost:8080/parking/dashboard
     */
    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        long totalSpots = parkingService.listAllSpots().size();
        long freeSpots = parkingService.listFreeSpots().size();
        long occupiedSpots = totalSpots - freeSpots;
        
        model.addAttribute("totalSpots", totalSpots);
        model.addAttribute("freeSpots", freeSpots);
        model.addAttribute("occupiedSpots", occupiedSpots);
        model.addAttribute("recentSpots", parkingService.listAllSpots());
        
        return "dashboard";  // → templates/dashboard.html
    }

    /**
     * GUI Screenshot 2: List all parking spots
     * URL: http://localhost:8080/parking/spots
     */
    @GetMapping("/spots")
    public String listAllSpots(Model model) {
        model.addAttribute("spots", parkingService.listAllSpots());
        model.addAttribute("pageTitle", "All Parking Spots");
        return "parking-list";  // → templates/parking-list.html
    }

    /**
     * GUI Screenshot 2b: Show only free spots
     */
    @GetMapping("/spots/free")
    public String listFreeSpots(Model model) {
        model.addAttribute("spots", parkingService.listFreeSpots());
        model.addAttribute("pageTitle", "Available Spots");
        return "parking-list";
    }

    /**
     * GUI Screenshot 3: Form to add new parking spot
     * URL: http://localhost:8080/parking/spots/new
     */
    @GetMapping("/spots/new")
    public String showAddSpotForm(Model model) {
        model.addAttribute("spot", new ParkingSpot(null, "", SpotType.CAR));
        model.addAttribute("spotTypes", SpotType.values());
        return "add-spot";  // → templates/add-spot.html
    }

    @PostMapping("/spots/new")
    public String addSpot(@RequestParam String code,
                         @RequestParam SpotType type,
                         RedirectAttributes redirectAttributes) {
        try {
            Long newId = System.currentTimeMillis(); // Simple ID generation
            ParkingSpot spot = new ParkingSpot(newId, code, type);
            parkingService.addSpot(spot);
            redirectAttributes.addFlashAttribute("success", 
                "Parking spot " + code + " added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Failed to add spot: " + e.getMessage());
        }
        return "redirect:/parking/spots";
    }

    /**
     * GUI Screenshot 4: Assign parking spot to resident
     * URL: http://localhost:8080/parking/spots/{id}/assign
     */
    @GetMapping("/spots/{id}/assign")
    public String showAssignForm(@PathVariable Long id, Model model) {
        parkingService.getSpotById(id).ifPresentOrElse(
            spot -> {
                model.addAttribute("spot", spot);
                model.addAttribute("resident", new Resident(null, "", "", ""));
            },
            () -> model.addAttribute("error", "Spot not found")
        );
        return "assign-spot";  // → templates/assign-spot.html
    }

    @PostMapping("/spots/{id}/assign")
    public String assignSpot(@PathVariable Long id,
                            @RequestParam String name,
                            @RequestParam String apartment,
                            @RequestParam String phone,
                            RedirectAttributes redirectAttributes) {
        try {
            Long residentId = System.currentTimeMillis();
            Resident resident = new Resident(residentId, name, apartment, phone);
            parkingService.assignSpotToResident(id, resident);
            redirectAttributes.addFlashAttribute("success", 
                "Spot assigned to " + name + " successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/parking/spots";
    }

    /**
     * Unassign a parking spot
     */
    @PostMapping("/spots/{id}/unassign")
    public String unassignSpot(@PathVariable Long id, 
                              RedirectAttributes redirectAttributes) {
        try {
            parkingService.unassignSpot(id);
            redirectAttributes.addFlashAttribute("success", "Spot unassigned successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/parking/spots";
    }

    /**
     * Mark spot as out of service
     */
    @PostMapping("/spots/{id}/out-of-service")
    public String markOutOfService(@PathVariable Long id,
                                   RedirectAttributes redirectAttributes) {
        parkingService.markSpotOutOfService(id);
        redirectAttributes.addFlashAttribute("success", "Spot marked as out of service");
        return "redirect:/parking/spots";
    }

    /**
     * Delete parking spot
     */
    @PostMapping("/spots/{id}/delete")
    public String deleteSpot(@PathVariable Long id,
                            RedirectAttributes redirectAttributes) {
        parkingService.removeSpot(id);
        redirectAttributes.addFlashAttribute("success", "Parking spot deleted");
        return "redirect:/parking/spots";
    }

    /**
     * View resident details
     */
    @GetMapping("/residents/{id}")
    public String viewResident(@PathVariable Long id, Model model) {
        parkingService.getResidentById(id).ifPresentOrElse(
            resident -> model.addAttribute("resident", resident),
            () -> model.addAttribute("error", "Resident not found")
        );
        return "resident-details";
    }
}