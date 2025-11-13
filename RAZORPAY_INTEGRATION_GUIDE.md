# 💳 Razorpay Payment Gateway Integration - Complete Guide

## 📋 Table of Contents
1. [Overview](#overview)
2. [Setup & Configuration](#setup--configuration)
3. [Architecture](#architecture)
4. [API Endpoints](#api-endpoints)
5. [Frontend Integration](#frontend-integration)
6. [Testing](#testing)
7. [Troubleshooting](#troubleshooting)

---

## 🎯 Overview

### What's Implemented
CabEase now supports **real UPI and Card payments** through Razorpay Payment Gateway:

✅ **Order Creation** - Generate Razorpay orders for payment  
✅ **Payment Verification** - Verify payment signatures securely  
✅ **Wallet Recharge** - Add money to wallet via UPI/Card  
✅ **Refund Processing** - Process refunds through gateway  
✅ **Payment Capture** - Manual payment capture support  

### Technologies
- **Razorpay Java SDK**: v1.4.3
- **Payment Methods**: UPI, Credit/Debit Cards, Netbanking
- **Security**: SHA256 signature verification
- **Currency**: INR (Indian Rupees)

---

## ⚙️ Setup & Configuration

### 1. Get Razorpay Credentials

**Test Mode (for development):**
1. Sign up at https://dashboard.razorpay.com/signup
2. Go to Settings → API Keys → Generate Test Key
3. Copy `Key ID` (starts with `rzp_test_`)
4. Copy `Key Secret`

**Live Mode (for production):**
1. Complete KYC verification
2. Generate Live keys (start with `rzp_live_`)

### 2. Configure Application

**Edit `src/main/resources/application-prod.properties`:**

```properties
# Test Mode Configuration
razorpay.key.id=rzp_test_YOUR_KEY_ID
razorpay.key.secret=YOUR_KEY_SECRET
razorpay.webhook.secret=YOUR_WEBHOOK_SECRET
razorpay.currency=INR
```

**For Production:**
```properties
# Live Mode Configuration
razorpay.key.id=rzp_live_YOUR_LIVE_KEY_ID
razorpay.key.secret=YOUR_LIVE_KEY_SECRET
razorpay.webhook.secret=YOUR_LIVE_WEBHOOK_SECRET
razorpay.currency=INR
```

### 3. Environment Variables (Recommended for Production)

```bash
export RAZORPAY_KEY_ID=rzp_live_xxxx
export RAZORPAY_KEY_SECRET=your_secret
export RAZORPAY_WEBHOOK_SECRET=your_webhook_secret
```

---

## 🏗️ Architecture

### Components Created

```
Payment Gateway Integration
├── Config
│   └── RazorpayConfig.java         # Razorpay client configuration
├── Service
│   ├── PaymentGatewayService.java  # Gateway API interactions
│   └── PaymentService.java         # Updated with gateway methods
├── Controller
│   └── PaymentGatewayController.java # Gateway REST endpoints
└── DTOs
    ├── RazorpayOrderDTO.java       # Order creation response
    └── PaymentVerificationDTO.java  # Payment verification request
```

### Flow Diagram

```
Frontend                Backend                    Razorpay
   |                       |                          |
   |-- Create Order ------>|                          |
   |                       |-- Create Order --------->|
   |                       |<-- Order ID & Details ---|
   |<-- Order Details -----|                          |
   |                       |                          |
   |-- Show Checkout ----->|                          |
   |-- User Pays --------->|                          |
   |                       |<-- Payment Success ------|
   |-- Verify Payment ---->|                          |
   |                       |-- Verify Signature ----->|
   |                       |<-- Valid ---------------|
   |                       |-- Update Database ------>|
   |<-- Success ----------|                          |
```

---

## 🔌 API Endpoints

### 1. Create Payment Order

**Endpoint:** `POST /api/payment-gateway/create-order`

**Purpose:** Create Razorpay order before showing checkout

**Request:**
```http
POST /api/payment-gateway/create-order?userId=1&amount=500.00&paymentType=WALLET_RECHARGE
```

**Response:**
```json
{
  "success": true,
  "message": "Order created successfully",
  "data": {
    "orderId": "order_MNzJkl8pQqYx4K",
    "amount": 500.00,
    "currency": "INR",
    "receipt": "receipt_1699123456789",
    "status": "created",
    "razorpayKeyId": "rzp_test_demo"
  }
}
```

**Payment Types:**
- `WALLET_RECHARGE` - Add money to wallet
- `RIDE_PAYMENT` - Pay for ride
- `BOOKING_PAYMENT` - Pay for booking

---

### 2. Verify Payment

**Endpoint:** `POST /api/payment-gateway/verify-payment`

**Purpose:** Verify payment signature after successful payment

**Request:**
```json
{
  "orderId": "order_MNzJkl8pQqYx4K",
  "paymentId": "pay_MNzKpL9qRsT5Uv",
  "signature": "9ef4dffb...signature...e3b0c44",
  "userId": 1
}
```

**Query Parameters:**
- `amount=500.00` (required)
- `paymentType=WALLET_RECHARGE` (default: WALLET_RECHARGE)

**Response:**
```json
{
  "success": true,
  "message": "Payment verified and completed successfully",
  "data": {
    "id": 123,
    "userId": 1,
    "transactionType": "CREDIT",
    "amount": 500.00,
    "balanceBefore": 1200.00,
    "balanceAfter": 1700.00,
    "description": "Wallet recharge via pay_MNzKpL9qRsT5Uv",
    "transactionId": "pay_MNzKpL9qRsT5Uv",
    "status": "COMPLETED",
    "createdAt": "2024-11-05T10:30:45"
  }
}
```

---

### 3. Process Refund

**Endpoint:** `POST /api/payment-gateway/refund`

**Purpose:** Refund a payment through Razorpay

**Request:**
```http
POST /api/payment-gateway/refund?paymentId=pay_MNzKpL9qRsT5Uv&amount=500.00&userId=1&reason=Customer requested refund
```

**Response:**
```json
{
  "success": true,
  "message": "Refund processed successfully",
  "data": {
    "id": 124,
    "userId": 1,
    "transactionType": "REFUND",
    "amount": 500.00,
    "balanceBefore": 1700.00,
    "balanceAfter": 2200.00,
    "description": "Refund: Customer requested refund",
    "transactionId": "rfnd_OpQrS5tUvWxYz",
    "status": "COMPLETED",
    "createdAt": "2024-11-05T11:00:00"
  }
}
```

---

### 4. Health Check

**Endpoint:** `GET /api/payment-gateway/health`

**Purpose:** Check if payment gateway service is running

**Response:**
```json
{
  "success": true,
  "message": "Payment Gateway API is running",
  "timestamp": 1699123456789
}
```

---

## 🎨 Frontend Integration

### Step 1: Add Razorpay Checkout Script

Add this to your HTML `<head>`:

```html
<script src="https://checkout.razorpay.com/v1/checkout.js"></script>
```

### Step 2: Implement Payment Flow

```javascript
async function initiatePayment(amount) {
    try {
        // Step 1: Create order
        const orderResponse = await fetch(
            `/api/payment-gateway/create-order?userId=${userId}&amount=${amount}&paymentType=WALLET_RECHARGE`,
            { method: 'POST' }
        );
        const orderData = await orderResponse.json();
        
        if (!orderData.success) {
            alert('Failed to create order: ' + orderData.message);
            return;
        }

        // Step 2: Show Razorpay checkout
        const options = {
            "key": orderData.data.razorpayKeyId,
            "amount": orderData.data.amount * 100, // Convert to paise
            "currency": orderData.data.currency,
            "name": "CabEase",
            "description": "Wallet Recharge",
            "order_id": orderData.data.orderId,
            "handler": async function (response) {
                // Step 3: Verify payment
                await verifyPayment(response, amount);
            },
            "prefill": {
                "name": userName,
                "email": userEmail
            },
            "theme": {
                "color": "#FFD700"
            }
        };

        const rzp = new Razorpay(options);
        rzp.open();

    } catch (error) {
        console.error('Payment error:', error);
        alert('Payment failed. Please try again.');
    }
}

async function verifyPayment(response, amount) {
    try {
        const verifyResponse = await fetch(
            `/api/payment-gateway/verify-payment?amount=${amount}&paymentType=WALLET_RECHARGE`,
            {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    orderId: response.razorpay_order_id,
                    paymentId: response.razorpay_payment_id,
                    signature: response.razorpay_signature,
                    userId: userId
                })
            }
        );

        const verifyData = await verifyResponse.json();
        
        if (verifyData.success) {
            alert('Payment successful! ₹' + amount + ' added to wallet.');
            location.reload(); // Refresh to show updated balance
        } else {
            alert('Payment verification failed: ' + verifyData.message);
        }
    } catch (error) {
        console.error('Verification error:', error);
        alert('Payment verification failed. Please contact support.');
    }
}
```

### Step 3: Trigger Payment

```html
<button onclick="initiatePayment(500)">Recharge ₹500</button>
```

---

## 🧪 Testing

### Test Cards (Test Mode Only)

| Card Number | CVV | Expiry | Result |
|------------|-----|--------|--------|
| 4111 1111 1111 1111 | Any | Future | Success |
| 4012 8888 8888 1881 | Any | Future | Success |
| 5555 5555 5555 4444 | Any | Future | Success |
| 5200 0000 0000 1096 | Any | Future | Success |

### Test UPI IDs

- `success@razorpay` - Successful payment
- `failure@razorpay` - Failed payment

### Test Flow

1. **Create Order:**
```bash
curl -X POST "http://localhost:8080/api/payment-gateway/create-order?userId=1&amount=100&paymentType=WALLET_RECHARGE"
```

2. **Simulate Payment:** Use Razorpay test cards in checkout

3. **Verify Payment:** Automatically done by frontend handler

4. **Check Balance:**
```bash
curl http://localhost:8080/api/payments/wallet/balance/1
```

---

## 🔧 Troubleshooting

### Common Issues

**1. Invalid Signature Error**
- **Cause:** Wrong API secret or signature mismatch
- **Solution:** Verify `razorpay.key.secret` matches dashboard
- **Check:** Ensure no extra spaces in configuration

**2. Order Creation Failed**
- **Cause:** Invalid API credentials
- **Solution:** Check `razorpay.key.id` is correct
- **Test:** Try health endpoint first

**3. Payment Not Reflecting**
- **Cause:** Verification not completed
- **Solution:** Check browser console for errors
- **Verify:** Transaction appears in Razorpay dashboard

**4. Webhook Not Working**
- **Cause:** Webhook URL not configured
- **Solution:** Add webhook in Razorpay dashboard
- **URL:** `https://yourdomain.com/api/webhooks/razorpay`

### Debug Mode

Enable detailed logging:

```properties
logging.level.com.cabease.service.PaymentGatewayService=DEBUG
logging.level.com.razorpay=DEBUG
```

### Razorpay Dashboard

Check payments at:
- Test: https://dashboard.razorpay.com/app/payments
- Live: https://dashboard.razorpay.com/app/payments

---

## 📊 Payment Flow Summary

### For Wallet Recharge

1. User clicks "Recharge ₹500"
2. Frontend calls `/api/payment-gateway/create-order`
3. Backend creates Razorpay order
4. Frontend shows Razorpay checkout modal
5. User pays via UPI/Card
6. Razorpay returns payment details
7. Frontend calls `/api/payment-gateway/verify-payment`
8. Backend verifies signature
9. Backend adds money to wallet
10. Transaction recorded in database
11. User sees updated balance

### For Refunds

1. Admin initiates refund
2. Backend calls `/api/payment-gateway/refund`
3. Razorpay processes refund
4. Money refunded to original payment method
5. Amount added to user's wallet
6. Refund transaction recorded

---

## 🔐 Security Best Practices

1. **Never expose Key Secret** in frontend code
2. **Always verify signatures** on backend
3. **Use HTTPS** in production
4. **Validate amounts** before creating orders
5. **Log all transactions** for audit trail
6. **Set up webhooks** for payment status updates
7. **Monitor failed payments** in dashboard
8. **Test thoroughly** before going live

---

## 📚 Resources

- [Razorpay Documentation](https://razorpay.com/docs/)
- [Razorpay Java SDK](https://github.com/razorpay/razorpay-java)
- [Test Cards & Credentials](https://razorpay.com/docs/payments/payments/test-card-details/)
- [Webhook Guide](https://razorpay.com/docs/webhooks/)
- [API Reference](https://razorpay.com/docs/api/)

---

## ✅ Next Steps

1. **Configure Real Keys** - Add production credentials
2. **Update UI** - Integrate Razorpay checkout in wallet.html
3. **Add Webhooks** - Create webhook handler
4. **Test Thoroughly** - Test all payment scenarios
5. **Go Live** - Enable live mode after testing

---

**Status:** ✅ Backend Integration Complete  
**Test Mode:** ✅ Ready for Testing  
**Production:** ⏸️ Pending Configuration

*Last Updated: November 2024*
