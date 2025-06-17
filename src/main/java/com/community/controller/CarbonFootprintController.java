package com.community.controller;

import com.community.model.CarbonFootprint;
import com.community.model.User;
import com.community.service.CarbonFootprintService;
import com.community.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/carbon-footprint")
@RequiredArgsConstructor
public class CarbonFootprintController {
    private final CarbonFootprintService carbonFootprintService;
    private final UserService userService;

    @GetMapping("/history")
    public String getHistoryView(
            @RequestParam(required = false) CarbonFootprint.MeasurementPeriod period,
            Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName());
        
        Map<String, Object> data = carbonFootprintService.getUserFootprintsWithDateRange(user, period);
        
        model.addAttribute("footprints", data.get("footprints"));
        model.addAttribute("dateRange", data.get("dateRange"));
        model.addAttribute("period", data.get("period"));
        model.addAttribute("totalOffset", data.get("totalOffset"));
        
        return "carbon-footprint/history";
    }

    @PostMapping("/calculate")
    @ResponseBody
    public ResponseEntity<CarbonFootprint> calculateFootprint(@RequestBody CarbonFootprint footprint) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName());
        footprint.setUser(user);
        return ResponseEntity.ok(carbonFootprintService.calculateAndSave(footprint));
    }

    @GetMapping("/user")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getUserFootprints(
            @RequestParam(required = false) CarbonFootprint.MeasurementPeriod period) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName());
        
        Map<String, Object> response = carbonFootprintService.getUserFootprintsWithDateRange(user, period);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/event/{eventId}")
    @ResponseBody
    public ResponseEntity<List<CarbonFootprint>> getEventFootprints(@PathVariable Long eventId) {
        return ResponseEntity.ok(carbonFootprintService.getEventFootprints(null)); // TODO: Implement event lookup
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<CarbonFootprint> getFootprint(@PathVariable Long id) {
        return ResponseEntity.ok(carbonFootprintService.getFootprintById(id));
    }

    @GetMapping("/user/total")
    @ResponseBody
    public ResponseEntity<Double> getUserTotalOffset() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName());
        return ResponseEntity.ok(carbonFootprintService.calculateTotalOffset(user));
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteFootprint(@PathVariable Long id) {
        carbonFootprintService.delete(id);
        return ResponseEntity.ok().build();
    }
} 