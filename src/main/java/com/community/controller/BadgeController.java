package com.community.controller;

import com.community.model.Badge;
import com.community.model.User;
import com.community.service.BadgeService;
import com.community.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/badges")
@RequiredArgsConstructor
public class BadgeController {
    private final BadgeService badgeService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<Badge>> getUserBadges() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName());
        return ResponseEntity.ok(badgeService.getUserBadges(user));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<Badge>> getBadgesByType(@PathVariable Badge.BadgeType type) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName());
        return ResponseEntity.ok(badgeService.getUserBadges(user).stream()
                .filter(badge -> badge.getType() == type)
                .toList());
    }
} 