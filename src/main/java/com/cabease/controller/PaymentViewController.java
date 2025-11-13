package com.cabease.controller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.cabease.models.User;
import com.cabease.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
@Controller
public class PaymentViewController {
    @Autowired
    private UserRepository userRepository;
    @GetMapping("/payment-methods")
    public String paymentMethods(Model model) {
        User user = getCurrentUser();
        if (user != null) {
            model.addAttribute("userId", user.getId());
            model.addAttribute("userName", user.getName());
            model.addAttribute("userEmail", user.getEmail());
        }
        return "payment-methods";
    }
    @GetMapping("/wallet")
    public String wallet(Model model) {
        User user = getCurrentUser();
        if (user != null) {
            model.addAttribute("userId", user.getId());
            model.addAttribute("userName", user.getName());
            model.addAttribute("userEmail", user.getEmail());
        }
        return "wallet";
    }
    @GetMapping("/profile")
    public String profile(Model model) {
        User user = getCurrentUser();
        if (user != null) {
            model.addAttribute("userId", user.getId());
            model.addAttribute("userName", user.getName());
            model.addAttribute("userEmail", user.getEmail());
        }
        return "profile";
    }
    @GetMapping("/analytics-dashboard")
    public String analyticsDashboard(Model model) {
        User user = getCurrentUser();
        if (user != null) {
            model.addAttribute("userId", user.getId());
            model.addAttribute("userName", user.getName());
            model.addAttribute("userEmail", user.getEmail());
        }
        return "analytics-dashboard";
    }
    @GetMapping("/fare-calculator")
    public String fareCalculator(Model model) {
        User user = getCurrentUser();
        if (user != null) {
            model.addAttribute("userId", user.getId());
            model.addAttribute("userName", user.getName());
            model.addAttribute("userEmail", user.getEmail());
        }
        return "fare-calculator";
    }
    @GetMapping("/schedule-ride")
    public String scheduleRide(Model model) {
        User user = getCurrentUser();
        if (user != null) {
            model.addAttribute("userId", user.getId());
            model.addAttribute("userName", user.getName());
            model.addAttribute("userEmail", user.getEmail());
        }
        return "schedule-ride";
    }
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
            !authentication.getPrincipal().equals("anonymousUser")) {
            String email = authentication.getName();
            User user = userRepository.findByEmail(email);
            return user;
        }
        return null;
    }
}
