# 🚀 Razorpay Integration - Quick Reference

## ⚡ Quick Start (5 Minutes)

### 1. Get Razorpay Test Keys
```
1. Sign up: https://dashboard.razorpay.com/signup
2. Navigate to: Settings → API Keys
3. Click: Generate Test Key
4. Copy: Key ID (rzp_test_...) and Key Secret
```

### 2. Update Configuration
```properties
# Edit: src/main/resources/application-prod.properties
razorpay.key.id=rzp_test_YOUR_KEY_ID_HERE
razorpay.key.secret=YOUR_KEY_SECRET_HERE
```

### 3. Rebuild & Run
```bash
mvn clean package -DskipTests
java -jar target/cabease-1.0.0.jar --spring.profiles.active=prod
```

### 4. Test API
```bash
curl -X POST "http://localhost:8080/api/payment-gateway/create-order?userId=1&amount=100&paymentType=WALLET_RECHARGE"
```

---

## 📋 API Quick Reference

### Create Order
```http
POST /api/payment-gateway/create-order
?userId=1
&amount=500.00
&paymentType=WALLET_RECHARGE
```

### Verify Payment
```http
POST /api/payment-gateway/verify-payment
?amount=500.00
&paymentType=WALLET_RECHARGE

Body:
{
  "orderId": "order_xxx",
  "paymentId": "pay_xxx",
  "signature": "signature_xxx",
  "userId": 1
}
```

### Process Refund
```http
POST /api/payment-gateway/refund
?paymentId=pay_xxx
&amount=500.00
&userId=1
&reason=Customer requested
```

---

## 🎨 Frontend Integration (Copy-Paste Ready)

### Add to HTML Head
```html
<script src="https://checkout.razorpay.com/v1/checkout.js"></script>
```

### Payment Function
```javascript
async function initiatePayment(amount) {
    // Step 1: Create Order
    const orderRes = await fetch(
        `/api/payment-gateway/create-order?userId=${userId}&amount=${amount}&paymentType=WALLET_RECHARGE`,
        { method: 'POST' }
    );
    const order = await orderRes.json();

    // Step 2: Show Razorpay Checkout
    const options = {
        "key": order.data.razorpayKeyId,
        "amount": order.data.amount * 100,
        "currency": "INR",
        "name": "CabEase",
        "description": "Wallet Recharge",
        "order_id": order.data.orderId,
        "handler": async function (response) {
            // Step 3: Verify Payment
            const verifyRes = await fetch(
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
            const verify = await verifyRes.json();
            
            if (verify.success) {
                alert('Payment successful!');
                location.reload();
            } else {
                alert('Verification failed: ' + verify.message);
            }
        },
        "prefill": {
            "name": userName,
            "email": userEmail
        },
        "theme": { "color": "#FFD700" }
    };

    new Razorpay(options).open();
}
```

### Button
```html
<button onclick="initiatePayment(500)">Pay ₹500</button>
```

---

## 🧪 Test Cards (Test Mode)

| Card | Number | CVV | Expiry | Result |
|------|--------|-----|--------|--------|
| Visa | 4111 1111 1111 1111 | Any | Future | ✅ Success |
| Mastercard | 5555 5555 5555 4444 | Any | Future | ✅ Success |
| Rupay | 6521 4521 4521 4521 | Any | Future | ✅ Success |

### Test UPI
- `success@razorpay` → ✅ Success
- `failure@razorpay` → ❌ Failure

---

## 🔧 Troubleshooting

### Error: "Invalid Signature"
```
✓ Check razorpay.key.secret in config
✓ Ensure no spaces in properties
✓ Verify signature parameter is correct
```

### Error: "Order Creation Failed"
```
✓ Check razorpay.key.id in config
✓ Test health endpoint first
✓ Verify network connectivity
```

### Payment Not Reflecting
```
✓ Check browser console for errors
✓ Verify verification API call succeeds
✓ Check transaction in Razorpay dashboard
```

---

## 📊 File Locations

| Component | File Path |
|-----------|-----------|
| Config | `src/main/java/com/cabease/config/RazorpayConfig.java` |
| Service | `src/main/java/com/cabease/service/PaymentGatewayService.java` |
| Controller | `src/main/java/com/cabease/controller/PaymentGatewayController.java` |
| Properties | `src/main/resources/application-prod.properties` |
| UI | `src/main/resources/templates/wallet.html` |

---

## 🎯 Common Tasks

### Check Gateway Status
```bash
curl http://localhost:8080/api/payment-gateway/health
```

### Test Order Creation
```bash
curl -X POST "http://localhost:8080/api/payment-gateway/create-order?userId=1&amount=100&paymentType=WALLET_RECHARGE"
```

### Check Wallet Balance
```bash
curl http://localhost:8080/api/payments/wallet/balance/1
```

### View Transactions
```bash
curl http://localhost:8080/api/payments/wallet/transactions/1?page=0&size=10
```

---

## 🚨 Important Notes

⚠️ **Never expose Key Secret** on frontend  
⚠️ **Always verify signatures** on backend  
⚠️ **Use HTTPS** in production  
⚠️ **Test thoroughly** before going live  
⚠️ **Monitor payments** in Razorpay dashboard  

---

## 📚 Full Documentation

- Complete Guide: `RAZORPAY_INTEGRATION_GUIDE.md`
- Implementation Summary: `PAYMENT_GATEWAY_SUMMARY.md`
- Payment API Docs: `PAYMENT_SYSTEM_COMPLETE.md`
- UI Documentation: `PAYMENT_UI_DOCUMENTATION.md`

---

## ✅ Checklist

Before Testing:
- [ ] Get Razorpay test keys
- [ ] Update application-prod.properties
- [ ] Rebuild application
- [ ] Start application

Before Production:
- [ ] Complete Razorpay KYC
- [ ] Generate live keys
- [ ] Update configuration
- [ ] Test with real cards
- [ ] Set up webhooks
- [ ] Enable monitoring

---

**Status:** ✅ Backend Complete | ⏸️ Frontend Integration Pending  
**Next:** Add JavaScript integration to wallet.html
