package com.community.controller;

import com.community.model.Event;
import com.community.model.User;
import com.community.service.EventService;
import com.community.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class WebController {
    private final EventService eventService;
    private final UserService userService;

    @GetMapping("/events")
    public String events(Model model) {
        model.addAttribute("events", eventService.getAllEvents());
        return "events/list";
    }

    @GetMapping("/events/new")
    public String newEvent(Model model) {
        model.addAttribute("event", new Event());
        model.addAttribute("eventTypes", Event.EventType.values());
        return "events/form";
    }

    @GetMapping("/events/{id}")
    public String eventDetails(@PathVariable Long id, Model model) {
        Event event = eventService.getEventById(id);
        model.addAttribute("event", event);
        model.addAttribute("totalParticipants", event.getParticipants().size());
        model.addAttribute("carbonFootprint", calculateCarbonFootprint(event));
        return "events/details";
    }

    @GetMapping("/myevents")
    public String myEvents(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName());
        model.addAttribute("participatingEvents", eventService.getEventsByParticipant(user));
        model.addAttribute("organizedEvents", eventService.getEventsByOrganizer(user));
        return "events/myevents";
    }

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
        return "users/profile";
    }

    private double calculateCarbonFootprint(Event event) {
        // Basic calculation based on event type and participants
        double baseFootprint = 100; // Base carbon footprint in kg CO2
        double participantFactor = event.getParticipants().size() * 0.5;
        
        switch (event.getEventType()) {
            case CLEANUP:
                return baseFootprint * 0.8 + participantFactor; // Cleanup events have lower footprint
            case PLANTING:
                return baseFootprint * 0.6 + participantFactor; // Planting events have lowest footprint
            case WORKSHOP:
                return baseFootprint * 1.2 + participantFactor; // Workshops have higher footprint
            default:
                return baseFootprint + participantFactor;
        }
    }

    private int calculateTotalVolunteerHours(User user) {
        return user.getParticipatedEvents().stream()
                .mapToInt(Event::getDurationInHours)
                .sum();
    }
} 