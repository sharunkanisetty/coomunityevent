package com.community.controller;

import com.community.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.community.model.Event;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final EventService eventService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("upcomingEvents", eventService.getUpcomingEvents());
        List<Event> volunteerOpportunities = eventService.getAllEvents().stream()
            .filter(e -> e.getVolunteersRequired() != null && e.getVolunteers().size() < e.getVolunteersRequired())
            .toList();
        model.addAttribute("volunteerOpportunities", volunteerOpportunities);
        return "index";
    }
} 