package com.cabease.services;
import com.cabease.models.Booking;
import com.cabease.models.Feedback;
import com.cabease.models.User;
import com.cabease.repository.FeedbackRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
@Service
@Transactional
@Slf4j
public class FeedbackService {
    @Autowired
    private FeedbackRepository feedbackRepository;
    public List<Feedback> findAllFeedback() {
        return feedbackRepository.findAll();
    }
    public List<Feedback> findFeedbackByUser(User user) {
        return feedbackRepository.findByUser(user);
    }
    public Optional<Feedback> findByUserAndBooking(User user, Booking booking) {
        return feedbackRepository.findByUserAndBooking(user, booking);
    }
    public Feedback submitFeedback(Feedback feedback) {
        if (feedbackRepository.findByUserAndBooking(feedback.getUser(), feedback.getBooking()).isPresent()) {
            throw new RuntimeException("Feedback already submitted for this booking");
        }
        log.info("Submitting feedback for booking {}", feedback.getBooking().getId());
        return feedbackRepository.save(feedback);
    }
    public Double getAverageRatingForCab(Long cabId) {
        return feedbackRepository.findAverageRatingByCab(cabId);
    }
    public Feedback updateFeedback(Feedback feedback) {
        log.info("Updating feedback {}", feedback.getId());
        return feedbackRepository.save(feedback);
    }
    public void deleteFeedback(Long id) {
        log.info("Deleting feedback {}", id);
        feedbackRepository.deleteById(id);
    }
}
