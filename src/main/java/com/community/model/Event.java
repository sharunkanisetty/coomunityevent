package com.community.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(nullable = false)
    private String location;

    @Column(name = "duration_hours")
    private Integer durationInHours;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type")
    private EventType eventType;

    @Column(name = "max_participants")
    private Integer maxParticipants;

    @ManyToOne
    @JoinColumn(name = "organizer_id")
    private User organizer;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "event_participants",
        joinColumns = @JoinColumn(name = "event_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> participants = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "event_volunteers",
        joinColumns = @JoinColumn(name = "event_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> volunteers = new HashSet<>();

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @Column(name = "volunteers_required")
    private Integer volunteersRequired;

    @Column(name = "participant_reward")
    private String participantReward;

    @Column(name = "volunteer_reward")
    private String volunteerReward;

    @Column(name = "processed", nullable = false)
    private boolean processed = false;

    @PrePersist
    public void ensureUuid() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
    }

    public enum EventType {
        CLEANUP,
        PLANTING,
        WORKSHOP,
        OTHER
    }
} 