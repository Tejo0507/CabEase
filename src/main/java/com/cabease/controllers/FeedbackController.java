package com.cabease.controllers;
import com.cabease.models.Booking;
import com.cabease.models.Feedback;
import com.cabease.models.User;
import com.cabease.services.BookingService;
import com.cabease.services.FeedbackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.validation.Valid;
@Controller
@RequestMapping("/feedback")
@Slf4j
public class FeedbackController {
    @Autowired
    private FeedbackService feedbackService;
    @Autowired
    private BookingService bookingService;
    @GetMapping("/new/{bookingId}")
    public String newFeedbackForm(@PathVariable Long bookingId, @AuthenticationPrincipal User user, Model model) {
        Booking booking = bookingService.findById(bookingId).orElseThrow(() -> new RuntimeException("Booking not found"));
        if (!booking.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        model.addAttribute("booking", booking);
        model.addAttribute("feedback", new Feedback());
        return "feedback/form";
    }
    @PostMapping
    public String submitFeedback(@Valid @ModelAttribute Feedback feedback, BindingResult result,
                                 @AuthenticationPrincipal User user, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "feedback/form";
        }
        feedback.setUser(user);
        try {
            feedbackService.submitFeedback(feedback);
            redirectAttributes.addFlashAttribute("success", "Feedback submitted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/bookings";
    }
}
