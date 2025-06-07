package com.community.controller.web;

import com.community.model.RedeemReward;
import com.community.model.User;
import com.community.service.RedeemRewardService;
import com.community.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping({"/rewards", "/my-rewards"})
@RequiredArgsConstructor
public class RewardsWebController {
    private final RedeemRewardService redeemRewardService;
    private final UserService userService;

    @GetMapping
    public String showRewards(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName());
        
        model.addAttribute("user", user);
        model.addAttribute("availableRewards", redeemRewardService.getAvailableRewards());
        model.addAttribute("redeemedRewards", redeemRewardService.getUserRewards(user));
        
        return "rewards/index";
    }

    @PostMapping("/redeem")
    public String redeemPoints(@RequestParam("rewardId") Long rewardId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName());
        
        try {
            redeemRewardService.redeemReward(user, rewardId);
            return "redirect:/rewards?success";
        } catch (RuntimeException e) {
            return "redirect:/rewards?error=" + e.getMessage();
        }
    }

    @GetMapping("/history")
    public String showHistory(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName());
        
        model.addAttribute("rewards", redeemRewardService.getUserRewards(user));
        return "rewards/history";
    }

    @GetMapping("/admin")
    public String showAdminPanel(Model model) {
        model.addAttribute("pendingRewards", redeemRewardService.getPendingRewards());
        return "rewards/admin";
    }

    @PostMapping("/admin/{id}/approve")
    public String approveReward(@PathVariable Long id) {
        redeemRewardService.approveReward(id);
        return "redirect:/rewards/admin?success";
    }

    @PostMapping("/admin/{id}/reject")
    public String rejectReward(@PathVariable Long id) {
        redeemRewardService.rejectReward(id);
        return "redirect:/rewards/admin?success";
    }
} 