package com.community.controller;

import com.community.model.User;
import com.community.security.CustomUserDetails;
import com.community.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        User user = userDetails.getUser();
        model.addAttribute("user", user);
        model.addAttribute("totalVolunteerHours", userService.getTotalVolunteerHours(user.getId()));
        model.addAttribute("badges", userService.getUserBadges(user.getId()));
        return "dashboard";
    }
} 