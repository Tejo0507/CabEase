package com.cabease.repository;
import com.cabease.models.Booking;
import com.cabease.models.Feedback;
import com.cabease.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByUser(User user);
    List<Feedback> findByBooking(Booking booking);
    Optional<Feedback> findByUserAndBooking(User user, Booking booking);
    @Query("SELECT AVG(f.rating) FROM Feedback f WHERE f.booking.cab.id = :cabId")
    Double findAverageRatingByCab(@Param("cabId") Long cabId);
}
