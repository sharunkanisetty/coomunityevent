package com.community.controller;

import com.community.model.RedeemReward;
import com.community.model.User;
import com.community.service.RedeemRewardService;
import com.community.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rewards")
@RequiredArgsConstructor
public class RedeemRewardController {
    private final RedeemRewardService redeemRewardService;
    private final UserService userService;

    @PostMapping("/redeem")
    public ResponseEntity<RedeemReward> redeemPoints(@RequestBody RedeemReward reward) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName());
        reward.setUser(user);
        return ResponseEntity.ok(redeemRewardService.createReward(reward));
    }

    @GetMapping("/user")
    public ResponseEntity<List<RedeemReward>> getUserRewards() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName());
        return ResponseEntity.ok(redeemRewardService.getUserRewards(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RedeemReward> getReward(@PathVariable Long id) {
        return ResponseEntity.ok(redeemRewardService.getRewardById(id));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<RedeemReward> approveReward(@PathVariable Long id) {
        return ResponseEntity.ok(redeemRewardService.approveReward(id));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<RedeemReward> rejectReward(@PathVariable Long id) {
        return ResponseEntity.ok(redeemRewardService.rejectReward(id));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<RedeemReward>> getPendingRewards() {
        return ResponseEntity.ok(redeemRewardService.getPendingRewards());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReward(@PathVariable Long id) {
        redeemRewardService.delete(id);
        return ResponseEntity.ok().build();
    }
} 