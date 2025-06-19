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
import java.util.UUID;

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
            logger.info("[CONTROLLER] Starting event creation process");
            logger.info("[CONTROLLER] Event title: {}", event.getTitle());
            logger.info("[CONTROLLER] Event UUID before service call: {}", event.getUuid());
            
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = userService.findByUsername(auth.getName());
            logger.info("[CONTROLLER] Setting organizer: {}", user.getUsername());
            event.setOrganizer(user);
            
            Event savedEvent = eventService.createEvent(event);
            logger.info("[CONTROLLER] Event saved successfully");
            logger.info("[CONTROLLER] Saved event UUID: {}", savedEvent.getUuid());
            logger.info("[CONTROLLER] Saved event ID: {}", savedEvent.getId());
            
            redirectAttributes.addFlashAttribute("successMessage", "Event created successfully!");
            String redirectUrl = "redirect:/events/" + savedEvent.getUuid();
            logger.info("[CONTROLLER] Redirecting to: {}", redirectUrl);
            return redirectUrl;
        } catch (Exception e) {
            logger.error("[CONTROLLER] Error creating event: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating event: " + e.getMessage());
            return "redirect:/events/new";
        }
    }

    @GetMapping("/{uuid}")
    public String showEventDetails(@PathVariable UUID uuid, Model model) {
        try {
            logger.info("[CONTROLLER] Loading event details for UUID: {}", uuid);
            Event event = eventService.getEventById(uuid);
            logger.info("[CONTROLLER] Event found: {} - {}", uuid, event.getTitle());
            model.addAttribute("event", event);
            
            try {
                double carbonFootprint = carbonFootprintService.calculateEventFootprint(event);
                model.addAttribute("carbonFootprint", carbonFootprint);
                logger.info("[CONTROLLER] Carbon footprint calculated: {}", carbonFootprint);
            } catch (Exception e) {
                logger.warn("[CONTROLLER] Could not calculate carbon footprint for event {}: {}", uuid, e.getMessage());
                model.addAttribute("carbonFootprint", 0.0);
            }
            
            return "events/details";
        } catch (Exception e) {
            logger.error("[CONTROLLER] Error loading event details for uuid {}: {}", uuid, e.getMessage(), e);
            model.addAttribute("errorMessage", "Event not found or an error occurred.");
            return "error";
        }
    }

    @GetMapping("/{uuid}/edit")
    @PreAuthorize("isAuthenticated() and (hasRole('ROLE_ADMIN') or @eventService.isOrganizer(#uuid, authentication.principal.username))")
    public String showEditForm(@PathVariable UUID uuid, Model model) {
        Event event = eventService.getEventById(uuid);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName());
        
        if (!event.getOrganizer().equals(user)) {
            return "redirect:/events/" + uuid;
        }
        
        model.addAttribute("event", event);
        model.addAttribute("eventTypes", Event.EventType.values());
        return "events/form";
    }

    @PostMapping("/{uuid}")
    @PreAuthorize("isAuthenticated() and (hasRole('ROLE_ADMIN') or @eventService.isOrganizer(#uuid, authentication.principal.username))")
    public String updateEvent(@PathVariable UUID uuid, @ModelAttribute Event event, RedirectAttributes redirectAttributes) {
        try {
            event.setUuid(uuid);
            eventService.updateEvent(event);
            redirectAttributes.addFlashAttribute("successMessage", "Event updated successfully!");
            return "redirect:/events/" + uuid;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating event: " + e.getMessage());
            return "redirect:/events/" + uuid + "/edit";
        }
    }

    @PostMapping("/{uuid}/delete")
    @PreAuthorize("isAuthenticated() and (hasRole('ROLE_ADMIN') or @eventService.isOrganizer(#uuid, authentication.principal.username))")
    public String deleteEvent(@PathVariable UUID uuid, RedirectAttributes redirectAttributes) {
        try {
            eventService.deleteEvent(uuid);
            redirectAttributes.addFlashAttribute("successMessage", "Event deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting event: " + e.getMessage());
        }
        return "redirect:/my-events";
    }

    @PostMapping("/{uuid}/join")
    @PreAuthorize("isAuthenticated()")
    public String joinEvent(@PathVariable UUID uuid, RedirectAttributes redirectAttributes) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = userService.findByUsername(auth.getName());
            eventService.joinEvent(uuid, user);
            redirectAttributes.addFlashAttribute("successMessage", "Successfully joined the event!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error joining event: " + e.getMessage());
        }
        return "redirect:/events";
    }

    @PostMapping("/{uuid}/leave")
    @PreAuthorize("isAuthenticated()")
    public String leaveEvent(@PathVariable UUID uuid, RedirectAttributes redirectAttributes) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = userService.findByUsername(auth.getName());
            eventService.leaveEvent(uuid, user);
            redirectAttributes.addFlashAttribute("successMessage", "Successfully left the event!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error leaving event: " + e.getMessage());
        }
        return "redirect:/my-events";
    }

    @PostMapping("/{uuid}/volunteer")
    @PreAuthorize("isAuthenticated()")
    public String joinAsVolunteer(@PathVariable UUID uuid, RedirectAttributes redirectAttributes) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = userService.findByUsername(auth.getName());
            eventService.joinAsVolunteer(uuid, user);
            redirectAttributes.addFlashAttribute("successMessage", "Successfully registered as a volunteer!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error registering as volunteer: " + e.getMessage());
        }
        return "redirect:/volunteer";
    }

    @PostMapping("/{uuid}/volunteer/leave")
    @PreAuthorize("isAuthenticated()")
    public String leaveAsVolunteer(@PathVariable UUID uuid, RedirectAttributes redirectAttributes) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = userService.findByUsername(auth.getName());
            eventService.leaveAsVolunteer(uuid, user);
            redirectAttributes.addFlashAttribute("successMessage", "Successfully left as a volunteer!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error leaving as volunteer: " + e.getMessage());
        }
        return "redirect:/my-events";
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