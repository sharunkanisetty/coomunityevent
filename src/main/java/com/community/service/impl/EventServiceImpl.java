package com.community.service.impl;

import com.community.model.Event;
import com.community.model.User;
import com.community.repository.EventRepository;
import com.community.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;

    @Override
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @Override
    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
    }

    @Override
    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    @Override
    public Event updateEvent(Event event) {
        Event existingEvent = getEventById(event.getId());
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
    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
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
    public void joinEvent(Long eventId, User user) {
        Event event = getEventById(eventId);
        event.getParticipants().add(user);
        eventRepository.save(event);
    }

    @Override
    @Transactional
    public void leaveEvent(Long eventId, User user) {
        Event event = getEventById(eventId);
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
    public boolean isOrganizer(Long eventId, String username) {
        Event event = getEventById(eventId);
        return event != null && event.getOrganizer() != null && 
               event.getOrganizer().getUsername().equals(username);
    }
} 