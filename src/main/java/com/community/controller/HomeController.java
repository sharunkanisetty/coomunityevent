package com.community.controller;

import com.community.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final EventService eventService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("upcomingEvents", eventService.getUpcomingEvents());
        return "index";
    }
} 