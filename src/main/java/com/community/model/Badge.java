package com.community.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "badges")
public class Badge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String icon;

    @Column(nullable = false)
    private String color;

    @Column(name = "required_points")
    private Integer requiredPoints;

    @Column(name = "earned_date")
    private LocalDateTime earnedDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "badge_type")
    @Enumerated(EnumType.STRING)
    private BadgeType type;

    public enum BadgeType {
        VOLUNTEER_HOURS,
        EVENT_PARTICIPATION,
        EVENT_ORGANIZATION,
        SPECIAL_ACHIEVEMENT
    }
} 