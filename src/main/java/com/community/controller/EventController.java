package com.community.controller;

import com.community.model.Event;
import com.community.model.User;
import com.community.service.EventService;
import com.community.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class EventController {
    private final EventService eventService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<Event> getEvent(@PathVariable UUID uuid) {
        return ResponseEntity.ok(eventService.getEventById(uuid));
    }

    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody Event event) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName());
        event.setOrganizer(user);
        return ResponseEntity.ok(eventService.createEvent(event));
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<Event> updateEvent(@PathVariable UUID uuid, @RequestBody Event event) {
        event.setUuid(uuid);
        return ResponseEntity.ok(eventService.updateEvent(event));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteEvent(@PathVariable UUID uuid) {
        eventService.deleteEvent(uuid);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{uuid}/join")
    public ResponseEntity<Void> joinEvent(@PathVariable UUID uuid, @RequestBody User user) {
        eventService.joinEvent(uuid, user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{uuid}/leave")
    public ResponseEntity<Void> leaveEvent(@PathVariable UUID uuid, @RequestBody User user) {
        eventService.leaveEvent(uuid, user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<Event>> getUpcomingEvents() {
        return ResponseEntity.ok(eventService.getUpcomingEvents());
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<Event>> getEventsByType(@PathVariable Event.EventType type) {
        return ResponseEntity.ok(eventService.findByType(type));
    }
} 