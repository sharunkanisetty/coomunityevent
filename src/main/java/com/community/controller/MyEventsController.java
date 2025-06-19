package com.community.controller;

import com.community.model.Event;
import com.community.model.User;
import com.community.security.CustomUserDetails;
import com.community.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class MyEventsController {
    private final EventService eventService;

    @GetMapping("/my-events")
    public String myEvents(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        User user = userDetails.getUser();
        List<Event> organizedEvents = eventService.getEventsByOrganizer(user);
        List<Event> participatedEvents = eventService.getEventsByParticipant(user);
        List<Event> volunteeredEvents = user.getVolunteeredEvents() != null ? List.copyOf(user.getVolunteeredEvents()) : List.of();
        LocalDateTime now = LocalDateTime.now();

        // Active = event date is in the future or now
        List<Event> activeOrganized = organizedEvents.stream()
                .filter(e -> e.getDate().isAfter(now) || e.getDate().isEqual(now))
                .collect(Collectors.toList());
        List<Event> completedOrganized = organizedEvents.stream()
                .filter(e -> e.getDate().isBefore(now))
                .collect(Collectors.toList());
        List<Event> activeParticipated = participatedEvents.stream()
                .filter(e -> e.getDate().isAfter(now) || e.getDate().isEqual(now))
                .collect(Collectors.toList());
        List<Event> completedParticipated = participatedEvents.stream()
                .filter(e -> e.getDate().isBefore(now))
                .collect(Collectors.toList());
        List<Event> activeVolunteered = volunteeredEvents.stream()
                .filter(e -> e.getDate().isAfter(now) || e.getDate().isEqual(now))
                .collect(Collectors.toList());
        List<Event> completedVolunteered = volunteeredEvents.stream()
                .filter(e -> e.getDate().isBefore(now))
                .collect(Collectors.toList());

        model.addAttribute("activeOrganized", activeOrganized);
        model.addAttribute("completedOrganized", completedOrganized);
        model.addAttribute("activeParticipated", activeParticipated);
        model.addAttribute("completedParticipated", completedParticipated);
        model.addAttribute("activeVolunteered", activeVolunteered);
        model.addAttribute("completedVolunteered", completedVolunteered);
        return "myevents/index";
    }
} 