package com.community.service;

import com.community.model.CarbonFootprint;
import com.community.model.User;
import com.community.model.Event;
import java.util.List;
import java.util.Map;

public interface CarbonFootprintService {
    CarbonFootprint calculateAndSave(CarbonFootprint footprint);
    List<CarbonFootprint> getUserFootprints(User user);
    Map<String, Object> getUserFootprintsWithDateRange(User user, CarbonFootprint.MeasurementPeriod period);
    List<CarbonFootprint> getEventFootprints(Event event);
    CarbonFootprint getFootprintById(Long id);
    Double calculateTotalOffset(User user);
    void delete(Long id);
    double calculateEventFootprint(Event event);
} 