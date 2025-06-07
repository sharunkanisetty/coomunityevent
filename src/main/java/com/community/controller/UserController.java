package com.community.controller;

import com.community.model.User;
import com.community.model.Badge;
import com.community.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> getTopContributors() {
        return ResponseEntity.ok(userService.findTopContributors());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        return ResponseEntity.ok(userService.save(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @Valid @RequestBody User user) {
        return ResponseEntity.ok(userService.updateUser(id, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/points")
    public ResponseEntity<Void> addPoints(@PathVariable Long id, @RequestParam Integer points) {
        userService.addPoints(id, points);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/badges/{badgeId}")
    public ResponseEntity<Void> awardBadge(@PathVariable Long userId, @PathVariable Long badgeId) {
        userService.awardBadge(userId, badgeId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/badges")
    public ResponseEntity<List<Badge>> getUserBadges(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserBadges(id));
    }

    @GetMapping("/{id}/volunteer-hours")
    public ResponseEntity<Double> getTotalVolunteerHours(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getTotalVolunteerHours(id));
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<Void> updatePassword(@PathVariable Long id, @RequestParam String newPassword) {
        userService.updatePassword(id, newPassword);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.findByEmail(email));
    }
} 