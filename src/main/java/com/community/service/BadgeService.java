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
        int totalVolunteerHours = user.getVolunteeredEvents().stream()
            .filter(e -> e.getDurationInHours() != null)
            .mapToInt(Event::getDurationInHours)
            .sum();

        if (totalVolunteerHours >= 50 && !hasBadgeOfType(user, "Green Warrior")) {
            awardBadge(user, "Green Warrior", "Completed 50 hours of volunteering", 
                    "tree", "success", Badge.BadgeType.VOLUNTEER_HOURS);
        } else if (totalVolunteerHours >= 25 && !hasBadgeOfType(user, "Environmental Champion")) {
            awardBadge(user, "Environmental Champion", "Completed 25 hours of volunteering", 
                    "award", "primary", Badge.BadgeType.VOLUNTEER_HOURS);
        } else if (totalVolunteerHours >= 10 && !hasBadgeOfType(user, "Earth Guardian")) {
            awardBadge(user, "Earth Guardian", "Completed 10 hours of volunteering", 
                    "heart", "info", Badge.BadgeType.VOLUNTEER_HOURS);
        }

        // Check for participation hours badges
        int totalParticipationHours = user.getParticipatedEvents().stream()
            .filter(e -> e.getDurationInHours() != null)
            .mapToInt(Event::getDurationInHours)
            .sum();

        if (totalParticipationHours >= 50 && !hasBadgeOfType(user, "Super Participant")) {
            awardBadge(user, "Super Participant", "Completed 50 participation hours", 
                "trophy", "success", Badge.BadgeType.SPECIAL_ACHIEVEMENT);
        } else if (totalParticipationHours >= 25 && !hasBadgeOfType(user, "Engaged Participant")) {
            awardBadge(user, "Engaged Participant", "Completed 25 participation hours", 
                "medal", "primary", Badge.BadgeType.SPECIAL_ACHIEVEMENT);
        } else if (totalParticipationHours >= 10 && !hasBadgeOfType(user, "Active Participant")) {
            awardBadge(user, "Active Participant", "Completed 10 participation hours", 
                "person-check", "info", Badge.BadgeType.SPECIAL_ACHIEVEMENT);
        } else if (totalParticipationHours >= 5 && !hasBadgeOfType(user, "Starter Participant")) {
            awardBadge(user, "Starter Participant", "Completed 5 participation hours", 
                "flag", "secondary", Badge.BadgeType.SPECIAL_ACHIEVEMENT);
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