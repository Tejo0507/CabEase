package com.cabease.controller;
import com.cabease.dto.PaymentMethodDTO;
import com.cabease.dto.WalletTransactionDTO;
import com.cabease.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;
    @PostMapping("/methods/add")
    public ResponseEntity<?> addPaymentMethod(@RequestBody PaymentMethodDTO dto) {
        try {
            PaymentMethodDTO saved = paymentService.addPaymentMethod(dto);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Payment method added successfully");
            response.put("data", saved);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    @GetMapping("/methods/user/{userId}")
    public ResponseEntity<?> getUserPaymentMethods(@PathVariable Long userId) {
        try {
            List<PaymentMethodDTO> methods = paymentService.getUserPaymentMethods(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", methods);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    @PutMapping("/methods/{methodId}/default")
    public ResponseEntity<?> setDefaultPaymentMethod(
            @PathVariable Long methodId,
            @RequestParam Long userId) {
        try {
            paymentService.setDefaultPaymentMethod(methodId, userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Default payment method updated");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    @DeleteMapping("/methods/{methodId}")
    public ResponseEntity<?> deletePaymentMethod(
            @PathVariable Long methodId,
            @RequestParam Long userId) {
        try {
            paymentService.deletePaymentMethod(methodId, userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Payment method deleted");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    @PostMapping("/wallet/recharge")
    public ResponseEntity<?> rechargeWallet(
            @RequestParam Long userId,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) String description) {
        try {
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("Amount must be greater than zero");
            }
            if (amount.compareTo(new BigDecimal("50000")) > 0) {
                throw new RuntimeException("Maximum recharge amount is ₹50,000");
            }
            WalletTransactionDTO transaction = paymentService.addMoneyToWallet(userId, amount, description);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Wallet recharged successfully");
            response.put("data", transaction);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    @GetMapping("/wallet/balance/{userId}")
    public ResponseEntity<?> getWalletBalance(@PathVariable Long userId) {
        try {
            BigDecimal balance = paymentService.getWalletBalance(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("balance", balance);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    @GetMapping("/wallet/transactions/{userId}")
    public ResponseEntity<?> getWalletTransactions(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<WalletTransactionDTO> transactions = paymentService.getWalletTransactions(userId, page, size);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", transactions.getContent());
            response.put("currentPage", transactions.getNumber());
            response.put("totalPages", transactions.getTotalPages());
            response.put("totalElements", transactions.getTotalElements());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    @GetMapping("/wallet/recent/{userId}")
    public ResponseEntity<?> getRecentTransactions(@PathVariable Long userId) {
        try {
            List<WalletTransactionDTO> transactions = paymentService.getRecentTransactions(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", transactions);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    @PostMapping("/process")
    public ResponseEntity<?> processPayment(
            @RequestParam Long bookingId,
            @RequestParam Long paymentMethodId,
            @RequestParam BigDecimal amount) {
        try {
            boolean success = paymentService.processPayment(bookingId, paymentMethodId, amount);
            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            response.put("message", success ? "Payment processed successfully" : "Payment failed");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    @PostMapping("/refund")
    public ResponseEntity<?> initiateRefund(
            @RequestParam Long bookingId,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) String reason) {
        try {
            WalletTransactionDTO transaction = paymentService.initiateRefund(bookingId, amount, reason);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Refund processed successfully");
            response.put("data", transaction);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
