package com.cabease.service;
import com.razorpay.Order;
import com.razorpay.Payment;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
@Service
public class PaymentGatewayService {
    @Autowired
    private RazorpayClient razorpayClient;
    @Autowired
    private com.cabease.config.RazorpayConfig razorpayConfig;
    public Map<String, Object> createOrder(BigDecimal amount, String receiptId, Map<String, String> notes) {
        try {
            int amountInPaise = amount.multiply(new BigDecimal("100")).intValue();
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountInPaise);
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", receiptId);
            if (notes != null && !notes.isEmpty()) {
                orderRequest.put("notes", new JSONObject(notes));
            }
            Order order = razorpayClient.orders.create(orderRequest);
            Map<String, Object> response = new HashMap<>();
            response.put("orderId", order.get("id"));
            response.put("amount", amount);
            response.put("currency", order.get("currency"));
            response.put("receipt", order.get("receipt"));
            response.put("status", order.get("status"));
            return response;
        } catch (RazorpayException e) {
            throw new RuntimeException("Failed to create Razorpay order: " + e.getMessage(), e);
        }
    }
    public boolean verifyPaymentSignature(String orderId, String paymentId, String signature) {
        try {
            JSONObject attributes = new JSONObject();
            attributes.put("razorpay_order_id", orderId);
            attributes.put("razorpay_payment_id", paymentId);
            attributes.put("razorpay_signature", signature);
            return com.razorpay.Utils.verifyPaymentSignature(attributes, razorpayConfig.getRazorpayKeySecret());
        } catch (RazorpayException e) {
            return false;
        }
    }
    public Map<String, Object> fetchPayment(String paymentId) {
        try {
            Payment payment = razorpayClient.payments.fetch(paymentId);
            Map<String, Object> response = new HashMap<>();
            response.put("id", payment.get("id"));
            response.put("orderId", payment.get("order_id"));
            response.put("amount", new BigDecimal(payment.get("amount").toString()).divide(new BigDecimal("100")));
            response.put("currency", payment.get("currency"));
            response.put("status", payment.get("status"));
            response.put("method", payment.get("method"));
            response.put("email", payment.get("email"));
            response.put("contact", payment.get("contact"));
            response.put("createdAt", payment.get("created_at"));
            return response;
        } catch (RazorpayException e) {
            throw new RuntimeException("Failed to fetch payment: " + e.getMessage(), e);
        }
    }
    public Map<String, Object> createRefund(String paymentId, BigDecimal amount, String reason) {
        try {
            JSONObject refundRequest = new JSONObject();
            if (amount != null) {
                int amountInPaise = amount.multiply(new BigDecimal("100")).intValue();
                refundRequest.put("amount", amountInPaise);
            }
            if (reason != null) {
                JSONObject notes = new JSONObject();
                notes.put("reason", reason);
                refundRequest.put("notes", notes);
            }
            com.razorpay.Refund refund = razorpayClient.payments.refund(paymentId, refundRequest);
            Map<String, Object> response = new HashMap<>();
            response.put("id", refund.get("id"));
            response.put("paymentId", refund.get("payment_id"));
            response.put("amount", new BigDecimal(refund.get("amount").toString()).divide(new BigDecimal("100")));
            response.put("currency", refund.get("currency"));
            response.put("status", refund.get("status"));
            response.put("createdAt", refund.get("created_at"));
            return response;
        } catch (RazorpayException e) {
            throw new RuntimeException("Failed to create refund: " + e.getMessage(), e);
        }
    }
    public Map<String, Object> capturePayment(String paymentId, BigDecimal amount) {
        try {
            int amountInPaise = amount.multiply(new BigDecimal("100")).intValue();
            JSONObject captureRequest = new JSONObject();
            captureRequest.put("amount", amountInPaise);
            Payment capturedPayment = razorpayClient.payments.capture(paymentId, captureRequest);
            Map<String, Object> response = new HashMap<>();
            response.put("id", capturedPayment.get("id"));
            response.put("amount", amount);
            response.put("currency", capturedPayment.get("currency"));
            response.put("status", capturedPayment.get("status"));
            return response;
        } catch (RazorpayException e) {
            throw new RuntimeException("Failed to capture payment: " + e.getMessage(), e);
        }
    }
}
