# 🧪 Mock Payment Gateway - Complete Test Guide

## 📋 Test Setup

### Prerequisites
✅ Application built successfully  
✅ PostgreSQL running (cabease-postgres)  
✅ Application started on port 8080  
✅ Mock mode enabled (default)  

---

## 🚀 Quick Test (5 Minutes)

### 1. Start Application

```powershell
cd E:\CabEase
java "-Duser.timezone=UTC" "-Dspring.profiles.active=prod" -jar target/cabease-1.0.0.jar
```

**Expected Output:**
```
Started CabEaseApplication in ~7 seconds
=== LOGIN CREDENTIALS ===
Admin: admin@cabease.com / admin123
User:  testuser@cabease.com / user123
========================
```

---

### 2. Login to Application

**URL:** http://localhost:8080/login

**Test User:**
- Email: `testuser@cabease.com`
- Password: `user123`

---

### 3. Navigate to Wallet

**URL:** http://localhost:8080/wallet

**What You'll See:**
- 💰 Current wallet balance
- 💵 Quick recharge buttons (₹100, ₹200, ₹500, etc.)
- 📊 Transaction history
- 📈 Monthly statistics
- 💳 Activity chart

---

### 4. Test Mock Payment

**Step-by-Step:**

1. **Click "₹500" Recharge Button**
2. **Mock Payment Modal Appears:**
   - Shows: 🎭 Mock Payment Gateway
   - Displays: Amount ₹500
   - Shows: Order ID (order_mock_xxx)
   - Button: "✅ Pay ₹500"

3. **Click "Pay ₹500"**
4. **Payment Processing:**
   - Info message: "Processing payment..."
   - Backend verifies mock payment
   - Wallet balance updates

5. **Success!**
   - Success message: "✅ Payment successful! ₹500 added to wallet"
   - Balance increases by ₹500
   - Transaction appears in history
   - Statistics update

---

## 🔬 API Testing

### Test 1: Health Check

```bash
curl http://localhost:8080/api/payment-gateway/health
```

**Expected Response:**
```json
{
  "success": true,
  "message": "Payment Gateway API is running",
  "timestamp": 1699123456789
}
```

---

### Test 2: Create Mock Order

```bash
curl -X POST "http://localhost:8080/api/payment-gateway/create-order?userId=1&amount=500&paymentType=WALLET_RECHARGE"
```

**Expected Response:**
```json
{
  "success": true,
  "message": "Order created successfully",
  "data": {
    "orderId": "order_mock_a1b2c3d4e5f6",
    "amount": 500.00,
    "currency": "INR",
    "receipt": "receipt_1699123456789",
    "status": "created",
    "razorpayKeyId": "mock_test_key"
  }
}
```

**Console Output (Application Log):**
```
🎭 MOCK: Order created - order_mock_xxx for ₹500.00
```

---

### Test 3: Verify Mock Payment

```bash
curl -X POST "http://localhost:8080/api/payment-gateway/verify-payment?amount=500&paymentType=WALLET_RECHARGE" \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": "order_mock_a1b2c3d4e5f6",
    "paymentId": "pay_mock_x1y2z3a4b5c6",
    "signature": "mock_signature_valid_format_here_123456789012345678901234567890",
    "userId": 1
  }'
```

**Expected Response:**
```json
{
  "success": true,
  "message": "Payment verified and completed successfully",
  "data": {
    "id": 123,
    "userId": 1,
    "transactionType": "CREDIT",
    "amount": 500.00,
    "balanceBefore": 1000.00,
    "balanceAfter": 1500.00,
    "description": "Wallet recharge via pay_mock_x1y2z3a4b5c6",
    "transactionId": "pay_mock_x1y2z3a4b5c6",
    "status": "COMPLETED",
    "createdAt": "2024-11-05T10:30:45"
  }
}
```

**Console Output:**
```
🎭 MOCK: Signature verification - ✅ VALID
🎭 MOCK: Payment captured - pay_mock_xxx for ₹500.00
```

---

### Test 4: Check Wallet Balance

```bash
curl http://localhost:8080/api/payments/wallet/balance/1
```

**Expected Response:**
```json
{
  "success": true,
  "message": "Balance retrieved successfully",
  "data": 1500.00
}
```

---

### Test 5: View Transaction History

```bash
curl http://localhost:8080/api/payments/wallet/transactions/1?page=0&size=10
```

**Expected Response:**
```json
{
  "success": true,
  "message": "Transactions retrieved successfully",
  "data": {
    "content": [
      {
        "id": 123,
        "transactionType": "CREDIT",
        "amount": 500.00,
        "balanceBefore": 1000.00,
        "balanceAfter": 1500.00,
        "description": "Wallet recharge via pay_mock_xxx",
        "transactionId": "pay_mock_x1y2z3a4b5c6",
        "status": "COMPLETED",
        "createdAt": "2024-11-05T10:30:45"
      }
    ],
    "totalPages": 1,
    "totalElements": 1
  }
}
```

---

## 🎯 UI Testing Scenarios

### Scenario 1: Quick Recharge ✅

**Steps:**
1. Login as testuser@cabease.com
2. Go to /wallet
3. Note current balance (e.g., ₹1000)
4. Click "₹100" button
5. Mock modal appears
6. Click "Pay ₹100"
7. Success message appears
8. Balance updates to ₹1100
9. Transaction shows in history

**Expected:**
- ✅ Modal appears instantly
- ✅ Payment completes in < 1 second
- ✅ Balance updates automatically
- ✅ Transaction appears immediately
- ✅ No page refresh needed

---

### Scenario 2: Custom Amount Recharge ✅

**Steps:**
1. Enter ₹2500 in custom amount field
2. Click "Recharge Custom Amount"
3. Modal shows ₹2500
4. Complete payment
5. Balance increases by ₹2500

**Edge Cases:**
- ❌ Amount < ₹1 → Error: "Please enter valid amount"
- ❌ Amount > ₹50,000 → Error: "Maximum recharge is ₹50,000"
- ✅ Amount = ₹25,000 → Success

---

### Scenario 3: Cancel Payment ✅

**Steps:**
1. Click "₹500" recharge
2. Modal appears
3. Click "❌ Cancel"
4. Modal closes
5. Balance unchanged

**Expected:**
- ✅ Modal closes instantly
- ✅ No API call made
- ✅ Balance remains same
- ✅ No transaction recorded

---

### Scenario 4: Multiple Recharges ✅

**Steps:**
1. Recharge ₹100
2. Recharge ₹200
3. Recharge ₹500
4. Check transaction history

**Expected:**
- ✅ All 3 transactions listed
- ✅ Balance = Original + 800
- ✅ Transactions ordered by date (newest first)
- ✅ Each has unique payment ID

---

### Scenario 5: Page Refresh Test ✅

**Steps:**
1. Recharge ₹1000
2. Refresh page (F5)
3. Check balance and history

**Expected:**
- ✅ Balance persists (saved in database)
- ✅ Transaction still in history
- ✅ Data loads correctly

---

## 🔍 Console Log Verification

### What to Look For

**During Order Creation:**
```
🎭 MOCK: Order created - order_mock_abc123def456 for ₹500.00
```

**During Payment Verification:**
```
🎭 MOCK: Signature verification - ✅ VALID
```

**During Database Update:**
```
Hibernate: insert into wallet_transactions ...
```

---

## ✅ Expected Behavior

### Mock Payment Modal

**Visual Elements:**
- 🎭 Icon at top
- "Mock Payment Gateway" title in gold
- "Development Mode" subtitle
- Amount in large gold text (₹500)
- Order details box (ID, status, currency)
- Two buttons: "Pay" (gold) and "Cancel" (red)
- Info text at bottom

**Interactions:**
- Hover on "Pay" → Button lifts up, shadow increases
- Hover on "Cancel" → Background darkens
- Click "Pay" → Modal closes, payment processes
- Click "Cancel" → Modal closes, no action

---

### Success Flow

**Timeline:**
1. Click recharge (0s)
2. Order created (0.1s)
3. Modal appears (0.2s)
4. User clicks pay
5. Verification starts (0s)
6. Payment verified (0.1s)
7. Wallet updated (0.2s)
8. UI refreshes (0.3s)
9. Success message (0.4s)

**Total Time:** < 1 second

---

### Error Handling

**Scenario: Invalid Amount**
```
Input: ₹0
Output: "Please enter a valid amount (minimum ₹1)"
```

**Scenario: Amount Too High**
```
Input: ₹60,000
Output: "Maximum recharge amount is ₹50,000"
```

**Scenario: Invalid Signature**
```
Signature: "invalid"
Output: "Invalid payment signature"
```

---

## 🎭 Mock vs Real Comparison

### Mock Mode (Current)

| Feature | Behavior |
|---------|----------|
| Speed | Instant |
| Success Rate | 100% |
| Payment ID | pay_mock_timestamp |
| Order ID | order_mock_random |
| Signature | Always valid (40+ chars) |
| Console Log | 🎭 MOCK: prefix |
| External Call | None |
| Cost | Free |

### Real Mode (Future)

| Feature | Behavior |
|---------|----------|
| Speed | 2-5 seconds |
| Success Rate | 95-98% |
| Payment ID | pay_razorpay_xxx |
| Order ID | order_razorpay_xxx |
| Signature | SHA256 HMAC |
| Console Log | No prefix |
| External Call | Razorpay API |
| Cost | 2% per transaction |

---

## 🧪 Automated Test Script

Create `test-mock-payment.ps1`:

```powershell
# Test Mock Payment Gateway

Write-Host "=== Mock Payment Gateway Tests ===" -ForegroundColor Green

# Test 1: Health Check
Write-Host "`n1. Health Check..." -ForegroundColor Yellow
$health = Invoke-RestMethod -Uri "http://localhost:8080/api/payment-gateway/health" -Method GET
Write-Host "   Result: $($health.success)" -ForegroundColor $(if($health.success){"Green"}else{"Red"})

# Test 2: Create Order
Write-Host "`n2. Creating Order..." -ForegroundColor Yellow
$order = Invoke-RestMethod -Uri "http://localhost:8080/api/payment-gateway/create-order?userId=1&amount=500&paymentType=WALLET_RECHARGE" -Method POST
Write-Host "   Order ID: $($order.data.orderId)" -ForegroundColor Cyan
Write-Host "   Amount: ₹$($order.data.amount)" -ForegroundColor Green

# Test 3: Verify Payment
Write-Host "`n3. Verifying Payment..." -ForegroundColor Yellow
$verifyBody = @{
    orderId = $order.data.orderId
    paymentId = "pay_mock_test_$(Get-Date -Format 'yyyyMMddHHmmss')"
    signature = "mock_sig_valid_test_signature_12345678901234567890"
    userId = 1
} | ConvertTo-Json

$verify = Invoke-RestMethod -Uri "http://localhost:8080/api/payment-gateway/verify-payment?amount=500&paymentType=WALLET_RECHARGE" `
    -Method POST -ContentType "application/json" -Body $verifyBody
Write-Host "   Result: $($verify.success)" -ForegroundColor $(if($verify.success){"Green"}else{"Red"})

# Test 4: Check Balance
Write-Host "`n4. Checking Balance..." -ForegroundColor Yellow
$balance = Invoke-RestMethod -Uri "http://localhost:8080/api/payments/wallet/balance/1" -Method GET
Write-Host "   Balance: ₹$($balance.data)" -ForegroundColor Green

Write-Host "`n=== All Tests Complete ===" -ForegroundColor Green
```

**Run:**
```powershell
.\test-mock-payment.ps1
```

---

## 📊 Success Criteria

### ✅ Payment Gateway Working If:

1. Health endpoint returns success
2. Order creation returns mock order ID
3. Payment verification succeeds
4. Wallet balance updates correctly
5. Transaction appears in history
6. Console shows 🎭 MOCK: messages
7. No errors in application log
8. UI updates without refresh
9. Modal appears and closes smoothly
10. All amounts match expectations

---

## 🐛 Troubleshooting

### Issue 1: Modal Doesn't Appear

**Cause:** JavaScript error
**Fix:** Check browser console (F12)
**Look for:** `completeMockPayment is not defined`
**Solution:** Clear browser cache, reload page

---

### Issue 2: Payment Not Updating Balance

**Cause:** API call failed
**Fix:** Check Network tab in browser (F12)
**Look for:** Red requests, 401/403 errors
**Solution:** Ensure logged in, check authentication

---

### Issue 3: "Cannot connect to server"

**Cause:** Application not running
**Fix:** Start application
**Command:** `java -jar target/cabease-1.0.0.jar`

---

### Issue 4: Console Shows Errors

**Check:**
```
❌ java.lang.NullPointerException
```

**Solution:**
1. Check userId exists
2. Verify database connection
3. Restart application

---

## 📝 Test Checklist

Before marking tests as complete:

- [ ] Application starts without errors
- [ ] Can login with test credentials
- [ ] Wallet page loads correctly
- [ ] Health endpoint responds
- [ ] Can create mock order
- [ ] Mock modal appears on recharge
- [ ] Can complete mock payment
- [ ] Balance updates after payment
- [ ] Transaction appears in history
- [ ] Can cancel payment
- [ ] Custom amount works
- [ ] Amount validation works
- [ ] Page refresh persists data
- [ ] Multiple payments work
- [ ] Console shows 🎭 MOCK logs

---

## 🎉 Success Indicators

**You've successfully tested mock payment gateway when:**

✅ All API tests pass  
✅ UI shows mock payment modal  
✅ Payments complete instantly  
✅ Balance updates correctly  
✅ Transactions recorded properly  
✅ Console shows mock indicators  
✅ No errors in logs  
✅ User experience is smooth  

---

## 📚 Related Documentation

- **MOCK_PAYMENT_GUIDE.md** - Complete usage guide
- **PAYMENT_GATEWAY_UNBLOCKED.md** - Problem/solution overview
- **RAZORPAY_INTEGRATION_GUIDE.md** - Real gateway setup (future)
- **PAYMENT_SYSTEM_COMPLETE.md** - API documentation

---

**Test Status:** ⏸️ Ready to Test  
**Estimated Time:** 10-15 minutes for complete testing  
**Difficulty:** ⭐ Easy (no setup required)
