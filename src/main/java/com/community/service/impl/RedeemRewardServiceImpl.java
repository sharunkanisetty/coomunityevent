package com.community.service.impl;

import com.community.model.RedeemReward;
import com.community.model.Reward;
import com.community.model.User;
import com.community.repository.RedeemRewardRepository;
import com.community.repository.RewardRepository;
import com.community.service.RedeemRewardService;
import com.community.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RedeemRewardServiceImpl implements RedeemRewardService {
    private final RedeemRewardRepository redeemRewardRepository;
    private final RewardRepository rewardRepository;
    private final UserService userService;

    @Override
    public List<Reward> getAvailableRewards() {
        return rewardRepository.findByIsActiveTrueAndStockQuantityGreaterThan(0);
    }

    @Override
    public RedeemReward redeemReward(User user, Long rewardId) {
        Reward reward = rewardRepository.findById(rewardId)
                .orElseThrow(() -> new RuntimeException("Reward not found"));

        if (!reward.getIsActive()) {
            throw new RuntimeException("This reward is no longer available");
        }

        if (reward.getStockQuantity() <= 0) {
            throw new RuntimeException("This reward is out of stock");
        }

        if (user.getPoints() < reward.getPointsCost()) {
            throw new RuntimeException("Insufficient points for this reward");
        }

        // Create redemption record
        RedeemReward redeemReward = new RedeemReward();
        redeemReward.setUser(user);
        redeemReward.setRewardName(reward.getRewardName());
        redeemReward.setDescription(reward.getDescription());
        redeemReward.setPointsCost(reward.getPointsCost());
        redeemReward.setRedeemDate(LocalDateTime.now());
        redeemReward.setStatus(RedeemReward.RewardStatus.PENDING);
        redeemReward.setRewardCode(generateRewardCode());

        // Deduct points from user
        userService.addPoints(user.getId(), -reward.getPointsCost());

        // Decrease stock quantity
        reward.setStockQuantity(reward.getStockQuantity() - 1);
        rewardRepository.save(reward);

        return redeemRewardRepository.save(redeemReward);
    }

    @Override
    public RedeemReward createReward(RedeemReward reward) {
        User user = reward.getUser();
        if (user.getPoints() < reward.getPointsCost()) {
            throw new RuntimeException("Insufficient points for this reward");
        }

        // Deduct points from user
        userService.addPoints(user.getId(), -reward.getPointsCost());
        
        // Generate unique reward code
        reward.setRewardCode(generateRewardCode());
        
        return redeemRewardRepository.save(reward);
    }

    @Override
    public RedeemReward approveReward(Long id) {
        RedeemReward reward = getRewardById(id);
        reward.setStatus(RedeemReward.RewardStatus.APPROVED);
        return redeemRewardRepository.save(reward);
    }

    @Override
    public RedeemReward rejectReward(Long id) {
        RedeemReward reward = getRewardById(id);
        reward.setStatus(RedeemReward.RewardStatus.REJECTED);
        
        // Refund points to user
        userService.addPoints(reward.getUser().getId(), reward.getPointsCost());
        
        return redeemRewardRepository.save(reward);
    }

    @Override
    public List<RedeemReward> getUserRewards(User user) {
        return redeemRewardRepository.findByUserOrderByRedeemDateDesc(user);
    }

    @Override
    public RedeemReward getRewardById(Long id) {
        return redeemRewardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reward not found"));
    }

    @Override
    public List<RedeemReward> getPendingRewards() {
        return redeemRewardRepository.findByStatus(RedeemReward.RewardStatus.PENDING);
    }

    @Override
    public void delete(Long id) {
        redeemRewardRepository.deleteById(id);
    }

    private String generateRewardCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
} 