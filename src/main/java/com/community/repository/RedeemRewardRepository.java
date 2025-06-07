package com.community.repository;

import com.community.model.RedeemReward;
import com.community.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RedeemRewardRepository extends JpaRepository<RedeemReward, Long> {
    List<RedeemReward> findByUser(User user);
    List<RedeemReward> findByUserOrderByRedeemDateDesc(User user);
    List<RedeemReward> findByStatus(RedeemReward.RewardStatus status);
} 