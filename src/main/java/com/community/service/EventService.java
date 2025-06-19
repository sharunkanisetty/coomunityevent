package com.community.service;

import com.community.model.Event;
import com.community.model.User;
import java.util.List;
import java.util.UUID;

public interface EventService {
    List<Event> getAllEvents();
    Event getEventById(UUID uuid);
    Event createEvent(Event event);
    Event updateEvent(Event event);
    void deleteEvent(UUID uuid);
    List<Event> getUpcomingEvents();
    List<Event> getEventsByParticipant(User user);
    List<Event> getEventsByOrganizer(User user);
    void joinEvent(UUID eventUuid, User user);
    void leaveEvent(UUID eventUuid, User user);
    void joinAsVolunteer(UUID eventUuid, User user);
    void leaveAsVolunteer(UUID eventUuid, User user);
    List<Event> findByType(Event.EventType type);
    List<Event> searchEvents(Event.EventType type, String location);
    boolean isOrganizer(UUID eventUuid, String username);
} 