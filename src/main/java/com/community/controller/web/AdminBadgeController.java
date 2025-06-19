package com.community.controller.web;

import com.community.model.User;
import com.community.service.BadgeService;
import com.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@Controller
public class AdminBadgeController {
    @Autowired
    private BadgeService badgeService;
    @Autowired
    private UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/award-badge")
    public String awardBadge(@RequestParam String username, @RequestParam String badgeName, Model model) {
        User user = userService.findByUsername(username);
        if (user == null) {
            model.addAttribute("errorMessage", "User not found");
            return "admin/award-badge";
        }
        boolean awarded = badgeService.awardBadge(user, badgeName, "Manually awarded by admin", "star", "info", null);
        if (awarded) {
            model.addAttribute("successMessage", "Badge awarded successfully!");
        } else {
            model.addAttribute("errorMessage", "Badge could not be awarded (maybe already has it?)");
        }
        return "admin/award-badge";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/recalculate-badges")
    public String showRecalculateBadgesPage() {
        return "admin/recalculate-badges";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/recalculate-badges")
    public String recalculateBadges(Model model) {
        List<User> users = userService.findAll();
        int count = 0;
        for (User user : users) {
            badgeService.checkAndAwardBadges(user, null);
            count++;
        }
        model.addAttribute("successMessage", "Badges recalculated for " + count + " users.");
        return "admin/recalculate-badges";
    }
} 