package com.community.controller.web;

import com.community.model.Event;
import com.community.model.User;
import com.community.service.CarbonFootprintService;
import com.community.service.EventService;
import com.community.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventWebController {
    private static final Logger logger = LoggerFactory.getLogger(EventWebController.class);
    private final EventService eventService;
    private final UserService userService;
    private final CarbonFootprintService carbonFootprintService;

    @GetMapping
    public String listEvents(
            @RequestParam(required = false) Event.EventType type,
            @RequestParam(required = false) String location,
            Model model) {
        List<Event> events;
        if (type != null || (location != null && !location.trim().isEmpty())) {
            events = eventService.searchEvents(type, location);
        } else {
            events = eventService.getAllEvents();
        }
        
        model.addAttribute("events", events != null ? events : Collections.emptyList());
        model.addAttribute("eventTypes", Event.EventType.values());
        model.addAttribute("selectedType", type);
        model.addAttribute("selectedLocation", location);
        
        return "events/list";
    }

    @GetMapping("/new")
    @PreAuthorize("isAuthenticated()")
    public String showCreateForm(Model model) {
        model.addAttribute("event", new Event());
        model.addAttribute("eventTypes", Event.EventType.values());
        return "events/form";
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public String createEvent(@ModelAttribute Event event, RedirectAttributes redirectAttributes) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = userService.findByUsername(auth.getName());
            event.setOrganizer(user);
            eventService.createEvent(event);
            redirectAttributes.addFlashAttribute("successMessage", "Event created successfully!");
            return "redirect:/events";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating event: " + e.getMessage());
            return "redirect:/events/new";
        }
    }

    @GetMapping("/{id}")
    public String showEventDetails(@PathVariable Long id, Model model) {
        try {
            Event event = eventService.getEventById(id);
            model.addAttribute("event", event);
            
            try {
                double carbonFootprint = carbonFootprintService.calculateEventFootprint(event);
                model.addAttribute("carbonFootprint", carbonFootprint);
            } catch (Exception e) {
                logger.warn("Could not calculate carbon footprint for event {}: {}", id, e.getMessage());
                model.addAttribute("carbonFootprint", 0.0);
            }
            
            return "events/details";
        } catch (Exception e) {
            logger.error("Error loading event details for id {}: {}", id, e.getMessage(), e);
            model.addAttribute("errorMessage", "Event not found or an error occurred.");
            return "error";
        }
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("isAuthenticated() and (hasRole('ROLE_ADMIN') or @eventService.isOrganizer(#id, authentication.principal.username))")
    public String showEditForm(@PathVariable Long id, Model model) {
        Event event = eventService.getEventById(id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName());
        
        if (!event.getOrganizer().equals(user)) {
            return "redirect:/events/" + id;
        }
        
        model.addAttribute("event", event);
        model.addAttribute("eventTypes", Event.EventType.values());
        return "events/form";
    }

    @PostMapping("/{id}")
    @PreAuthorize("isAuthenticated() and (hasRole('ROLE_ADMIN') or @eventService.isOrganizer(#id, authentication.principal.username))")
    public String updateEvent(@PathVariable Long id, @ModelAttribute Event event, RedirectAttributes redirectAttributes) {
        try {
            event.setId(id);
            eventService.updateEvent(event);
            redirectAttributes.addFlashAttribute("successMessage", "Event updated successfully!");
            return "redirect:/events/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating event: " + e.getMessage());
            return "redirect:/events/" + id + "/edit";
        }
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("isAuthenticated() and (hasRole('ROLE_ADMIN') or @eventService.isOrganizer(#id, authentication.principal.username))")
    public String deleteEvent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            eventService.deleteEvent(id);
            redirectAttributes.addFlashAttribute("successMessage", "Event deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting event: " + e.getMessage());
        }
        return "redirect:/events";
    }

    @PostMapping("/{id}/join")
    @PreAuthorize("isAuthenticated()")
    public String joinEvent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = userService.findByUsername(auth.getName());
            eventService.joinEvent(id, user);
            redirectAttributes.addFlashAttribute("successMessage", "Successfully joined the event!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error joining event: " + e.getMessage());
        }
        return "redirect:/events/" + id;
    }

    @PostMapping("/{id}/leave")
    @PreAuthorize("isAuthenticated()")
    public String leaveEvent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = userService.findByUsername(auth.getName());
            eventService.leaveEvent(id, user);
            redirectAttributes.addFlashAttribute("successMessage", "Successfully left the event!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error leaving event: " + e.getMessage());
        }
        return "redirect:/events/" + id;
    }

    @GetMapping("/my-events")
    @PreAuthorize("isAuthenticated()")
    public String showMyEvents(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName());
        model.addAttribute("organizedEvents", eventService.getEventsByOrganizer(user));
        model.addAttribute("participatedEvents", eventService.getEventsByParticipant(user));
        return "events/myevents";
    }
} 