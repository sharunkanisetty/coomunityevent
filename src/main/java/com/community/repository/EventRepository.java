package com.community.repository;

import com.community.model.Event;
import com.community.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByDateAfter(LocalDateTime date);
    List<Event> findByEventType(Event.EventType type);
    List<Event> findByParticipantsContaining(User user);
    List<Event> findByOrganizer(User user);
    List<Event> findByLocationContainingIgnoreCase(String location);
    List<Event> findByEventTypeAndLocationContainingIgnoreCase(Event.EventType type, String location);

    @Query("SELECT e FROM Event e WHERE (:type IS NULL OR e.eventType = :type) AND (:location IS NULL OR LOWER(e.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND e.date > :date ORDER BY e.date")
    List<Event> searchEvents(@Param("type") Event.EventType type, @Param("location") String location, @Param("date") LocalDateTime date);

    @Query("SELECT e FROM Event e WHERE e.latitude BETWEEN :latMin AND :latMax AND e.longitude BETWEEN :lonMin AND :lonMax")
    List<Event> findNearbyEvents(@Param("latMin") double latMin, @Param("latMax") double latMax, @Param("lonMin") double lonMin, @Param("lonMax") double lonMax);
} 