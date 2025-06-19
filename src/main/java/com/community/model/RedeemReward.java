package com.community.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Table(name = "redeem_rewards")
public class RedeemReward {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    private Reward reward;

    @Column(nullable = false)
    private String rewardName;

    @Column(nullable = false)
    private Integer pointsCost;

    private String description;

    @Column(nullable = false)
    private LocalDateTime redeemDate = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private RewardStatus status;

    private String rewardCode;

    @Column(name = "points_before")
    private Integer pointsBefore;

    @Column(name = "points_after")
    private Integer pointsAfter;

    public enum RewardStatus {
        PENDING,
        APPROVED,
        REJECTED,
        REDEEMED
    }

    @PrePersist
    protected void onCreate() {
        status = RewardStatus.REDEEMED;
    }
} 