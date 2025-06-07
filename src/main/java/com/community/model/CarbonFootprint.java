package com.community.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "carbon_footprints")
public class CarbonFootprint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = true)
    private Event event;

    private Double transportationEmissions; // in kg CO2
    private Double energyConsumption; // in kWh
    private Double wasteGenerated; // in kg
    private Double totalCarbonOffset; // in kg CO2

    @Column(nullable = false)
    private LocalDateTime calculationDate;

    private String transportationType; // e.g., "CAR", "PUBLIC_TRANSPORT", "BICYCLE"
    private Double distanceTraveled; // in km

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MeasurementPeriod measurementPeriod = MeasurementPeriod.DAILY;

    public enum MeasurementPeriod {
        DAILY("Daily"),
        WEEKLY("Weekly"),
        MONTHLY("Monthly");

        private final String displayName;

        MeasurementPeriod(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    @PrePersist
    protected void onCreate() {
        calculationDate = LocalDateTime.now();
    }
} 