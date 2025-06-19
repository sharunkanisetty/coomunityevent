package com.community.controller;

import com.community.model.User;
import com.community.model.Event;
import com.community.security.CustomUserDetails;
import com.community.service.UserService;
import com.community.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final UserService userService;
    private final EventService eventService;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        User user = userDetails.getUser();
        model.addAttribute("user", user);
        model.addAttribute("totalVolunteerHours", userService.getTotalVolunteerHours(user.getId()));
        model.addAttribute("badges", userService.getUserBadges(user.getId()));
        // Add available volunteer events
        List<Event> availableEvents = eventService.getAllEvents().stream()
            .filter(e -> e.getVolunteersRequired() != null && e.getVolunteers().size() < e.getVolunteersRequired())
            .collect(Collectors.toList());
        model.addAttribute("volunteerOpportunities", availableEvents);
        return "dashboard";
    }
} 