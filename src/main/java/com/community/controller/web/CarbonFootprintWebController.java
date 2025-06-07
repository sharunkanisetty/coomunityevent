package com.community.controller.web;

import com.community.model.CarbonFootprint;
import com.community.model.User;
import com.community.service.CarbonFootprintService;
import com.community.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/carbon-footprint")
@RequiredArgsConstructor
public class CarbonFootprintWebController {
    private final CarbonFootprintService carbonFootprintService;
    private final UserService userService;

    @GetMapping
    public String showCalculator(Model model, @RequestParam(required = false) String success) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName());
        
        if (!model.containsAttribute("footprint")) {
            model.addAttribute("footprint", new CarbonFootprint());
        }
        model.addAttribute("userFootprints", carbonFootprintService.getUserFootprints(user));
        model.addAttribute("totalOffset", carbonFootprintService.calculateTotalOffset(user));
        
        if (success != null) {
            model.addAttribute("successMessage", "Carbon footprint calculated successfully!");
        }
        
        return "carbon-footprint/calculator";
    }

    @PostMapping("/calculate")
    public String calculateFootprint(@ModelAttribute CarbonFootprint footprint, 
                                   BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes,
                                   Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = userService.findByUsername(auth.getName());
            footprint.setUser(user);
            
            // Basic validation
            if (footprint.getTransportationType() == null && 
                (footprint.getEnergyConsumption() == null || footprint.getEnergyConsumption() <= 0) &&
                (footprint.getWasteGenerated() == null || footprint.getWasteGenerated() <= 0)) {
                model.addAttribute("errorMessage", "Please provide at least one measurement (transportation, energy, or waste)");
                return showCalculator(model, null);
            }
            
            carbonFootprintService.calculateAndSave(footprint);
            redirectAttributes.addAttribute("success", true);
            return "redirect:/carbon-footprint";
            
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error calculating carbon footprint: " + e.getMessage());
            return showCalculator(model, null);
        }
    }

    @GetMapping("/history")
    public String showHistory(Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = userService.findByUsername(auth.getName());
            model.addAttribute("footprints", carbonFootprintService.getUserFootprints(user));
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error loading history: " + e.getMessage());
        }
        return "carbon-footprint/history";
    }

    @PostMapping("/{id}")
    public String deleteFootprint(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            carbonFootprintService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Calculation deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting calculation: " + e.getMessage());
        }
        return "redirect:/carbon-footprint/history";
    }
} 