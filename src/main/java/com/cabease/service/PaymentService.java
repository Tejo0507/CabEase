package com.cabease.service;
import com.cabease.dto.PaymentMethodDTO;
import com.cabease.dto.WalletTransactionDTO;
import com.cabease.entity.*;
import com.cabease.models.Booking;
import com.cabease.models.User;
import com.cabease.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
@Service
public class PaymentService {
    @Autowired
    private PaymentMethodRepository paymentMethodRepository;
    @Autowired
    private WalletTransactionRepository walletTransactionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private PaymentGatewayService paymentGatewayService;
    @Autowired
    private MockPaymentGatewayService mockPaymentGatewayService;
    @Autowired
    private com.cabease.config.RazorpayConfig razorpayConfig;
    @org.springframework.beans.factory.annotation.Value("${payment.gateway.mode:mock}")
    private String gatewayMode;
    @Transactional
    public PaymentMethodDTO addPaymentMethod(PaymentMethodDTO dto) {
        User user = userRepository.findById(dto.getUserId())
            .orElseThrow(() -> new RuntimeException("User not found"));
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setUser(user);
        paymentMethod.setMethodType(dto.getMethodType());
        paymentMethod.setCardLastFour(dto.getCardLastFour());
        paymentMethod.setCardBrand(dto.getCardBrand());
        paymentMethod.setUpiId(dto.getUpiId());
        paymentMethod.setCardholderName(dto.getCardholderName());
        paymentMethod.setExpiryMonth(dto.getExpiryMonth());
        paymentMethod.setExpiryYear(dto.getExpiryYear());
        Long existingCount = paymentMethodRepository.countByUser(user);
        if (existingCount == 0) {
            paymentMethod.setIsDefault(true);
        } else {
            paymentMethod.setIsDefault(dto.getIsDefault() != null ? dto.getIsDefault() : false);
        }
        if ("WALLET".equals(dto.getMethodType())) {
            paymentMethod.setWalletBalance(BigDecimal.ZERO);
        }
        PaymentMethod saved = paymentMethodRepository.save(paymentMethod);
        return convertToDTO(saved);
    }
    public List<PaymentMethodDTO> getUserPaymentMethods(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        List<PaymentMethod> methods = paymentMethodRepository.findByUserAndIsActiveTrue(user);
        return methods.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    @Transactional
    public void setDefaultPaymentMethod(Long methodId, Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        List<PaymentMethod> allMethods = paymentMethodRepository.findByUser(user);
        allMethods.forEach(m -> m.setIsDefault(false));
        paymentMethodRepository.saveAll(allMethods);
        PaymentMethod method = paymentMethodRepository.findById(methodId)
            .orElseThrow(() -> new RuntimeException("Payment method not found"));
        if (!method.getUser().getId().equals(userId)) {
            throw new RuntimeException("Payment method does not belong to user");
        }
        method.setIsDefault(true);
        paymentMethodRepository.save(method);
    }
    @Transactional
    public WalletTransactionDTO addMoneyToWallet(Long userId, BigDecimal amount, String description) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        PaymentMethod wallet = paymentMethodRepository.findByUserAndMethodType(user, "WALLET")
            .orElseGet(() -> {
                PaymentMethod newWallet = new PaymentMethod();
                newWallet.setUser(user);
                newWallet.setMethodType("WALLET");
                newWallet.setWalletBalance(BigDecimal.ZERO);
                newWallet.setIsActive(true);
                return paymentMethodRepository.save(newWallet);
            });
        BigDecimal oldBalance = wallet.getWalletBalance();
        BigDecimal newBalance = oldBalance.add(amount);
        wallet.setWalletBalance(newBalance);
        paymentMethodRepository.save(wallet);
        WalletTransaction transaction = new WalletTransaction();
        transaction.setUser(user);
        transaction.setTransactionType("CREDIT");
        transaction.setAmount(amount);
        transaction.setBalanceBefore(oldBalance);
        transaction.setBalanceAfter(newBalance);
        transaction.setDescription(description != null ? description : "Wallet recharge");
        transaction.setTransactionId(generateTransactionId());
        transaction.setStatus("COMPLETED");
        WalletTransaction saved = walletTransactionRepository.save(transaction);
        return convertToTransactionDTO(saved);
    }
    public BigDecimal getWalletBalance(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        Optional<PaymentMethod> wallet = paymentMethodRepository.findByUserAndMethodType(user, "WALLET");
        return wallet.map(PaymentMethod::getWalletBalance).orElse(BigDecimal.ZERO);
    }
    @Transactional
    public boolean processPayment(Long bookingId, Long paymentMethodId, BigDecimal amount) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));
        PaymentMethod method = paymentMethodRepository.findById(paymentMethodId)
            .orElseThrow(() -> new RuntimeException("Payment method not found"));
        if ("WALLET".equals(method.getMethodType())) {
            if (method.getWalletBalance().compareTo(amount) < 0) {
                throw new RuntimeException("Insufficient wallet balance");
            }
            BigDecimal oldBalance = method.getWalletBalance();
            BigDecimal newBalance = oldBalance.subtract(amount);
            method.setWalletBalance(newBalance);
            paymentMethodRepository.save(method);
            WalletTransaction transaction = new WalletTransaction();
            transaction.setUser(method.getUser());
            transaction.setTransactionType("DEBIT");
            transaction.setAmount(amount);
            transaction.setBalanceBefore(oldBalance);
            transaction.setBalanceAfter(newBalance);
            transaction.setBooking(booking);
            transaction.setDescription("Payment for booking #" + bookingId);
            transaction.setTransactionId(generateTransactionId());
            transaction.setStatus("COMPLETED");
            walletTransactionRepository.save(transaction);
            return true;
        } else if ("UPI".equals(method.getMethodType()) || "CARD".equals(method.getMethodType())) {
            return true;
        } else if ("CASH".equals(method.getMethodType())) {
            return true;
        }
        return false;
    }
    @Transactional
    public WalletTransactionDTO initiateRefund(Long bookingId, BigDecimal amount, String reason) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));
        User user = booking.getUser();
        PaymentMethod wallet = paymentMethodRepository.findByUserAndMethodType(user, "WALLET")
            .orElseGet(() -> {
                PaymentMethod newWallet = new PaymentMethod();
                newWallet.setUser(user);
                newWallet.setMethodType("WALLET");
                newWallet.setWalletBalance(BigDecimal.ZERO);
                newWallet.setIsActive(true);
                return paymentMethodRepository.save(newWallet);
            });
        BigDecimal oldBalance = wallet.getWalletBalance();
        BigDecimal newBalance = oldBalance.add(amount);
        wallet.setWalletBalance(newBalance);
        paymentMethodRepository.save(wallet);
        WalletTransaction transaction = new WalletTransaction();
        transaction.setUser(user);
        transaction.setTransactionType("REFUND");
        transaction.setAmount(amount);
        transaction.setBalanceBefore(oldBalance);
        transaction.setBalanceAfter(newBalance);
        transaction.setBooking(booking);
        transaction.setDescription(reason != null ? reason : "Refund for booking #" + bookingId);
        transaction.setTransactionId(generateTransactionId());
        transaction.setStatus("COMPLETED");
        WalletTransaction saved = walletTransactionRepository.save(transaction);
        return convertToTransactionDTO(saved);
    }
    public Page<WalletTransactionDTO> getWalletTransactions(Long userId, int page, int size) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        Pageable pageable = PageRequest.of(page, size);
        Page<WalletTransaction> transactions = walletTransactionRepository.findByUserOrderByCreatedAtDesc(user, pageable);
        return transactions.map(this::convertToTransactionDTO);
    }
    public List<WalletTransactionDTO> getRecentTransactions(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        List<WalletTransaction> transactions = walletTransactionRepository.findTop10ByUserOrderByCreatedAtDesc(user);
        return transactions.stream().map(this::convertToTransactionDTO).collect(Collectors.toList());
    }
    @Transactional
    public void deletePaymentMethod(Long methodId, Long userId) {
        PaymentMethod method = paymentMethodRepository.findById(methodId)
            .orElseThrow(() -> new RuntimeException("Payment method not found"));
        if (!method.getUser().getId().equals(userId)) {
            throw new RuntimeException("Payment method does not belong to user");
        }
        if ("WALLET".equals(method.getMethodType())) {
            throw new RuntimeException("Cannot delete wallet payment method");
        }
        method.setIsActive(false);
        paymentMethodRepository.save(method);
    }
    public com.cabease.dto.RazorpayOrderDTO createPaymentOrder(Long userId, BigDecimal amount, String paymentType) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        String receiptId = "receipt_" + System.currentTimeMillis();
        java.util.Map<String, String> notes = new java.util.HashMap<>();
        notes.put("userId", userId.toString());
        notes.put("paymentType", paymentType);
        notes.put("userName", user.getName());
        notes.put("userEmail", user.getEmail());
        java.util.Map<String, Object> orderDetails;
        String keyId;
        if ("mock".equalsIgnoreCase(gatewayMode)) {
            orderDetails = mockPaymentGatewayService.createOrder(amount, receiptId, notes);
            keyId = mockPaymentGatewayService.getMockKeyId();
        } else {
            orderDetails = paymentGatewayService.createOrder(amount, receiptId, notes);
            keyId = razorpayConfig.getRazorpayKeyId();
        }
        com.cabease.dto.RazorpayOrderDTO dto = new com.cabease.dto.RazorpayOrderDTO();
        dto.setOrderId((String) orderDetails.get("orderId"));
        dto.setAmount((BigDecimal) orderDetails.get("amount"));
        dto.setCurrency((String) orderDetails.get("currency"));
        dto.setReceipt((String) orderDetails.get("receipt"));
        dto.setStatus((String) orderDetails.get("status"));
        dto.setRazorpayKeyId(keyId);
        return dto;
    }
    @Transactional
    public WalletTransactionDTO verifyAndCompletePayment(
            String orderId,
            String paymentId,
            String signature,
            Long userId,
            BigDecimal amount,
            String paymentType) {
        boolean isValid;
        if ("mock".equalsIgnoreCase(gatewayMode)) {
            isValid = mockPaymentGatewayService.verifyPaymentSignature(orderId, paymentId, signature);
        } else {
            isValid = paymentGatewayService.verifyPaymentSignature(orderId, paymentId, signature);
        }
        if (!isValid) {
            throw new RuntimeException("Invalid payment signature");
        }
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        if ("WALLET_RECHARGE".equals(paymentType)) {
            return addMoneyToWallet(userId, amount, "Wallet recharge via " + paymentId);
        }
        WalletTransaction transaction = new WalletTransaction();
        transaction.setUser(user);
        transaction.setTransactionType("PAYMENT");
        transaction.setAmount(amount);
        transaction.setDescription("Payment via " + paymentType + " - " + paymentId);
        transaction.setTransactionId(paymentId);
        transaction.setStatus("COMPLETED");
        WalletTransaction saved = walletTransactionRepository.save(transaction);
        return convertToTransactionDTO(saved);
    }
    @Transactional
    public WalletTransactionDTO processGatewayRefund(String paymentId, BigDecimal amount, Long userId, String reason) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        java.util.Map<String, Object> refundDetails;
        if ("mock".equalsIgnoreCase(gatewayMode)) {
            refundDetails = mockPaymentGatewayService.createRefund(paymentId, amount, reason);
        } else {
            refundDetails = paymentGatewayService.createRefund(paymentId, amount, reason);
        }
        PaymentMethod wallet = paymentMethodRepository.findByUserAndMethodType(user, "WALLET")
            .orElseGet(() -> {
                PaymentMethod newWallet = new PaymentMethod();
                newWallet.setUser(user);
                newWallet.setMethodType("WALLET");
                newWallet.setWalletBalance(BigDecimal.ZERO);
                newWallet.setIsActive(true);
                return paymentMethodRepository.save(newWallet);
            });
        BigDecimal oldBalance = wallet.getWalletBalance();
        BigDecimal newBalance = oldBalance.add(amount);
        wallet.setWalletBalance(newBalance);
        paymentMethodRepository.save(wallet);
        WalletTransaction transaction = new WalletTransaction();
        transaction.setUser(user);
        transaction.setTransactionType("REFUND");
        transaction.setAmount(amount);
        transaction.setBalanceBefore(oldBalance);
        transaction.setBalanceAfter(newBalance);
        transaction.setDescription("Refund: " + reason);
        transaction.setTransactionId((String) refundDetails.get("id"));
        transaction.setStatus("COMPLETED");
        WalletTransaction saved = walletTransactionRepository.save(transaction);
        return convertToTransactionDTO(saved);
    }
    private PaymentMethodDTO convertToDTO(PaymentMethod method) {
        return new PaymentMethodDTO(
            method.getId(),
            method.getUser().getId(),
            method.getMethodType(),
            method.getCardLastFour(),
            method.getCardBrand(),
            method.getUpiId(),
            method.getWalletBalance(),
            method.getIsDefault(),
            method.getIsActive()
        );
    }
    private WalletTransactionDTO convertToTransactionDTO(WalletTransaction transaction) {
        return new WalletTransactionDTO(
            transaction.getId(),
            transaction.getUser().getId(),
            transaction.getTransactionType(),
            transaction.getAmount(),
            transaction.getBalanceBefore(),
            transaction.getBalanceAfter(),
            transaction.getDescription(),
            transaction.getTransactionId(),
            transaction.getStatus(),
            transaction.getCreatedAt()
        );
    }
    private String generateTransactionId() {
        return "TXN" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
