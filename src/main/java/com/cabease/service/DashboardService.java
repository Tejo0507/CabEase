package com.cabease.service;
import com.cabease.entity.UserProfile;
import com.cabease.models.Booking;
import com.cabease.models.BookingStatus;
import com.cabease.models.User;
import com.cabease.repository.BookingRepository;
import com.cabease.service.UserProfileService;
import com.cabease.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
@Service
@Transactional
public class DashboardService {
    private static final Logger log = LoggerFactory.getLogger(DashboardService.class);
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserProfileService userProfileService;
    @Autowired
    private PaymentService paymentService;
    @Transactional(readOnly = true)
    public Map<String, Object> getDashboardData(Long userId) {
        log.debug("Fetching dashboard data for user ID: {}", userId);
        Map<String, Object> dashboard = new HashMap<>();
        User user = new User();
        user.setId(userId);
        List<Booking> allBookings = bookingRepository.findByUser(user);
        dashboard.put("overview", getOverviewStats(allBookings, userId));
        dashboard.put("recentActivity", getRecentActivity(allBookings));
        dashboard.put("spendingAnalytics", getSpendingAnalytics(allBookings));
        dashboard.put("monthlyTrends", getMonthlyTrends(allBookings));
        dashboard.put("popularRoutes", getPopularRoutes(allBookings));
        dashboard.put("ridesByStatus", getRidesByStatus(allBookings));
        dashboard.put("timeAnalytics", getTimeAnalytics(allBookings));
        log.info("Dashboard data prepared for user ID: {}", userId);
        return dashboard;
    }
    private Map<String, Object> getOverviewStats(List<Booking> bookings, Long userId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRides", bookings.size());
        long completed = bookings.stream()
            .filter(b -> b.getStatus() == BookingStatus.COMPLETED)
            .count();
        stats.put("completedRides", completed);
        long cancelled = bookings.stream()
            .filter(b -> b.getStatus() == BookingStatus.CANCELLED)
            .count();
        stats.put("cancelledRides", cancelled);
        long inProgress = bookings.stream()
            .filter(b -> b.getStatus() == BookingStatus.IN_PROGRESS ||
                        b.getStatus() == BookingStatus.CONFIRMED ||
                        b.getStatus() == BookingStatus.PENDING)
            .count();
        stats.put("activeRides", inProgress);
        BigDecimal totalSpent = bookings.stream()
            .filter(b -> b.getStatus() == BookingStatus.COMPLETED)
            .map(Booking::getTotalFare)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("totalSpent", totalSpent);
        BigDecimal avgFare = completed > 0 ?
            totalSpent.divide(BigDecimal.valueOf(completed), 2, BigDecimal.ROUND_HALF_UP) :
            BigDecimal.ZERO;
        stats.put("averageFare", avgFare);
        BigDecimal totalDistance = bookings.stream()
            .filter(b -> b.getStatus() == BookingStatus.COMPLETED)
            .map(Booking::getDistance)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("totalDistance", totalDistance);
        try {
            Optional<UserProfile> profileOpt = userProfileService.getProfile(userId);
            if (profileOpt.isPresent()) {
                UserProfile profile = profileOpt.get();
                stats.put("loyaltyPoints", profile.getLoyaltyPoints());
                stats.put("membershipTier", profile.getMembershipTier());
            }
        } catch (Exception e) {
            log.warn("Could not fetch user profile stats: {}", e.getMessage());
        }
        try {
            BigDecimal walletBalance = paymentService.getWalletBalance(userId);
            stats.put("walletBalance", walletBalance);
        } catch (Exception e) {
            log.warn("Could not fetch wallet balance: {}", e.getMessage());
            stats.put("walletBalance", BigDecimal.ZERO);
        }
        return stats;
    }
    private List<Map<String, Object>> getRecentActivity(List<Booking> bookings) {
        return bookings.stream()
            .sorted(Comparator.comparing(Booking::getBookingDateTime).reversed())
            .limit(10)
            .map(this::convertBookingToActivity)
            .collect(Collectors.toList());
    }
    private Map<String, Object> convertBookingToActivity(Booking booking) {
        Map<String, Object> activity = new HashMap<>();
        activity.put("id", booking.getId());
        activity.put("date", booking.getBookingDateTime());
        activity.put("status", booking.getStatus().name());
        activity.put("statusDisplay", booking.getStatus().getDisplayName());
        activity.put("pickupLocation", booking.getPickupLocation());
        activity.put("dropoffLocation", booking.getDropLocation());
        activity.put("fare", booking.getTotalFare());
        activity.put("distance", booking.getDistance());
        if (booking.getCab() != null) {
            activity.put("vehicleType", booking.getCab().getVehicleType().name());
            activity.put("vehicleNumber", booking.getCab().getCabNumber());
        } else if (booking.getPreferredVehicleType() != null) {
            activity.put("vehicleType", booking.getPreferredVehicleType().name());
        }
        return activity;
    }
    private Map<String, Object> getSpendingAnalytics(List<Booking> bookings) {
        Map<String, Object> analytics = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekAgo = now.minus(7, ChronoUnit.DAYS);
        LocalDateTime monthAgo = now.minus(30, ChronoUnit.DAYS);
        BigDecimal weekSpending = bookings.stream()
            .filter(b -> b.getStatus() == BookingStatus.COMPLETED)
            .filter(b -> b.getBookingDateTime().isAfter(weekAgo))
            .map(Booking::getTotalFare)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        analytics.put("thisWeek", weekSpending);
        BigDecimal monthSpending = bookings.stream()
            .filter(b -> b.getStatus() == BookingStatus.COMPLETED)
            .filter(b -> b.getBookingDateTime().isAfter(monthAgo))
            .map(Booking::getTotalFare)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        analytics.put("thisMonth", monthSpending);
        Map<String, BigDecimal> byVehicle = bookings.stream()
            .filter(b -> b.getStatus() == BookingStatus.COMPLETED)
            .filter(b -> b.getCab() != null)
            .collect(Collectors.groupingBy(
                b -> b.getCab().getVehicleType().name(),
                Collectors.reducing(
                    BigDecimal.ZERO,
                    b -> b.getTotalFare() != null ? b.getTotalFare() : BigDecimal.ZERO,
                    BigDecimal::add
                )
            ));
        analytics.put("byVehicleType", byVehicle);
        return analytics;
    }
    private List<Map<String, Object>> getMonthlyTrends(List<Booking> bookings) {
        List<Map<String, Object>> trends = new ArrayList<>();
        LocalDate now = LocalDate.now();
        for (int i = 5; i >= 0; i--) {
            YearMonth month = YearMonth.from(now.minusMonths(i));
            LocalDateTime monthStart = month.atDay(1).atStartOfDay();
            LocalDateTime monthEnd = month.atEndOfMonth().atTime(23, 59, 59);
            List<Booking> monthBookings = bookings.stream()
                .filter(b -> b.getBookingDateTime().isAfter(monthStart) &&
                           b.getBookingDateTime().isBefore(monthEnd))
                .collect(Collectors.toList());
            long completedCount = monthBookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.COMPLETED)
                .count();
            BigDecimal monthTotal = monthBookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.COMPLETED)
                .map(Booking::getTotalFare)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            Map<String, Object> trend = new HashMap<>();
            trend.put("month", month.getMonth().name());
            trend.put("year", month.getYear());
            trend.put("totalRides", monthBookings.size());
            trend.put("completedRides", completedCount);
            trend.put("totalSpent", monthTotal);
            trends.add(trend);
        }
        return trends;
    }
    private List<Map<String, Object>> getPopularRoutes(List<Booking> bookings) {
        Map<String, Long> routeCounts = bookings.stream()
            .filter(b -> b.getStatus() == BookingStatus.COMPLETED)
            .collect(Collectors.groupingBy(
                b -> b.getPickupLocation() + " → " + b.getDropLocation(),
                Collectors.counting()
            ));
        return routeCounts.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(5)
            .map(entry -> {
                String[] locations = entry.getKey().split(" → ");
                Map<String, Object> route = new HashMap<>();
                route.put("pickup", locations[0]);
                route.put("dropoff", locations.length > 1 ? locations[1] : "");
                route.put("count", entry.getValue());
                return route;
            })
            .collect(Collectors.toList());
    }
    private Map<String, Long> getRidesByStatus(List<Booking> bookings) {
        return bookings.stream()
            .collect(Collectors.groupingBy(
                b -> b.getStatus().name(),
                Collectors.counting()
            ));
    }
    private Map<String, Object> getTimeAnalytics(List<Booking> bookings) {
        Map<String, Object> analytics = new HashMap<>();
        Map<String, Long> byDayOfWeek = bookings.stream()
            .collect(Collectors.groupingBy(
                b -> b.getBookingDateTime().getDayOfWeek().name(),
                Collectors.counting()
            ));
        analytics.put("byDayOfWeek", byDayOfWeek);
        Map<Integer, Long> byHour = bookings.stream()
            .collect(Collectors.groupingBy(
                b -> b.getBookingDateTime().getHour(),
                Collectors.counting()
            ));
        analytics.put("byHour", byHour);
        return analytics;
    }
    @Transactional(readOnly = true)
    public Map<String, Object> getQuickStats(Long userId) {
        User user = new User();
        user.setId(userId);
        List<Booking> bookings = bookingRepository.findByUser(user);
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRides", bookings.size());
        stats.put("completedRides", bookings.stream()
            .filter(b -> b.getStatus() == BookingStatus.COMPLETED)
            .count());
        BigDecimal totalSpent = bookings.stream()
            .filter(b -> b.getStatus() == BookingStatus.COMPLETED)
            .map(Booking::getTotalFare)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("totalSpent", totalSpent);
        return stats;
    }
}
