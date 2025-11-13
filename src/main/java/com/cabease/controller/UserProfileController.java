package com.cabease.controller;
import com.cabease.entity.UserProfile;
import com.cabease.models.User;
import com.cabease.service.UserProfileService;
import com.cabease.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
@RestController
@RequestMapping("/api/profile")
public class UserProfileController {
    private static final Logger log = LoggerFactory.getLogger(UserProfileController.class);
    @Autowired
    private UserProfileService userProfileService;
    @Autowired
    private UserService userService;
    @GetMapping
    public ResponseEntity<Map<String, Object>> getCurrentUserProfile(Authentication auth) {
        try {
            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createResponse(false, "User not authenticated", null));
            }
            String email = auth.getName();
            User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
            Optional<UserProfile> profileOpt = userProfileService.getProfile(user.getId());
            if (profileOpt.isPresent()) {
                Map<String, Object> profileData = convertProfileToMap(profileOpt.get());
                return ResponseEntity.ok(createResponse(true, "Profile fetched successfully", profileData));
            } else {
                UserProfile newProfile = userProfileService.createProfile(user.getId(), new HashMap<>());
                Map<String, Object> profileData = convertProfileToMap(newProfile);
                return ResponseEntity.ok(createResponse(true, "Profile created successfully", profileData));
            }
        } catch (Exception e) {
            log.error("Error fetching profile", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createResponse(false, "Error: " + e.getMessage(), null));
        }
    }
    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getUserProfile(@PathVariable Long userId, Authentication auth) {
        try {
            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createResponse(false, "User not authenticated", null));
            }
            Optional<UserProfile> profileOpt = userProfileService.getProfile(userId);
            if (profileOpt.isPresent()) {
                Map<String, Object> profileData = convertProfileToMap(profileOpt.get());
                return ResponseEntity.ok(createResponse(true, "Profile fetched successfully", profileData));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createResponse(false, "Profile not found", null));
            }
        } catch (Exception e) {
            log.error("Error fetching profile for user {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createResponse(false, "Error: " + e.getMessage(), null));
        }
    }
    @PostMapping
    public ResponseEntity<Map<String, Object>> createProfile(
        @RequestBody Map<String, Object> profileData,
        Authentication auth
    ) {
        try {
            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createResponse(false, "User not authenticated", null));
            }
            String email = auth.getName();
            User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
            UserProfile profile = userProfileService.createProfile(user.getId(), profileData);
            Map<String, Object> responseData = convertProfileToMap(profile);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(createResponse(true, "Profile created successfully", responseData));
        } catch (Exception e) {
            log.error("Error creating profile", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createResponse(false, "Error: " + e.getMessage(), null));
        }
    }
    @PutMapping
    public ResponseEntity<Map<String, Object>> updateCurrentUserProfile(
        @RequestBody Map<String, Object> updates,
        Authentication auth
    ) {
        try {
            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createResponse(false, "User not authenticated", null));
            }
            String email = auth.getName();
            User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
            UserProfile profile = userProfileService.updateProfile(user.getId(), updates);
            Map<String, Object> profileData = convertProfileToMap(profile);
            return ResponseEntity.ok(createResponse(true, "Profile updated successfully", profileData));
        } catch (Exception e) {
            log.error("Error updating profile", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createResponse(false, "Error: " + e.getMessage(), null));
        }
    }
    @PostMapping("/photo")
    public ResponseEntity<Map<String, Object>> updateProfilePhoto(
        @RequestBody Map<String, String> photoData,
        Authentication auth
    ) {
        try {
            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createResponse(false, "User not authenticated", null));
            }
            String email = auth.getName();
            User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
            String photoUrl = photoData.get("photoUrl");
            if (photoUrl == null || photoUrl.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(createResponse(false, "Photo URL is required", null));
            }
            UserProfile profile = userProfileService.updateProfilePhoto(user.getId(), photoUrl);
            Map<String, Object> profileData = convertProfileToMap(profile);
            return ResponseEntity.ok(createResponse(true, "Profile photo updated successfully", profileData));
        } catch (Exception e) {
            log.error("Error updating profile photo", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createResponse(false, "Error: " + e.getMessage(), null));
        }
    }
    @PutMapping("/theme")
    public ResponseEntity<Map<String, Object>> updateTheme(
        @RequestBody Map<String, String> themeData,
        Authentication auth
    ) {
        try {
            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createResponse(false, "User not authenticated", null));
            }
            String email = auth.getName();
            User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
            String theme = themeData.get("theme");
            if (theme == null || theme.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(createResponse(false, "Theme is required", null));
            }
            UserProfile profile = userProfileService.updateThemePreference(user.getId(), theme);
            Map<String, Object> data = new HashMap<>();
            data.put("theme", profile.getThemePreference());
            return ResponseEntity.ok(createResponse(true, "Theme updated successfully", data));
        } catch (Exception e) {
            log.error("Error updating theme", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createResponse(false, "Error: " + e.getMessage(), null));
        }
    }
    @PutMapping("/notifications")
    public ResponseEntity<Map<String, Object>> updateNotifications(
        @RequestBody Map<String, String> notifData,
        Authentication auth
    ) {
        try {
            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createResponse(false, "User not authenticated", null));
            }
            String email = auth.getName();
            User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
            String preferences = notifData.get("preferences");
            if (preferences == null || preferences.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(createResponse(false, "Preferences are required", null));
            }
            UserProfile profile = userProfileService.updateNotificationPreferences(user.getId(), preferences);
            Map<String, Object> data = new HashMap<>();
            data.put("notificationPreferences", profile.getNotificationPreferences());
            return ResponseEntity.ok(createResponse(true, "Notification preferences updated successfully", data));
        } catch (Exception e) {
            log.error("Error updating notifications", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createResponse(false, "Error: " + e.getMessage(), null));
        }
    }
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getProfileStats(Authentication auth) {
        try {
            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createResponse(false, "User not authenticated", null));
            }
            String email = auth.getName();
            User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
            Map<String, Object> stats = userProfileService.getProfileStats(user.getId());
            return ResponseEntity.ok(createResponse(true, "Stats fetched successfully", stats));
        } catch (Exception e) {
            log.error("Error fetching profile stats", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createResponse(false, "Error: " + e.getMessage(), null));
        }
    }
    @PostMapping("/loyalty/add")
    public ResponseEntity<Map<String, Object>> addLoyaltyPoints(
        @RequestBody Map<String, Object> pointsData,
        Authentication auth
    ) {
        try {
            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createResponse(false, "User not authenticated", null));
            }
            String email = auth.getName();
            User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
            int points = (Integer) pointsData.get("points");
            UserProfile profile = userProfileService.addLoyaltyPoints(user.getId(), points);
            Map<String, Object> data = new HashMap<>();
            data.put("loyaltyPoints", profile.getLoyaltyPoints());
            data.put("membershipTier", profile.getMembershipTier());
            return ResponseEntity.ok(createResponse(true, "Loyalty points added successfully", data));
        } catch (Exception e) {
            log.error("Error adding loyalty points", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createResponse(false, "Error: " + e.getMessage(), null));
        }
    }
    private Map<String, Object> convertProfileToMap(UserProfile profile) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", profile.getId());
        map.put("userId", profile.getUser().getId());
        map.put("userName", profile.getUser().getName());
        map.put("userEmail", profile.getUser().getEmail());
        map.put("photoUrl", profile.getPhotoUrl());
        map.put("phoneNumber", profile.getPhoneNumber());
        map.put("alternatePhone", profile.getAlternatePhone());
        map.put("dateOfBirth", profile.getDateOfBirth());
        map.put("gender", profile.getGender());
        map.put("addressLine1", profile.getAddressLine1());
        map.put("addressLine2", profile.getAddressLine2());
        map.put("city", profile.getCity());
        map.put("state", profile.getState());
        map.put("zipCode", profile.getZipCode());
        map.put("country", profile.getCountry());
        map.put("emergencyContactName", profile.getEmergencyContactName());
        map.put("emergencyContactPhone", profile.getEmergencyContactPhone());
        map.put("emergencyContactRelation", profile.getEmergencyContactRelation());
        map.put("preferredLanguage", profile.getPreferredLanguage());
        map.put("themePreference", profile.getThemePreference());
        map.put("notificationPreferences", profile.getNotificationPreferences());
        map.put("totalBookings", profile.getTotalBookings());
        map.put("totalSpent", profile.getTotalSpent());
        map.put("loyaltyPoints", profile.getLoyaltyPoints());
        map.put("membershipTier", profile.getMembershipTier());
        map.put("createdAt", profile.getCreatedAt());
        map.put("updatedAt", profile.getUpdatedAt());
        return map;
    }
    private Map<String, Object> createResponse(boolean success, String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", message);
        if (data != null) {
            response.put("data", data);
        }
        return response;
    }
}
