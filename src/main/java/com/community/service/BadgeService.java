package com.community.service;

import com.community.model.Badge;
import com.community.model.User;
import com.community.model.Event;
import com.community.repository.BadgeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BadgeService {
    private final BadgeRepository badgeRepository;

    public List<Badge> getUserBadges(User user) {
        return badgeRepository.findByUser(user);
    }

    @Transactional
    public void checkAndAwardBadges(User user, Event event) {
        // Check for volunteer hours badges
        int totalHours = user.getParticipatedEvents().stream()
                .mapToInt(Event::getDurationInHours)
                .sum();

        if (totalHours >= 50 && !hasBadgeOfType(user, "Green Warrior")) {
            awardBadge(user, "Green Warrior", "Completed 50 hours of volunteering", 
                    "tree", "success", Badge.BadgeType.VOLUNTEER_HOURS);
        } else if (totalHours >= 25 && !hasBadgeOfType(user, "Environmental Champion")) {
            awardBadge(user, "Environmental Champion", "Completed 25 hours of volunteering", 
                    "award", "primary", Badge.BadgeType.VOLUNTEER_HOURS);
        } else if (totalHours >= 10 && !hasBadgeOfType(user, "Earth Guardian")) {
            awardBadge(user, "Earth Guardian", "Completed 10 hours of volunteering", 
                    "heart", "info", Badge.BadgeType.VOLUNTEER_HOURS);
        }

        // Check for event participation badges
        long participatedEvents = user.getParticipatedEvents().size();
        if (participatedEvents >= 10 && !hasBadgeOfType(user, "Event Master")) {
            awardBadge(user, "Event Master", "Participated in 10 events", 
                    "star", "warning", Badge.BadgeType.EVENT_PARTICIPATION);
        }

        // Check for event organization badges
        long organizedEvents = user.getOrganizedEvents().size();
        if (organizedEvents >= 5 && !hasBadgeOfType(user, "Community Leader")) {
            awardBadge(user, "Community Leader", "Organized 5 events", 
                    "people", "danger", Badge.BadgeType.EVENT_ORGANIZATION);
        }
    }

    private boolean hasBadgeOfType(User user, String badgeName) {
        return badgeRepository.findByUser(user).stream()
                .anyMatch(badge -> badge.getName().equals(badgeName));
    }

    private void awardBadge(User user, String name, String description, 
                          String icon, String color, Badge.BadgeType type) {
        Badge badge = new Badge();
        badge.setUser(user);
        badge.setName(name);
        badge.setDescription(description);
        badge.setIcon(icon);
        badge.setColor(color);
        badge.setType(type);
        badge.setEarnedDate(LocalDateTime.now());
        badgeRepository.save(badge);
    }
} 