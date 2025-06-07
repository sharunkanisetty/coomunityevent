package com.community.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "redeem_rewards")
public class RedeemReward {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String rewardName;

    @Column(nullable = false)
    private Integer pointsCost;

    private String description;

    @Column(nullable = false)
    private LocalDateTime redeemDate;

    @Enumerated(EnumType.STRING)
    private RewardStatus status;

    private String rewardCode;

    public enum RewardStatus {
        PENDING,
        APPROVED,
        REJECTED,
        REDEEMED
    }

    @PrePersist
    protected void onCreate() {
        redeemDate = LocalDateTime.now();
        status = RewardStatus.PENDING;
    }
} 