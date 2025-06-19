package com.community.controller.web;

import com.community.model.User;
import com.community.model.RedeemReward;
import com.community.service.RewardService;
import com.community.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;
import java.util.List;

@Controller
public class RewardsWebController {
    private final RewardService rewardService;
    private final UserService userService;

    public RewardsWebController(RewardService rewardService, UserService userService) {
        this.rewardService = rewardService;
        this.userService = userService;
    }

    @GetMapping("/rewards")
    public String showRewards(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        model.addAttribute("availableRewards", rewardService.getAvailableRewards());
        model.addAttribute("user", user);
        List<RedeemReward> redeemedRewards = rewardService.getUserRedeemedRewards(user);
        model.addAttribute("redeemedRewards", redeemedRewards);
        // Add a set of redeemed reward IDs for button logic
        java.util.Set<Long> redeemedRewardIds = new java.util.HashSet<>();
        for (RedeemReward rr : redeemedRewards) {
            if (rr.getReward() != null) {
                redeemedRewardIds.add(rr.getReward().getId());
            }
        }
        model.addAttribute("redeemedRewardIds", redeemedRewardIds);
        return "rewards/index";
    }

    @PostMapping("/rewards/redeem")
    public String redeemReward(@RequestParam("rewardId") Long rewardId, Principal principal, Model model) {
        User user = userService.findByUsername(principal.getName());
        try {
            boolean success = rewardService.redeemReward(user, rewardId);
            model.addAttribute("availableRewards", rewardService.getAvailableRewards());
            model.addAttribute("user", user);
            model.addAttribute("redeemedRewards", rewardService.getUserRedeemedRewards(user));
            if (success) {
                model.addAttribute("successMessage", "Reward redeemed successfully!");
            } else {
                model.addAttribute("errorMessage", "Unable to redeem reward. Check your points or stock.");
            }
        } catch (Exception e) {
            model.addAttribute("availableRewards", rewardService.getAvailableRewards());
            model.addAttribute("user", user);
            model.addAttribute("redeemedRewards", rewardService.getUserRedeemedRewards(user));
            model.addAttribute("errorMessage", "An error occurred while redeeming the reward: " + e.getMessage());
        }
        return "rewards/index";
    }
} 