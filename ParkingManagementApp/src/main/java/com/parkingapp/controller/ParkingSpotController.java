package com.parkingapp.controller;

import com.parkingapp.enums.SpotType;
import com.parkingapp.model.ParkingSpot;
import com.parkingapp.model.Resident;
import com.parkingapp.service.ParkingService;
import java.util.stream.Collectors;
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
        var views = parkingService.listAllSpots().stream()
                .map(spot -> parkingService.getResidentById(spot.getAssignedResidentId()).map(res -> new com.parkingapp.dto.SpotView(spot, res)).orElseGet(() -> new com.parkingapp.dto.SpotView(spot, null)))
                .collect(Collectors.toList());
        model.addAttribute("spots", views);
        model.addAttribute("pageTitle", "All Parking Spots");
        return "parking-list";  // → templates/parking-list.html
    }

    /**
     * GUI Screenshot 2b: Show only free spots
     */
    @GetMapping("/spots/free")
    public String listFreeSpots(Model model) {
        var views = parkingService.listFreeSpots().stream()
                .map(spot -> parkingService.getResidentById(spot.getAssignedResidentId()).map(res -> new com.parkingapp.dto.SpotView(spot, res)).orElseGet(() -> new com.parkingapp.dto.SpotView(spot, null)))
                .collect(Collectors.toList());
        model.addAttribute("spots", views);
        model.addAttribute("pageTitle", "Available Spots");
        return "parking-list";
    }

    /**
     * Search spots
     */
    @GetMapping("/spots/search")
    public String searchSpots(@RequestParam String keyword, Model model) {
        var views = parkingService.searchSpots(keyword).stream()
                .map(spot -> parkingService.getResidentById(spot.getAssignedResidentId()).map(res -> new com.parkingapp.dto.SpotView(spot, res)).orElseGet(() -> new com.parkingapp.dto.SpotView(spot, null)))
                .collect(Collectors.toList());
        model.addAttribute("spots", views);
        model.addAttribute("pageTitle", "Search Results for '" + keyword + "'");
        model.addAttribute("keyword", keyword);
        return "parking-list";
    }

    /**
     * View spot history
     */
    @GetMapping("/spots/{id}/history")
    public String viewSpotHistory(@PathVariable Long id, Model model) {
        parkingService.getSpotById(id).ifPresentOrElse(
            spot -> {
                model.addAttribute("spot", spot);
                model.addAttribute("historyList", parkingService.getSpotHistory(id));
            },
            () -> model.addAttribute("error", "Spot not found")
        );
        return "spot-history";
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
    public String showAssignForm(@PathVariable Long id, 
                                 @RequestParam(required = false) String keyword,
                                 Model model) {
        parkingService.getSpotById(id).ifPresentOrElse(
            spot -> {
                model.addAttribute("spot", spot);
                if (keyword != null && !keyword.isEmpty()) {
                    model.addAttribute("residents", parkingService.searchResidents(keyword));
                    model.addAttribute("keyword", keyword);
                } else {
                    model.addAttribute("residents", parkingService.getAllResidents());
                }
            },
            () -> model.addAttribute("error", "Spot not found")
        );
        return "assign-spot";  // → templates/assign-spot.html
    }

    @PostMapping("/spots/{id}/assign")
    public String assignSpot(@PathVariable Long id,
                            @RequestParam Long residentId,
                            RedirectAttributes redirectAttributes) {
        try {
            Resident resident = parkingService.getResidentById(residentId)
                .orElseThrow(() -> new IllegalArgumentException("Resident not found"));
            parkingService.assignSpotToResident(id, resident);
            redirectAttributes.addFlashAttribute("success", 
                "Spot assigned to " + resident.getName() + " successfully!");
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
     * Mark spot as occupied
     */
    @PostMapping("/spots/{id}/occupy")
    public String markOccupied(@PathVariable Long id,
                               RedirectAttributes redirectAttributes) {
        parkingService.markSpotOccupied(id);
        redirectAttributes.addFlashAttribute("success", "Spot marked as occupied");
        return "redirect:/parking/spots";
    }

    /**
     * Mark spot as free (or assigned if resident exists)
     */
    @PostMapping("/spots/{id}/free")
    public String markFree(@PathVariable Long id,
                           RedirectAttributes redirectAttributes) {
        parkingService.markSpotFree(id);
        redirectAttributes.addFlashAttribute("success", "Spot marked as available");
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