package com.community.service.impl;

import com.community.model.Event;
import com.community.model.User;
import com.community.repository.EventRepository;
import com.community.repository.UserRepository;
import com.community.service.BadgeService;
import com.community.service.UserService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventProcessingService {
    private static final Logger logger = LoggerFactory.getLogger(EventProcessingService.class);
    private final EventRepository eventRepository;
    private final UserService userService;
    private final BadgeService badgeService;
    private final UserRepository userRepository;

    public EventProcessingService(EventRepository eventRepository, UserService userService, BadgeService badgeService, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.userService = userService;
        this.badgeService = badgeService;
        this.userRepository = userRepository;
    }

    @Scheduled(cron = "0 * * * * *") // every minute for testing
    @Transactional
    public void processCompletedEvents() {
        List<Event> events = eventRepository.findByDateBeforeAndProcessedFalse(LocalDateTime.now());
        for (Event event : events) {
            // Award points to participants
            for (User participant : event.getParticipants()) {
                if (event.getParticipantReward() != null) {
                    try {
                        int points = Integer.parseInt(event.getParticipantReward());
                        userService.addPoints(participant.getId(), points);
                    } catch (NumberFormatException ignored) {}
                }
                badgeService.checkAndAwardBadges(participant, event);
            }
            // Award volunteer hours to volunteers
            for (User volunteer : event.getVolunteers()) {
                if (event.getDurationInHours() != null) {
                    userService.addVolunteerHours(volunteer.getId(), event.getId(), event.getDurationInHours());
                }
                badgeService.checkAndAwardBadges(volunteer, event);
            }
            // Award badges to organizer (for Community Leader)
            if (event.getOrganizer() != null) {
                badgeService.checkAndAwardBadges(event.getOrganizer(), event);
            }
            event.setProcessed(true);
            eventRepository.save(event);
            logger.info("Processed event {} for points, hours, and badges.", event.getId());
        }
    }
} 