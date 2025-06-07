package com.community.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "rewards")
public class Reward {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String rewardName;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private Integer pointsCost;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(nullable = false)
    private Integer stockQuantity;

    @Column(length = 100)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RewardType rewardType;

    public enum RewardType {
        GIFT_CARD,
        MERCHANDISE,
        EXPERIENCE,
        DONATION,
        OTHER
    }
} 