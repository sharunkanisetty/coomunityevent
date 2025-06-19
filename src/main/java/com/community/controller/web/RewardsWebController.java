package com.community.controller.web;

import com.community.model.User;
import com.community.service.RewardService;
import com.community.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;

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
        model.addAttribute("availableRewards", rewardService.getAvailableRewards());
        model.addAttribute("user", userService.findByUsername(principal.getName()));
        return "rewards/index";
    }

    @PostMapping("/rewards/redeem/{id}")
    public String redeemReward(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        User user = userService.findByUsername(principal.getName());
        boolean success = rewardService.redeemReward(user, id);
        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", "Reward redeemed successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Unable to redeem reward. Check your points or stock.");
        }
        return "redirect:/rewards";
    }

    @GetMapping("/my-rewards")
    public String myRewards(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        model.addAttribute("redeemedRewards", rewardService.getUserRedeemedRewards(user));
        return "rewards/my-rewards";
    }
} 