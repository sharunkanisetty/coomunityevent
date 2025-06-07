package com.community.repository;

import com.community.model.CarbonFootprint;
import com.community.model.User;
import com.community.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CarbonFootprintRepository extends JpaRepository<CarbonFootprint, Long> {
    List<CarbonFootprint> findByUser(User user);
    List<CarbonFootprint> findByEvent(Event event);
    List<CarbonFootprint> findByUserOrderByCalculationDateDesc(User user);
} 