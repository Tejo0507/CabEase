package com.cabease.repository;
import com.cabease.models.Booking;
import com.cabease.models.BookingStatus;
import com.cabease.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUser(User user);
    List<Booking> findByUserAndStatus(User user, BookingStatus status);
    List<Booking> findByStatus(BookingStatus status);
    @Query("SELECT b FROM Booking b WHERE b.bookingDateTime BETWEEN :start AND :end")
    List<Booking> findBookingsBetweenDates(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.cab.id = :cabId AND b.status IN ('PENDING', 'CONFIRMED', 'IN_PROGRESS')")
    long countActiveBookingsByCab(@Param("cabId") Long cabId);
    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId AND b.status IN ('CONFIRMED', 'IN_PROGRESS') ORDER BY b.bookingDateTime DESC")
    List<Booking> findActiveBookingsByUser(@Param("userId") Long userId);
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Booking b WHERE b.user.id = :userId AND b.status IN ('CONFIRMED', 'IN_PROGRESS')")
    boolean hasActiveBooking(@Param("userId") Long userId);
    @Query("SELECT b FROM Booking b LEFT JOIN FETCH b.cab c LEFT JOIN FETCH c.driver WHERE b.id = :bookingId")
    Optional<Booking> findByIdWithCabAndDriver(@Param("bookingId") Long bookingId);
}
