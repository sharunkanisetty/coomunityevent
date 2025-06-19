package com.community.service;

import com.community.model.Reward;
import com.community.model.RedeemReward;
import com.community.model.User;
import java.util.List;

public interface RewardService {
    List<Reward> getAvailableRewards();
    boolean redeemReward(User user, Long rewardId);
    List<RedeemReward> getUserRedeemedRewards(User user);
} 