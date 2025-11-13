package com.cabease.service;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
@Service
public class MockPaymentGatewayService {
    private static final String MOCK_KEY_ID = "mock_test_key";
    private static final String MOCK_KEY_SECRET = "mock_test_secret";
    public Map<String, Object> createOrder(BigDecimal amount, String receiptId, Map<String, String> notes) {
        String orderId = "order_mock_" + UUID.randomUUID().toString().substring(0, 14);
        Map<String, Object> response = new HashMap<>();
        response.put("orderId", orderId);
        response.put("amount", amount);
        response.put("currency", "INR");
        response.put("receipt", receiptId);
        response.put("status", "created");
        System.out.println("🎭 MOCK: Order created - " + orderId + " for ₹" + amount);
        return response;
    }
    public boolean verifyPaymentSignature(String orderId, String paymentId, String signature) {
        boolean isValid = signature != null && signature.length() > 20;
        System.out.println("🎭 MOCK: Signature verification - " + (isValid ? "✅ VALID" : "❌ INVALID"));
        return isValid;
    }
    public Map<String, Object> fetchPayment(String paymentId) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", paymentId);
        response.put("orderId", "order_mock_" + UUID.randomUUID().toString().substring(0, 10));
        response.put("amount", new BigDecimal("500.00"));
        response.put("currency", "INR");
        response.put("status", "captured");
        response.put("method", "upi");
        response.put("email", "test@example.com");
        response.put("contact", "9876543210");
        response.put("createdAt", System.currentTimeMillis() / 1000);
        System.out.println("🎭 MOCK: Payment fetched - " + paymentId);
        return response;
    }
    public Map<String, Object> createRefund(String paymentId, BigDecimal amount, String reason) {
        String refundId = "rfnd_mock_" + UUID.randomUUID().toString().substring(0, 14);
        Map<String, Object> response = new HashMap<>();
        response.put("id", refundId);
        response.put("paymentId", paymentId);
        response.put("amount", amount);
        response.put("currency", "INR");
        response.put("status", "processed");
        response.put("createdAt", System.currentTimeMillis() / 1000);
        System.out.println("🎭 MOCK: Refund created - " + refundId + " for ₹" + amount);
        return response;
    }
    public Map<String, Object> capturePayment(String paymentId, BigDecimal amount) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", paymentId);
        response.put("amount", amount);
        response.put("currency", "INR");
        response.put("status", "captured");
        System.out.println("🎭 MOCK: Payment captured - " + paymentId + " for ₹" + amount);
        return response;
    }
    public String generateMockSignature(String orderId, String paymentId) {
        try {
            String payload = orderId + "|" + paymentId;
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(payload.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            return "mock_signature_" + UUID.randomUUID().toString();
        }
    }
    public String getMockKeyId() {
        return MOCK_KEY_ID;
    }
    public boolean isMockMode() {
        return true;
    }
}
