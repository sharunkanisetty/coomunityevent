package com.community.controller;

import com.community.model.User;
import com.community.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new User());
        }
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user, 
                             BindingResult result, 
                             Model model,
                             RedirectAttributes redirectAttributes) {
        // Validate password confirmation
        if (!user.isPasswordConfirmed()) {
            result.addError(new FieldError("user", "confirmPassword", "Passwords do not match"));
        }

        // Check if username already exists
        if (userService.findByUsername(user.getUsername()) != null) {
            result.addError(new FieldError("user", "username", "Username is already taken"));
        }

        // Check if email already exists
        if (userService.findByEmail(user.getEmail()) != null) {
            result.addError(new FieldError("user", "email", "Email is already registered"));
        }

        if (result.hasErrors()) {
            return "auth/register";
        }

        try {
            userService.registerNewUser(user);
            redirectAttributes.addFlashAttribute("successMessage", "Registration successful! Please login.");
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "An unexpected error occurred during registration. Please try again.");
            return "auth/register";
        }
    }
} 