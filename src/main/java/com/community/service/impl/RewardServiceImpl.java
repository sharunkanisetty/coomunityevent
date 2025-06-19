package com.community.service.impl;

import com.community.model.Reward;
import com.community.model.RedeemReward;
import com.community.model.User;
import com.community.repository.RewardRepository;
import com.community.repository.RedeemRewardRepository;
import com.community.repository.UserRepository;
import com.community.service.RewardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class RewardServiceImpl implements RewardService {
    private final RewardRepository rewardRepository;
    private final RedeemRewardRepository redeemRewardRepository;
    private final UserRepository userRepository;

    public RewardServiceImpl(RewardRepository rewardRepository, RedeemRewardRepository redeemRewardRepository, UserRepository userRepository) {
        this.rewardRepository = rewardRepository;
        this.redeemRewardRepository = redeemRewardRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Reward> getAvailableRewards() {
        List<Reward> rewards = rewardRepository.findByIsActiveTrueAndStockQuantityGreaterThan(0);
        // Deduplicate by reward id
        List<Reward> deduped = rewards.stream().collect(java.util.stream.Collectors.collectingAndThen(
            java.util.stream.Collectors.toMap(Reward::getId, r -> r, (r1, r2) -> r1),
            m -> new java.util.ArrayList<>(m.values())
        ));
        // Limit to first four rewards
        return deduped.stream().limit(4).toList();
    }

    @Override
    @Transactional
    public boolean redeemReward(User user, Long rewardId) {
        Reward reward = rewardRepository.findById(rewardId).orElse(null);
        if (reward == null || !reward.getIsActive() || reward.getStockQuantity() <= 0) return false;
        if (user.getPoints() < reward.getPointsCost()) return false;

        int pointsBefore = user.getPoints();

        user.setPoints(user.getPoints() - reward.getPointsCost());
        reward.setStockQuantity(reward.getStockQuantity() - 1);
        userRepository.save(user);
        rewardRepository.save(reward);

        int pointsAfter = user.getPoints();

        RedeemReward rr = new RedeemReward();
        rr.setUser(user);
        rr.setReward(reward);
        rr.setRewardName(reward.getRewardName());
        rr.setPointsCost(reward.getPointsCost());
        rr.setDescription(reward.getDescription());
        rr.setRedeemDate(java.time.LocalDateTime.now());
        rr.setStatus(com.community.model.RedeemReward.RewardStatus.REDEEMED);
        rr.setRewardCode(java.util.UUID.randomUUID().toString().substring(0,8).toUpperCase());
        rr.setPointsBefore(pointsBefore);
        rr.setPointsAfter(pointsAfter);
        redeemRewardRepository.save(rr);

        return true;
    }

    @Override
    public List<RedeemReward> getUserRedeemedRewards(User user) {
        return redeemRewardRepository.findByUserOrderByRedeemDateDesc(user);
    }
} 