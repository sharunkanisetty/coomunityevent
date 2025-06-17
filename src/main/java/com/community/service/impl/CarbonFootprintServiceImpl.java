package com.community.service.impl;

import com.community.model.CarbonFootprint;
import com.community.model.User;
import com.community.model.Event;
import com.community.repository.CarbonFootprintRepository;
import com.community.service.CarbonFootprintService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CarbonFootprintServiceImpl implements CarbonFootprintService {
    private final CarbonFootprintRepository carbonFootprintRepository;

    @Override
    public CarbonFootprint calculateAndSave(CarbonFootprint footprint) {
        if (footprint == null) {
            throw new IllegalArgumentException("Footprint cannot be null");
        }

        // Initialize values to 0.0 if null
        Double distanceTraveled = footprint.getDistanceTraveled() != null ? footprint.getDistanceTraveled() : 0.0;
        Double energyConsumption = footprint.getEnergyConsumption() != null ? footprint.getEnergyConsumption() : 0.0;
        Double wasteGenerated = footprint.getWasteGenerated() != null ? footprint.getWasteGenerated() : 0.0;

        // Update the footprint with non-null values
        footprint.setDistanceTraveled(distanceTraveled);
        footprint.setEnergyConsumption(energyConsumption);
        footprint.setWasteGenerated(wasteGenerated);

        // Ensure measurement period is set
        if (footprint.getMeasurementPeriod() == null) {
            footprint.setMeasurementPeriod(CarbonFootprint.MeasurementPeriod.DAILY);
        }

        // Calculate emissions based on transportation type and distance
        double transportEmissions = calculateTransportEmissions(footprint.getTransportationType(), distanceTraveled);
        footprint.setTransportationEmissions(transportEmissions);

        // Calculate total carbon offset based on measurement period
        double totalOffset = calculateTotalOffset(
            transportEmissions,
            energyConsumption,
            wasteGenerated,
            footprint.getMeasurementPeriod()
        );
        footprint.setTotalCarbonOffset(totalOffset);

        // Ensure event is null if not provided
        if (footprint.getEvent() != null && footprint.getEvent().getId() == null) {
            footprint.setEvent(null);
        }

        return carbonFootprintRepository.save(footprint);
    }

    @Override
    public Map<String, Object> getUserFootprintsWithDateRange(User user, CarbonFootprint.MeasurementPeriod period) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate;
        String dateRangeDisplay;

        if (period == null) {
            period = CarbonFootprint.MeasurementPeriod.DAILY;
        }

        switch (period) {
            case WEEKLY:
                startDate = now.minusDays(7);
                DateTimeFormatter weekFormatter = DateTimeFormatter.ofPattern("MMM dd");
                dateRangeDisplay = startDate.format(weekFormatter) + " - " + now.format(weekFormatter);
                break;
            case MONTHLY:
                startDate = now.minusMonths(1);
                DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy");
                dateRangeDisplay = startDate.format(monthFormatter);
                break;
            default: // DAILY
                startDate = now.minusDays(1);
                DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
                dateRangeDisplay = now.format(dayFormatter);
                break;
        }

        List<CarbonFootprint> footprints = carbonFootprintRepository
            .findByUserAndCalculationDateBetweenOrderByCalculationDateDesc(user, startDate, now);

        Map<String, Object> response = new HashMap<>();
        response.put("footprints", footprints);
        response.put("dateRange", dateRangeDisplay);
        response.put("period", period);
        response.put("totalOffset", footprints.stream()
            .mapToDouble(CarbonFootprint::getTotalCarbonOffset)
            .sum());

        return response;
    }

    @Override
    public List<CarbonFootprint> getUserFootprints(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        return carbonFootprintRepository.findByUserOrderByCalculationDateDesc(user);
    }

    @Override
    public List<CarbonFootprint> getEventFootprints(Event event) {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }
        return carbonFootprintRepository.findByEvent(event);
    }

    @Override
    public CarbonFootprint getFootprintById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        return carbonFootprintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Carbon footprint not found"));
    }

    @Override
    public Double calculateTotalOffset(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        return getUserFootprints(user).stream()
                .mapToDouble(CarbonFootprint::getTotalCarbonOffset)
                .sum();
    }

    @Override
    public void delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        carbonFootprintRepository.deleteById(id);
    }

    @Override
    public double calculateEventFootprint(Event event) {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }
        
        // Get all footprints associated with this event
        List<CarbonFootprint> eventFootprints = carbonFootprintRepository.findByEvent(event);
        
        // Calculate total carbon offset from all footprints
        return eventFootprints.stream()
                .mapToDouble(CarbonFootprint::getTotalCarbonOffset)
                .sum();
    }

    private double calculateTransportEmissions(String transportType, Double distance) {
        if (distance == null || distance < 0) {
            return 0.0;
        }
        
        // Emission factors in kg CO2 per km
        return switch (transportType != null ? transportType.toUpperCase() : "") {
            case "CAR" -> distance * 0.2; // Average car emissions
            case "PUBLIC_TRANSPORT" -> distance * 0.08; // Bus/train emissions
            case "BICYCLE", "WALK" -> 0.0; // No emissions
            default -> distance * 0.15; // Default average
        };
    }

    private double calculateTotalOffset(double transportEmissions, double energyConsumption, 
                                     double wasteGenerated, CarbonFootprint.MeasurementPeriod period) {
        // Base calculation
        double totalOffset = transportEmissions +
                           (energyConsumption * 0.5) + // 0.5 kg CO2 per kWh
                           (wasteGenerated * 2.0);     // 2 kg CO2 per kg waste

        // Adjust based on period
        switch (period) {
            case DAILY:
                return totalOffset; // Daily is our base calculation
            case WEEKLY:
                return totalOffset / 7.0; // Convert to daily average
            case MONTHLY:
                return totalOffset / 30.0; // Convert to daily average
            default:
                return totalOffset;
        }
    }
} 