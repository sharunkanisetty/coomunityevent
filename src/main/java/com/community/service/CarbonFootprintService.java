package com.community.service;

import com.community.model.CarbonFootprint;
import com.community.model.User;
import com.community.model.Event;
import java.util.List;

public interface CarbonFootprintService {
    CarbonFootprint calculateAndSave(CarbonFootprint footprint);
    List<CarbonFootprint> getUserFootprints(User user);
    List<CarbonFootprint> getEventFootprints(Event event);
    CarbonFootprint getFootprintById(Long id);
    Double calculateTotalOffset(User user);
    void delete(Long id);
} 