package com.community.repository;

import com.community.model.VolunteerHours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VolunteerHoursRepository extends JpaRepository<VolunteerHours, Long> {
    List<VolunteerHours> findByUserId(Long userId);
    List<VolunteerHours> findByEventId(Long eventId);
    List<VolunteerHours> findByVerified(boolean verified);
} 