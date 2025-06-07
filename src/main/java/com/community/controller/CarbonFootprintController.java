package com.community.controller;

import com.community.model.CarbonFootprint;
import com.community.model.User;
import com.community.service.CarbonFootprintService;
import com.community.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carbon-footprint")
@RequiredArgsConstructor
public class CarbonFootprintController {
    private final CarbonFootprintService carbonFootprintService;
    private final UserService userService;

    @PostMapping("/calculate")
    public ResponseEntity<CarbonFootprint> calculateFootprint(@RequestBody CarbonFootprint footprint) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName());
        footprint.setUser(user);
        return ResponseEntity.ok(carbonFootprintService.calculateAndSave(footprint));
    }

    @GetMapping("/user")
    public ResponseEntity<List<CarbonFootprint>> getUserFootprints() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName());
        return ResponseEntity.ok(carbonFootprintService.getUserFootprints(user));
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<CarbonFootprint>> getEventFootprints(@PathVariable Long eventId) {
        return ResponseEntity.ok(carbonFootprintService.getEventFootprints(null)); // TODO: Implement event lookup
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarbonFootprint> getFootprint(@PathVariable Long id) {
        return ResponseEntity.ok(carbonFootprintService.getFootprintById(id));
    }

    @GetMapping("/user/total")
    public ResponseEntity<Double> getUserTotalOffset() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName());
        return ResponseEntity.ok(carbonFootprintService.calculateTotalOffset(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFootprint(@PathVariable Long id) {
        carbonFootprintService.delete(id);
        return ResponseEntity.ok().build();
    }
} 