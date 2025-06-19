package com.community.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.community.model.Event;
import com.community.service.EventService;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class VolunteerController {
    private final EventService eventService;

    @GetMapping("/volunteer")
    public String volunteerOpportunities(Model model) {
        List<Event> allEvents = eventService.getAllEvents();
        List<Event> availableEvents = allEvents.stream()
            .filter(e -> e.getVolunteersRequired() != null && e.getParticipants().size() < e.getVolunteersRequired())
            .collect(Collectors.toList());
        availableEvents.forEach(e -> System.out.println("[DEBUG] Event: " + e.getTitle() + ", Current Volunteers: " + e.getParticipants().size()));
        model.addAttribute("events", availableEvents);
        return "volunteer/opportunities";
    }
} 