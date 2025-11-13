package com.cabease.repository;
import com.cabease.entity.RideSchedule;
import com.cabease.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
@Repository
public interface RideScheduleRepository extends JpaRepository<RideSchedule, Long> {
    List<RideSchedule> findByUserOrderByScheduledTimeDesc(User user);
    @Query("SELECT rs FROM RideSchedule rs WHERE rs.user = :user " +
           "AND rs.status IN ('SCHEDULED', 'CONFIRMED') " +
           "AND rs.scheduledTime > :now " +
           "ORDER BY rs.scheduledTime ASC")
    List<RideSchedule> findUpcomingRidesByUser(@Param("user") User user, @Param("now") LocalDateTime now);
    @Query("SELECT rs FROM RideSchedule rs WHERE rs.user = :user " +
           "AND (rs.status IN ('COMPLETED', 'CANCELLED') " +
           "OR rs.scheduledTime < :now) " +
           "ORDER BY rs.scheduledTime DESC")
    List<RideSchedule> findPastRidesByUser(@Param("user") User user, @Param("now") LocalDateTime now);
    List<RideSchedule> findByStatusOrderByScheduledTimeAsc(String status);
    List<RideSchedule> findByUserAndStatusOrderByScheduledTimeDesc(User user, String status);
    @Query("SELECT COUNT(rs) FROM RideSchedule rs WHERE rs.user = :user " +
           "AND rs.status IN ('SCHEDULED', 'CONFIRMED') " +
           "AND rs.scheduledTime > :now")
    Long countUpcomingRidesByUser(@Param("user") User user, @Param("now") LocalDateTime now);
    @Query("SELECT rs FROM RideSchedule rs WHERE rs.user = :user " +
           "AND rs.scheduledTime BETWEEN :startTime AND :endTime " +
           "ORDER BY rs.scheduledTime ASC")
    List<RideSchedule> findByUserAndTimeRange(
        @Param("user") User user,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
    @Query("SELECT rs FROM RideSchedule rs WHERE rs.user = :user " +
           "AND DATE(rs.scheduledTime) = DATE(:today) " +
           "AND rs.status IN ('SCHEDULED', 'CONFIRMED') " +
           "ORDER BY rs.scheduledTime ASC")
    List<RideSchedule> findTodayRidesByUser(@Param("user") User user, @Param("today") LocalDateTime today);
}
