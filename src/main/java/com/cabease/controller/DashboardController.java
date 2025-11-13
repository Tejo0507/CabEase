package com.cabease.controller;
import com.cabease.models.User;
import com.cabease.service.DashboardService;
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
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private static final Logger log = LoggerFactory.getLogger(DashboardController.class);
    @Autowired
    private DashboardService dashboardService;
    @Autowired
    private UserService userService;
    @GetMapping
    public ResponseEntity<Map<String, Object>> getDashboard(Authentication auth) {
        try {
            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createResponse(false, "User not authenticated", null));
            }
            String email = auth.getName();
            User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
            Map<String, Object> dashboardData = dashboardService.getDashboardData(user.getId());
            return ResponseEntity.ok(createResponse(true, "Dashboard data fetched successfully", dashboardData));
        } catch (Exception e) {
            log.error("Error fetching dashboard data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createResponse(false, "Error: " + e.getMessage(), null));
        }
    }
    @GetMapping("/quick-stats")
    public ResponseEntity<Map<String, Object>> getQuickStats(Authentication auth) {
        try {
            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createResponse(false, "User not authenticated", null));
            }
            String email = auth.getName();
            User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
            Map<String, Object> stats = dashboardService.getQuickStats(user.getId());
            return ResponseEntity.ok(createResponse(true, "Quick stats fetched successfully", stats));
        } catch (Exception e) {
            log.error("Error fetching quick stats", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createResponse(false, "Error: " + e.getMessage(), null));
        }
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
