package com.community.controller;

import com.community.model.User;
import com.community.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class WebController {
    private final UserService userService;

    @GetMapping("/badges")
    public String badges(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName());
        model.addAttribute("userBadges", user.getBadges());
        model.addAttribute("totalVolunteerHours", calculateTotalVolunteerHours(user));
        return "users/badges";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName());
        model.addAttribute("user", user);
        model.addAttribute("totalVolunteerHours", userService.getTotalVolunteerHours(user.getId()));
        model.addAttribute("recentEvents", user.getParticipatedEvents().stream()
                .sorted((e1, e2) -> e2.getDate().compareTo(e1.getDate()))
                .limit(5)
                .collect(Collectors.toList()));
        return "users/profile";
    }

    private int calculateTotalVolunteerHours(User user) {
        return user.getParticipatedEvents().stream()
                .mapToInt(event -> event.getDurationInHours())
                .sum();
    }
} 