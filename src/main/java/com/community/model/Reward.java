package com.community.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Table(name = "rewards")
public class Reward {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reward_name", nullable = false)
    private String rewardName;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "points_cost", nullable = false)
    private Integer pointsCost;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @Column(name = "image_url", length = 100)
    private String imageUrl;

    @Column(name = "reward_type", nullable = false)
    private String rewardType;

    public enum RewardType {
        GIFT_CARD,
        MERCHANDISE,
        EXPERIENCE,
        DONATION,
        OTHER
    }
} 