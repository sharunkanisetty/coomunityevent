package com.community.controller;

import com.community.model.VolunteerHours;
import com.community.repository.VolunteerHoursRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/volunteer-hours")
public class VolunteerHoursController {

    @Autowired
    private VolunteerHoursRepository volunteerHoursRepository;

    @GetMapping
    public ResponseEntity<List<VolunteerHours>> getAllVolunteerHours() {
        return ResponseEntity.ok(volunteerHoursRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VolunteerHours> getVolunteerHours(@PathVariable Long id) {
        return ResponseEntity.ok(volunteerHoursRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Volunteer hours record not found")));
    }

    @PostMapping
    public ResponseEntity<VolunteerHours> createVolunteerHours(@RequestBody VolunteerHours volunteerHours) {
        return ResponseEntity.ok(volunteerHoursRepository.save(volunteerHours));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VolunteerHours> updateVolunteerHours(@PathVariable Long id, @RequestBody VolunteerHours volunteerHours) {
        volunteerHours.setId(id);
        return ResponseEntity.ok(volunteerHoursRepository.save(volunteerHours));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVolunteerHours(@PathVariable Long id) {
        volunteerHoursRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<VolunteerHours>> getVolunteerHoursByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(volunteerHoursRepository.findByUserId(userId));
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<VolunteerHours>> getVolunteerHoursByEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(volunteerHoursRepository.findByEventId(eventId));
    }

    @PutMapping("/{id}/verify")
    public ResponseEntity<VolunteerHours> verifyVolunteerHours(@PathVariable Long id) {
        VolunteerHours hours = volunteerHoursRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Volunteer hours record not found"));
        hours.setVerified(true);
        return ResponseEntity.ok(volunteerHoursRepository.save(hours));
    }
} 