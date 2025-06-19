package com.community.service.impl;

import com.community.model.Event;
import com.community.model.User;
import com.community.repository.EventRepository;
import com.community.service.EventService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service("eventService")
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private static final Logger logger = LoggerFactory.getLogger(EventServiceImpl.class);
    private final EventRepository eventRepository;

    @Override
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @Override
    public Event getEventById(UUID uuid) {
        logger.info("[SERVICE] Looking for event with UUID: {}", uuid);
        try {
            Event event = eventRepository.findByUuid(uuid)
                    .orElseThrow(() -> new RuntimeException("Event not found"));
            logger.info("[SERVICE] Found event: {} with title: {}", uuid, event.getTitle());
            return event;
        } catch (Exception e) {
            logger.error("[SERVICE] Event not found for UUID: {}", uuid);
            throw e;
        }
    }

    @Override
    public Event createEvent(Event event) {
        logger.info("[SERVICE] Starting event creation for title: {}", event.getTitle());
        logger.info("[SERVICE] Event UUID before processing: {}", event.getUuid());
        
        if (event.getUuid() == null) {
            event.setUuid(UUID.randomUUID());
            logger.info("[SERVICE] Generated new UUID: {}", event.getUuid());
        }
        
        try {
            Event savedEvent = eventRepository.save(event);
            logger.info("[SERVICE] Event saved successfully with UUID: {}", savedEvent.getUuid());
            logger.info("[SERVICE] Saved event ID: {}", savedEvent.getId());
            
            // Verify the event can be retrieved immediately after saving
            try {
                Event retrievedEvent = eventRepository.findByUuid(savedEvent.getUuid()).orElse(null);
                if (retrievedEvent != null) {
                    logger.info("[SERVICE] Event verification successful - found in database: {}", retrievedEvent.getTitle());
                } else {
                    logger.error("[SERVICE] Event verification failed - event not found in database after save!");
                }
            } catch (Exception e) {
                logger.error("[SERVICE] Error during event verification: {}", e.getMessage());
            }
            
            return savedEvent;
        } catch (Exception e) {
            logger.error("[SERVICE] Error saving event: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Event updateEvent(Event event) {
        Event existingEvent = getEventById(event.getUuid());
        existingEvent.setTitle(event.getTitle());
        existingEvent.setDescription(event.getDescription());
        existingEvent.setDate(event.getDate());
        existingEvent.setLocation(event.getLocation());
        existingEvent.setDurationInHours(event.getDurationInHours());
        existingEvent.setLatitude(event.getLatitude());
        existingEvent.setLongitude(event.getLongitude());
        existingEvent.setEventType(event.getEventType());
        return eventRepository.save(existingEvent);
    }

    @Override
    public void deleteEvent(UUID uuid) {
        Event event = getEventById(uuid);
        eventRepository.delete(event);
    }

    @Override
    public List<Event> getUpcomingEvents() {
        return eventRepository.findByDateAfter(LocalDateTime.now());
    }

    @Override
    public List<Event> getEventsByParticipant(User user) {
        return eventRepository.findByParticipantsContaining(user);
    }

    @Override
    public List<Event> getEventsByOrganizer(User user) {
        return eventRepository.findByOrganizer(user);
    }

    @Override
    @Transactional
    public void joinEvent(UUID eventUuid, User user) {
        Event event = getEventById(eventUuid);
        if (event.getMaxParticipants() != null
                && event.getParticipants().size() < event.getMaxParticipants()
                && !event.getParticipants().contains(user)) {
            event.getParticipants().add(user);
            eventRepository.save(event);
        } else if (event.getParticipants().contains(user)) {
            logger.warn("[SERVICE] User {} already joined event: {} (UUID: {})", user.getUsername(), event.getTitle(), eventUuid);
        } else {
            logger.warn("[SERVICE] Attempt to join full event: {} (UUID: {})", event.getTitle(), eventUuid);
        }
    }

    @Override
    @Transactional
    public void leaveEvent(UUID eventUuid, User user) {
        Event event = getEventById(eventUuid);
        event.getParticipants().remove(user);
        eventRepository.save(event);
    }

    @Override
    public List<Event> findByType(Event.EventType type) {
        return eventRepository.findByEventType(type);
    }

    @Override
    public List<Event> searchEvents(Event.EventType type, String location) {
        if (type != null && location != null && !location.trim().isEmpty()) {
            return eventRepository.findByEventTypeAndLocationContainingIgnoreCase(type, location);
        } else if (type != null) {
            return eventRepository.findByEventType(type);
        } else if (location != null && !location.trim().isEmpty()) {
            return eventRepository.findByLocationContainingIgnoreCase(location);
        } else {
            return getAllEvents();
        }
    }

    @Override
    public boolean isOrganizer(UUID eventUuid, String username) {
        Event event = getEventById(eventUuid);
        return event != null && event.getOrganizer() != null && 
               event.getOrganizer().getUsername().equals(username);
    }

    @Override
    @Transactional
    public void joinAsVolunteer(UUID eventUuid, User user) {
        Event event = getEventById(eventUuid);
        if (event.getVolunteersRequired() != null
                && event.getVolunteers().size() < event.getVolunteersRequired()
                && !event.getVolunteers().contains(user)) {
            event.getVolunteers().add(user);
            eventRepository.save(event);
        } else if (event.getVolunteers().contains(user)) {
            logger.warn("[SERVICE] User {} already registered as volunteer for event: {} (UUID: {})", user.getUsername(), event.getTitle(), eventUuid);
        } else {
            logger.warn("[SERVICE] Attempt to join full volunteer slots for event: {} (UUID: {})", event.getTitle(), eventUuid);
        }
    }

    @Override
    @Transactional
    public void leaveAsVolunteer(UUID eventUuid, User user) {
        Event event = getEventById(eventUuid);
        event.getVolunteers().remove(user);
        eventRepository.save(event);
    }

    // Debug method to check database schema
    public void debugDatabaseSchema() {
        try {
            List<Event> allEvents = eventRepository.findAll();
            logger.info("[DEBUG] Total events in database: {}", allEvents.size());
            for (Event event : allEvents) {
                logger.info("[DEBUG] Event ID: {}, UUID: {}, Title: {}", 
                    event.getId(), event.getUuid(), event.getTitle());
            }
        } catch (Exception e) {
            logger.error("[DEBUG] Error checking database schema: {}", e.getMessage(), e);
        }
    }
} 