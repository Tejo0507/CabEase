package com.cabease.controllers;
import com.cabease.models.User;
import com.cabease.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
@Slf4j
public class HomeController {
    @Autowired
    private UserService userService;
    @GetMapping("/premium")
    public String premiumHome() {
        return "premium-home";
    }
    @GetMapping("/home")
    public String home(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            User user = userService.findByUsername(username).orElse(null);
            model.addAttribute("user", user);
            model.addAttribute("authenticated", true);
            log.info("Home accessed by authenticated user: {}", username);
        } else {
            model.addAttribute("authenticated", false);
        }
        return "premium-home";
    }
    @GetMapping("/contact")
    public String contact(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            User user = userService.findByUsername(username).orElse(null);
            model.addAttribute("user", user);
            model.addAttribute("authenticated", true);
            log.info("Contact page accessed by authenticated user: {}", username);
        } else {
            model.addAttribute("authenticated", false);
        }
        return "contact";
    }
}
