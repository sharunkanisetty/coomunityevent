package com.community.service;

import com.community.model.Event;
import com.community.model.User;
import java.util.List;

public interface EventService {
    List<Event> getAllEvents();
    Event getEventById(Long id);
    Event createEvent(Event event);
    Event updateEvent(Event event);
    void deleteEvent(Long id);
    List<Event> getUpcomingEvents();
    List<Event> getEventsByParticipant(User user);
    List<Event> getEventsByOrganizer(User user);
    void joinEvent(Long eventId, User user);
    void leaveEvent(Long eventId, User user);
    List<Event> findByType(Event.EventType type);
} 