package com.cabease.controllers;
import com.cabease.models.User;
import com.cabease.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.validation.Valid;
@Controller
@Slf4j
public class AuthController {
    @Autowired
    private UserService userService;
    @GetMapping("/")
    public String home() {
        return "premium-home";
    }
    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user,
                               BindingResult result, RedirectAttributes redirectAttributes, Model model) {
        if (result.hasErrors()) {
            return "register";
        }
        try {
            userService.registerUser(user);
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login with your email.");
            log.info("User registered: {} ({})", user.getName(), user.getEmail());
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            if (e.getMessage().contains("email")) {
                model.addAttribute("emailError", e.getMessage());
            }
            return "register";
        }
        return "redirect:/login";
    }
    @GetMapping("/logout")
    public String logout() {
        return "redirect:/login?logout";
    }
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            User user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
            model.addAttribute("user", user);
            model.addAttribute("role", user.getRole());
            log.info("Dashboard accessed by user: {} ({})", user.getName(), email);
            if ("ROLE_USER".equals(user.getRole())) {
                return "redirect:/booking-dashboard";
            }
        }
        return "dashboard";
    }
    @GetMapping("/booking-dashboard")
    public String bookingDashboard(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        String email = authentication.getName();
        User user = userService.findByEmail(email).orElse(null);
        if (user == null) {
            log.warn("Authenticated principal {} not found in DB, redirecting to login", email);
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        model.addAttribute("role", user.getRole());
        log.info("Booking dashboard accessed by user: {} ({})", user.getName(), email);
        return "booking-dashboard";
    }
    @GetMapping("/ride-tracking")
    public String rideTracking(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        String email = authentication.getName();
        User user = userService.findByEmail(email).orElse(null);
        if (user == null) {
            log.warn("Authenticated principal {} not found in DB, redirecting to login", email);
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        log.info("Ride tracking page accessed by user: {} ({})", user.getName(), email);
        return "ride-tracking";
    }
    @GetMapping("/driver-simulator")
    public String driverSimulator(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        String email = authentication.getName();
        User user = userService.findByEmail(email).orElse(null);
        if (user == null) {
            log.warn("Authenticated principal {} not found in DB, redirecting to login", email);
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        log.info("Driver simulator accessed by user: {} ({})", user.getName(), email);
        return "driver-simulator";
    }
}
