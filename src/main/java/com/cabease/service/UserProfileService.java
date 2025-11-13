package com.cabease.service;
import com.cabease.entity.UserProfile;
import com.cabease.models.User;
import com.cabease.repository.UserProfileRepository;
import com.cabease.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
@Service
@Transactional
public class UserProfileService {
    private static final Logger log = LoggerFactory.getLogger(UserProfileService.class);
    @Autowired
    private UserProfileRepository userProfileRepository;
    @Autowired
    private UserRepository userRepository;
    public UserProfile createProfile(Long userId, Map<String, Object> profileData) {
        log.info("Creating profile for user ID: {}", userId);
        if (userProfileRepository.existsByUserId(userId)) {
            throw new RuntimeException("Profile already exists for user ID: " + userId);
        }
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        UserProfile profile = new UserProfile();
        profile.setUser(user);
        profile.setCreatedAt(LocalDateTime.now());
        profile.setUpdatedAt(LocalDateTime.now());
        if (profileData.containsKey("phoneNumber")) {
            profile.setPhoneNumber((String) profileData.get("phoneNumber"));
        }
        if (profileData.containsKey("gender")) {
            profile.setGender((String) profileData.get("gender"));
        }
        if (profileData.containsKey("city")) {
            profile.setCity((String) profileData.get("city"));
        }
        UserProfile savedProfile = userProfileRepository.save(profile);
        log.info("Profile created successfully for user ID: {}", userId);
        return savedProfile;
    }
    @Transactional(readOnly = true)
    public Optional<UserProfile> getProfile(Long userId) {
        log.debug("Fetching profile for user ID: {}", userId);
        return userProfileRepository.findByUserId(userId);
    }
    public UserProfile getOrCreateProfile(Long userId) {
        return getProfile(userId).orElseGet(() -> {
            log.info("Profile not found for user {}, creating new one", userId);
            return createProfile(userId, new HashMap<>());
        });
    }
    public UserProfile updateProfile(Long userId, Map<String, Object> updates) {
        log.info("Updating profile for user ID: {}", userId);
        UserProfile profile = getProfile(userId)
            .orElseThrow(() -> new RuntimeException("Profile not found for user ID: " + userId));
        if (updates.containsKey("phoneNumber")) {
            profile.setPhoneNumber((String) updates.get("phoneNumber"));
        }
        if (updates.containsKey("alternatePhone")) {
            profile.setAlternatePhone((String) updates.get("alternatePhone"));
        }
        if (updates.containsKey("dateOfBirth")) {
            profile.setDateOfBirth(java.time.LocalDate.parse((String) updates.get("dateOfBirth")));
        }
        if (updates.containsKey("gender")) {
            profile.setGender((String) updates.get("gender"));
        }
        if (updates.containsKey("addressLine1")) {
            profile.setAddressLine1((String) updates.get("addressLine1"));
        }
        if (updates.containsKey("addressLine2")) {
            profile.setAddressLine2((String) updates.get("addressLine2"));
        }
        if (updates.containsKey("city")) {
            profile.setCity((String) updates.get("city"));
        }
        if (updates.containsKey("state")) {
            profile.setState((String) updates.get("state"));
        }
        if (updates.containsKey("zipCode")) {
            profile.setZipCode((String) updates.get("zipCode"));
        }
        if (updates.containsKey("country")) {
            profile.setCountry((String) updates.get("country"));
        }
        if (updates.containsKey("emergencyContactName")) {
            profile.setEmergencyContactName((String) updates.get("emergencyContactName"));
        }
        if (updates.containsKey("emergencyContactPhone")) {
            profile.setEmergencyContactPhone((String) updates.get("emergencyContactPhone"));
        }
        if (updates.containsKey("emergencyContactRelation")) {
            profile.setEmergencyContactRelation((String) updates.get("emergencyContactRelation"));
        }
        if (updates.containsKey("preferredLanguage")) {
            profile.setPreferredLanguage((String) updates.get("preferredLanguage"));
        }
        if (updates.containsKey("themePreference")) {
            profile.setThemePreference((String) updates.get("themePreference"));
        }
        profile.setUpdatedAt(LocalDateTime.now());
        UserProfile updatedProfile = userProfileRepository.save(profile);
        log.info("Profile updated successfully for user ID: {}", userId);
        return updatedProfile;
    }
    public UserProfile updateProfilePhoto(Long userId, String photoUrl) {
        log.info("Updating profile photo for user ID: {}", userId);
        UserProfile profile = getOrCreateProfile(userId);
        profile.setPhotoUrl(photoUrl);
        profile.setUpdatedAt(LocalDateTime.now());
        return userProfileRepository.save(profile);
    }
    public UserProfile updateNotificationPreferences(Long userId, String preferences) {
        log.info("Updating notification preferences for user ID: {}", userId);
        UserProfile profile = getOrCreateProfile(userId);
        profile.setNotificationPreferences(preferences);
        profile.setUpdatedAt(LocalDateTime.now());
        return userProfileRepository.save(profile);
    }
    public UserProfile updateThemePreference(Long userId, String theme) {
        log.info("Updating theme preference for user ID: {} to {}", userId, theme);
        UserProfile profile = getOrCreateProfile(userId);
        profile.setThemePreference(theme);
        profile.setUpdatedAt(LocalDateTime.now());
        return userProfileRepository.save(profile);
    }
    public UserProfile addLoyaltyPoints(Long userId, int points) {
        log.info("Adding {} loyalty points for user ID: {}", points, userId);
        UserProfile profile = getOrCreateProfile(userId);
        profile.setLoyaltyPoints(profile.getLoyaltyPoints() + points);
        profile.setUpdatedAt(LocalDateTime.now());
        updateMembershipTier(profile);
        return userProfileRepository.save(profile);
    }
    public UserProfile updateBookingStats(Long userId, BigDecimal rideAmount) {
        log.info("Updating booking stats for user ID: {}", userId);
        UserProfile profile = getOrCreateProfile(userId);
        profile.setTotalBookings(profile.getTotalBookings() + 1);
        profile.setTotalSpent(profile.getTotalSpent().add(rideAmount));
        profile.setUpdatedAt(LocalDateTime.now());
        int pointsToAdd = rideAmount.divide(BigDecimal.TEN).intValue();
        profile.setLoyaltyPoints(profile.getLoyaltyPoints() + pointsToAdd);
        updateMembershipTier(profile);
        return userProfileRepository.save(profile);
    }
    private void updateMembershipTier(UserProfile profile) {
        int points = profile.getLoyaltyPoints();
        String newTier;
        if (points >= 10000) {
            newTier = "DIAMOND";
        } else if (points >= 5000) {
            newTier = "PLATINUM";
        } else if (points >= 2000) {
            newTier = "GOLD";
        } else {
            newTier = "SILVER";
        }
        if (!newTier.equals(profile.getMembershipTier())) {
            log.info("User ID: {} membership tier upgraded from {} to {}",
                profile.getUser().getId(), profile.getMembershipTier(), newTier);
            profile.setMembershipTier(newTier);
        }
    }
    @Transactional(readOnly = true)
    public Map<String, Object> getProfileStats(Long userId) {
        log.debug("Fetching profile stats for user ID: {}", userId);
        UserProfile profile = getProfile(userId)
            .orElseThrow(() -> new RuntimeException("Profile not found for user ID: " + userId));
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalBookings", profile.getTotalBookings());
        stats.put("totalSpent", profile.getTotalSpent());
        stats.put("loyaltyPoints", profile.getLoyaltyPoints());
        stats.put("membershipTier", profile.getMembershipTier());
        stats.put("memberSince", profile.getCreatedAt());
        return stats;
    }
    public void deleteProfile(Long userId) {
        log.info("Deleting profile for user ID: {}", userId);
        userProfileRepository.findByUserId(userId).ifPresent(profile -> {
            userProfileRepository.delete(profile);
            log.info("Profile deleted for user ID: {}", userId);
        });
    }
}
