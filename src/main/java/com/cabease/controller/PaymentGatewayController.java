package com.cabease.controller;
import com.cabease.dto.PaymentVerificationDTO;
import com.cabease.dto.RazorpayOrderDTO;
import com.cabease.dto.WalletTransactionDTO;
import com.cabease.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
@RestController
@RequestMapping("/api/payment-gateway")
public class PaymentGatewayController {
    @Autowired
    private PaymentService paymentService;
    @PostMapping("/create-order")
    public ResponseEntity<Map<String, Object>> createOrder(
            @RequestParam Long userId,
            @RequestParam BigDecimal amount,
            @RequestParam String paymentType) {
        try {
            RazorpayOrderDTO order = paymentService.createPaymentOrder(userId, amount, paymentType);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Order created successfully");
            response.put("data", order);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to create order: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    @PostMapping("/verify-payment")
    public ResponseEntity<Map<String, Object>> verifyPayment(@Valid @RequestBody PaymentVerificationDTO dto,
                                                               @RequestParam BigDecimal amount,
                                                               @RequestParam(defaultValue = "WALLET_RECHARGE") String paymentType) {
        try {
            WalletTransactionDTO transaction = paymentService.verifyAndCompletePayment(
                dto.getOrderId(),
                dto.getPaymentId(),
                dto.getSignature(),
                dto.getUserId(),
                amount,
                paymentType
            );
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Payment verified and completed successfully");
            response.put("data", transaction);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Payment verification failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    @PostMapping("/refund")
    public ResponseEntity<Map<String, Object>> processRefund(
            @RequestParam String paymentId,
            @RequestParam BigDecimal amount,
            @RequestParam Long userId,
            @RequestParam(required = false) String reason) {
        try {
            WalletTransactionDTO transaction = paymentService.processGatewayRefund(
                paymentId,
                amount,
                userId,
                reason != null ? reason : "Refund processed"
            );
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Refund processed successfully");
            response.put("data", transaction);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Refund failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Payment Gateway API is running");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
}
