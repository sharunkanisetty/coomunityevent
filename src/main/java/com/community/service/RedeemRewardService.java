package com.community.service;

import com.community.model.RedeemReward;
import com.community.model.Reward;
import com.community.model.User;
import java.util.List;

public interface RedeemRewardService {
    RedeemReward createReward(RedeemReward reward);
    RedeemReward approveReward(Long id);
    RedeemReward rejectReward(Long id);
    List<RedeemReward> getUserRewards(User user);
    RedeemReward getRewardById(Long id);
    List<RedeemReward> getPendingRewards();
    void delete(Long id);
    
    // New methods
    List<Reward> getAvailableRewards();
    RedeemReward redeemReward(User user, Long rewardId);
} 