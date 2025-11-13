# 🚀 CabEase Premium Features Upgrade Plan

## Overview
Transform CabEase from a basic cab booking platform into a **feature-rich, enterprise-grade ride-hailing service** while maintaining the luxury UI theme.

## 📊 Current Status
- ✅ Database schema created (V4 migration with 15+ new tables)
- ✅ Core entities created: RideSchedule, PaymentMethod, UserProfile
- ✅ Application running successfully on port 8080
- ✅ PostgreSQL database operational with 6 existing tables
- ✅ Google Maps API configured

## 🎯 Features to Implement

### 1. ✨ Enhanced Booking Dashboard (Merge Admin Features)
**Priority: HIGH | Estimated Time: 2-3 hours**

#### Tasks:
- [ ] Extract admin dashboard statistics cards (Total Bookings, Drivers, Cabs, Revenue)
- [ ] Integrate real-time booking management table into user view
- [ ] Add driver availability map from admin dashboard
- [ ] Merge vehicle fleet overview
- [ ] Create unified navigation with admin + user features
- [ ] Maintain luxury gradient theme consistency

#### Files to Modify:
- `booking-dashboard.html` (2301 lines - target file)
- Extract from `dashboard.html` (894 lines - source file)

---

### 2. 📅 Ride Scheduling System
**Priority: HIGH | Estimated Time: 3-4 hours**

#### Backend:
- [ ] Create `RideScheduleRepository.java`
- [ ] Create `RideScheduleService.java` with methods:
  - `scheduleRide(RideScheduleDTO)` - Create scheduled ride
  - `getAllScheduledRides(userId)` - Get user's schedules
  - `updateSchedule(scheduleId, updates)` - Modify schedule
  - `cancelSchedule(scheduleId, reason)` - Cancel schedule
  - `convertScheduleToBooking()` - Automatic conversion
- [ ] Create `RideScheduleController.java` REST endpoints:
  - `POST /api/schedules/create`
  - `GET /api/schedules/user/{userId}`
  - `PUT /api/schedules/{id}`
  - `DELETE /api/schedules/{id}/cancel`

#### Frontend:
- [ ] Add "Schedule Ride" modal with datetime picker
- [ ] Create scheduled rides list view
- [ ] Add reminder notifications before scheduled time
- [ ] Integrate with calendar view (weekly/monthly)

#### Database:
- ✅ Table `ride_schedules` already created

---

### 3. 🗺️ Live Ride Tracking with WebSocket
**Priority: HIGH | Estimated Time: 4-5 hours**

#### Backend:
- [ ] Add WebSocket dependency to `pom.xml`:
  ```xml
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-websocket</artifactId>
  </dependency>
  ```
- [ ] Create `WebSocketConfig.java` configuration
- [ ] Create `LocationWebSocketHandler.java`
- [ ] Create `RideTrackingService.java` with methods:
  - `startTracking(bookingId)` - Initialize tracking
  - `updateDriverLocation(driverId, lat, lng)` - Real-time updates
  - `getActiveRideLocation(bookingId)` - Get current position
  - `stopTracking(bookingId)` - End tracking
- [ ] Create `RideTrackingController.java`:
  - `POST /api/tracking/start/{bookingId}`
  - `POST /api/tracking/update` - Driver sends location
  - `GET /api/tracking/live/{bookingId}` - User gets location
  - `POST /api/tracking/stop/{bookingId}`

#### Frontend:
- [ ] Create live tracking map component with Google Maps
- [ ] Add animated car marker for driver
- [ ] Show ETA and distance remaining
- [ ] Add route polyline visualization
- [ ] Connect to WebSocket endpoint
- [ ] Real-time driver location updates (every 5 seconds)
- [ ] Add "Share Live Location" feature

#### Database:
- ✅ Table `ride_tracking` already created

---

### 4. 💰 Fare Estimation Engine
**Priority: MEDIUM | Estimated Time: 2-3 hours**

#### Backend:
- [ ] Create `FareCalculationService.java` with methods:
  - `estimateFare(pickupLat, pickupLng, dropLat, dropLng, vehicleType)` - Calculate estimate
  - `calculateActualFare(bookingId)` - Final fare after ride
  - `applyPromoCode(fareAmount, promoCode)` - Discount calculation
  - `calculateSurgePricing(location, time)` - Dynamic pricing
  - `applyNightCharges(fare, startTime)` - Night surcharge
- [ ] Create `FareEstimationController.java`:
  - `POST /api/fare/estimate` - Get fare estimate
  - `GET /api/fare/breakdown/{bookingId}` - Detailed fare breakdown
  - `POST /api/fare/apply-promo` - Apply promo code

#### Frontend:
- [ ] Add fare estimation widget before booking
- [ ] Show fare breakdown (Base + Distance + Time + Surge + Night)
- [ ] Display promo code discount
- [ ] Add fare comparison for different vehicle types
- [ ] Show estimated time and distance

#### Database:
- ✅ Table `fare_rules` already created with sample data

---

### 5. 💳 Multiple Payment Methods
**Priority: HIGH | Estimated Time: 4-5 hours**

#### Backend:
- [ ] Create `PaymentMethodRepository.java`
- [ ] Create `WalletTransactionRepository.java`
- [ ] Create `PaymentService.java` with methods:
  - `addPaymentMethod(userId, methodDTO)` - Add card/UPI
  - `getUserPaymentMethods(userId)` - Get all methods
  - `setDefaultPaymentMethod(methodId)` - Set default
  - `addMoneyToWallet(userId, amount)` - Wallet recharge
  - `processPayment(bookingId, methodId, amount)` - Process payment
  - `initiateRefund(bookingId)` - Refund to source
- [ ] Create `PaymentController.java`:
  - `POST /api/payments/methods/add` - Add payment method
  - `GET /api/payments/methods/user/{userId}` - Get methods
  - `PUT /api/payments/methods/{id}/default` - Set default
  - `POST /api/payments/wallet/recharge` - Add wallet money
  - `POST /api/payments/process` - Process payment
  - `GET /api/payments/wallet/balance/{userId}` - Get wallet balance
  - `GET /api/payments/wallet/transactions/{userId}` - Transaction history

#### Frontend:
- [ ] Create "Payment Methods" section in profile
- [ ] Add Card, UPI, Wallet options
- [ ] Wallet balance display and recharge option
- [ ] Payment method selection during booking
- [ ] Transaction history view
- [ ] Secure card input form (no storage of CVV)

#### Database:
- ✅ Tables `payment_methods` and `wallet_transactions` already created

---

### 6. 📜 Ride History with Invoices
**Priority: MEDIUM | Estimated Time: 2-3 hours**

#### Backend:
- [ ] Enhance `BookingService.java`:
  - `getRideHistory(userId, filter, sortBy)` - Paginated history
  - `getRideDetails(bookingId)` - Full ride details
  - `downloadInvoice(bookingId)` - Generate PDF invoice
  - `exportHistory(userId, format)` - Export CSV/PDF
- [ ] Create `InvoiceService.java`:
  - `generateInvoice(bookingId)` - Create invoice PDF
  - `sendInvoiceEmail(bookingId, email)` - Email invoice
- [ ] Enhance `BookingController.java`:
  - `GET /api/bookings/history` - Ride history (pagination, filters)
  - `GET /api/bookings/{id}/details` - Detailed view
  - `GET /api/bookings/{id}/invoice/download` - PDF download
  - `POST /api/bookings/{id}/invoice/email` - Email invoice

#### Frontend:
- [ ] Create "Ride History" page with filters:
  - Date range picker
  - Status filter (Completed, Cancelled, Scheduled)
  - Sort by date/amount
- [ ] Show ride details: date, route, driver, vehicle, fare, payment
- [ ] Add "Download Invoice" button per ride
- [ ] Add "Email Invoice" option
- [ ] Show monthly spending chart

#### Dependencies:
- [ ] Add PDF generation library (iText or Apache PDFBox)

---

### 7. 👤 Driver Profiles with Photos & Ratings
**Priority: MEDIUM | Estimated Time: 3-4 hours**

#### Backend:
- [ ] Create `DriverProfileRepository.java`
- [ ] Create `DriverProfileService.java` with methods:
  - `createDriverProfile(driverId, profileDTO)` - Create profile
  - `updateDriverProfile(driverId, updates)` - Update profile
  - `getDriverProfile(driverId)` - Get profile with ratings
  - `uploadDriverPhoto(driverId, photo)` - Upload photo
  - `verifyDriver(driverId)` - Admin verification
  - `getTopRatedDrivers(limit)` - Get best drivers
- [ ] Create `DriverProfileController.java`:
  - `POST /api/drivers/profile/create` - Create profile
  - `PUT /api/drivers/profile/{id}` - Update profile
  - `GET /api/drivers/profile/{id}` - View profile
  - `POST /api/drivers/profile/{id}/photo` - Upload photo
  - `GET /api/drivers/top-rated` - Top drivers

#### Frontend:
- [ ] Show driver photo and name during booking
- [ ] Display driver rating and completed rides count
- [ ] Add driver profile modal with:
  - Photo, name, rating
  - Total rides, years of experience
  - Vehicle details
  - Recent reviews
- [ ] Add "View Driver Profile" link in ride details

#### Database:
- ✅ Table `driver_profiles` already created

---

### 8. ⭐ Rating & Feedback System
**Priority: HIGH | Estimated Time: 3-4 hours**

#### Backend:
- [ ] Enhance `FeedbackRepository.java` for new columns
- [ ] Create `RatingService.java` with methods:
  - `submitRating(bookingId, userId, ratingDTO)` - Submit rating
  - `getBookingRating(bookingId)` - Get rating
  - `getDriverRatings(driverId, pagination)` - Driver reviews
  - `calculateDriverAverageRating(driverId)` - Update average
  - `reportReview(reviewId, reason)` - Report inappropriate review
  - `getTopReviews(driverId, limit)` - Get helpful reviews
- [ ] Create `RatingController.java`:
  - `POST /api/ratings/submit` - Submit rating
  - `GET /api/ratings/booking/{bookingId}` - Get booking rating
  - `GET /api/ratings/driver/{driverId}` - Driver reviews
  - `POST /api/ratings/{id}/report` - Report review
  - `POST /api/ratings/{id}/helpful` - Mark as helpful

#### Frontend:
- [ ] Post-ride rating modal with:
  - Overall rating (1-5 stars)
  - Ride quality, Vehicle cleanliness, Driver behavior, Punctuality
  - Tags: "Clean Vehicle", "Polite", "Safe Driving", "On Time"
  - Text feedback
- [ ] Show driver's average rating in profile
- [ ] Display recent reviews in driver profile
- [ ] Add "Mark as Helpful" button on reviews

#### Database:
- ✅ Table `feedbacks` enhanced with new columns

---

### 9. 🎁 Promo Codes & Offers
**Priority: MEDIUM | Estimated Time: 2-3 hours**

#### Backend:
- [ ] Create `PromoCodeRepository.java`
- [ ] Create `PromoCodeUsageRepository.java`
- [ ] Create `PromoCodeService.java` with methods:
  - `validatePromoCode(code, userId, fareAmount)` - Check validity
  - `applyPromoCode(code, userId, bookingId)` - Apply promo
  - `getActivePromoCodes(userId)` - Get available promos
  - `getUserPromoHistory(userId)` - Usage history
  - `createPromoCode(promoDTO)` - Admin creates promo
- [ ] Create `PromoCodeController.java`:
  - `POST /api/promo/validate` - Validate promo code
  - `POST /api/promo/apply` - Apply to booking
  - `GET /api/promo/active` - Get active promos
  - `GET /api/promo/history/{userId}` - Usage history

#### Frontend:
- [ ] Add "Apply Promo Code" section in booking flow
- [ ] Show available promo codes with:
  - Code, discount, validity, conditions
  - "Apply" button
- [ ] Display promo discount in fare breakdown
- [ ] Show "Saved ₹X" after applying promo
- [ ] Create "Offers" page with all active promos

#### Database:
- ✅ Tables `promo_codes` and `promo_code_usage` created with sample promos

---

### 10. 🔔 In-App Notifications
**Priority: MEDIUM | Estimated Time: 3-4 hours**

#### Backend:
- [ ] Create `NotificationRepository.java`
- [ ] Create `NotificationService.java` with methods:
  - `sendNotification(userId, title, message, type)` - Create notification
  - `getUserNotifications(userId, pagination)` - Get notifications
  - `markAsRead(notificationId)` - Mark read
  - `markAllAsRead(userId)` - Mark all read
  - `deleteNotification(notificationId)` - Delete
  - `getUnreadCount(userId)` - Count unread
- [ ] Create `NotificationController.java`:
  - `GET /api/notifications/user/{userId}` - Get notifications
  - `PUT /api/notifications/{id}/read` - Mark as read
  - `PUT /api/notifications/user/{userId}/read-all` - Mark all read
  - `DELETE /api/notifications/{id}` - Delete
  - `GET /api/notifications/user/{userId}/unread-count` - Unread count
- [ ] Add notification triggers:
  - Booking confirmed
  - Driver assigned
  - Driver arrived
  - Ride started
  - Ride completed
  - Payment successful
  - New promo available

#### Frontend:
- [ ] Add notification bell icon in header with unread badge
- [ ] Create notification dropdown/panel
- [ ] Show notifications with icon, title, message, time
- [ ] Add "Mark all as read" option
- [ ] Auto-refresh unread count every 30 seconds
- [ ] Add notification preferences in settings

#### Database:
- ✅ Table `notifications` already created

---

### 11. 🆘 SOS Emergency Button
**Priority: HIGH | Estimated Time: 3-4 hours**

#### Backend:
- [ ] Create `SOSAlertRepository.java`
- [ ] Create `SOSService.java` with methods:
  - `triggerSOS(userId, bookingId, location, type, message)` - Create alert
  - `getActiveAlerts(userId)` - Get user's active alerts
  - `resolveAlert(alertId, notes)` - Resolve SOS
  - `notifyEmergencyContacts(alertId)` - Send SMS/Email
  - `notifyPolice(alertId)` - Optional police notification
  - `trackSOSLocation(alertId)` - Real-time tracking
- [ ] Create `SOSController.java`:
  - `POST /api/sos/trigger` - Trigger emergency alert
  - `GET /api/sos/active/{userId}` - Get active alerts
  - `PUT /api/sos/{id}/resolve` - Resolve alert
  - `GET /api/sos/{id}/location` - Get current location

#### Frontend:
- [ ] Add prominent red "SOS" button during active ride
- [ ] SOS trigger modal with:
  - Alert type: General, Accident, Harassment, Medical
  - Quick message options
  - Current location display
  - Emergency contact selection
- [ ] Auto-capture current GPS location
- [ ] Show "Alert Triggered" confirmation
- [ ] Add SOS alert in notification panel

#### Database:
- ✅ Table `sos_alerts` already created

#### Integration:
- [ ] SMS Gateway for emergency contacts (Twilio/MSG91)
- [ ] Email service for emergency notifications

---

### 12. 📞 Customer Support (Chat/Call)
**Priority: MEDIUM | Estimated Time: 3-4 hours**

#### Backend:
- [ ] Create `SupportTicketRepository.java`
- [ ] Create `SupportMessageRepository.java`
- [ ] Create `SupportService.java` with methods:
  - `createTicket(userId, subject, category, description)` - Create ticket
  - `getUserTickets(userId, status)` - Get user tickets
  - `addMessage(ticketId, senderId, message)` - Add chat message
  - `getTicketMessages(ticketId)` - Get conversation
  - `resolveTicket(ticketId, resolution)` - Resolve ticket
  - `assignTicket(ticketId, agentId)` - Assign to agent
- [ ] Create `SupportController.java`:
  - `POST /api/support/tickets/create` - Create ticket
  - `GET /api/support/tickets/user/{userId}` - Get tickets
  - `GET /api/support/tickets/{id}` - Get ticket details
  - `POST /api/support/tickets/{id}/message` - Add message
  - `GET /api/support/tickets/{id}/messages` - Get messages
  - `PUT /api/support/tickets/{id}/resolve` - Resolve

#### Frontend:
- [ ] Add "Support" section in dashboard
- [ ] Create ticket form with:
  - Subject, category (Booking, Payment, Driver, Technical, Other)
  - Description, attach screenshot
  - Related booking (optional)
- [ ] Show ticket list with status badges (Open, In Progress, Resolved, Closed)
- [ ] Create chat interface for ticket messages
- [ ] Add "Call Support" button with phone number
- [ ] Show estimated response time

#### Database:
- ✅ Tables `support_tickets` and `support_messages` already created

---

### 13. 👤 User Profile Management
**Priority: MEDIUM | Estimated Time: 2-3 hours**

#### Backend:
- [ ] Create `UserProfileRepository.java`
- [ ] Create `UserProfileService.java` with methods:
  - `createProfile(userId, profileDTO)` - Create profile
  - `updateProfile(userId, updates)` - Update profile
  - `uploadProfilePhoto(userId, photo)` - Upload photo
  - `addSavedAddress(userId, addressDTO)` - Save address
  - `getUserProfile(userId)` - Get complete profile
  - `updateNotificationPreferences(userId, preferences)` - Update settings
- [ ] Create `UserProfileController.java`:
  - `POST /api/profile/create` - Create profile
  - `PUT /api/profile/{userId}` - Update profile
  - `GET /api/profile/{userId}` - Get profile
  - `POST /api/profile/{userId}/photo` - Upload photo
  - `POST /api/profile/{userId}/address` - Add saved address
  - `PUT /api/profile/{userId}/preferences` - Update preferences

#### Frontend:
- [ ] Create "Profile" page with tabs:
  - **Personal Info**: Name, email, phone, photo, DOB, gender
  - **Address**: Saved addresses (Home, Work, Other)
  - **Emergency Contact**: Name, phone, relation
  - **Preferences**: Language, notifications, theme
  - **Membership**: Tier (Silver/Gold/Platinum), loyalty points, total bookings
- [ ] Add profile photo upload with crop functionality
- [ ] Show membership progress bar
- [ ] Add "Edit Profile" modal

#### Database:
- ✅ Tables `user_profiles` and `saved_addresses` already created

---

### 14. 🌙 Dark/Light Mode Toggle
**Priority: LOW | Estimated Time: 1-2 hours**

#### Backend:
- [ ] Add theme preference to UserProfile (already in schema)
- [ ] Update `updateProfile` method to save theme preference

#### Frontend:
- [ ] Add theme toggle button in header (moon/sun icon)
- [ ] Create dark mode CSS variables:
  ```css
  :root[data-theme="dark"] {
      --bg-primary: #1a1a2e;
      --bg-secondary: #16213e;
      --text-primary: #ffffff;
      --text-secondary: #a0a0a0;
      --accent-gradient: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  }
  ```
- [ ] Apply dark mode styles to all pages
- [ ] Save theme preference to localStorage and database
- [ ] Auto-apply theme on page load
- [ ] Add "Auto (System)" option to follow OS theme

---

### 15. 📊 Enhanced Statistics Dashboard
**Priority: MEDIUM | Estimated Time: 2 hours**

#### Tasks:
- [ ] Add statistics cards:
  - Total rides, Total spent, Wallet balance
  - Upcoming scheduled rides, Active bookings
  - Loyalty points, Membership tier
- [ ] Add charts:
  - Monthly spending chart (Line/Bar)
  - Ride frequency by vehicle type (Pie)
  - Weekly ride pattern (Heatmap)
- [ ] Add quick actions:
  - Book Now, Schedule Ride, View History
  - Add Money to Wallet, Apply Promo

---

### 16. 🔐 Enhanced Security Features
**Priority: MEDIUM | Estimated Time: 2-3 hours**

#### Backend:
- [ ] Add OTP verification for phone numbers
- [ ] Add two-factor authentication (2FA) option
- [ ] Add session management and device tracking
- [ ] Add password strength validation
- [ ] Add account activity log

#### Frontend:
- [ ] Add "Verify Phone" flow with OTP
- [ ] Add "Enable 2FA" in security settings
- [ ] Show active sessions and devices
- [ ] Add "Change Password" with strength meter
- [ ] Show recent account activity

---

### 17. 🚗 Vehicle Preferences
**Priority: LOW | Estimated Time: 1-2 hours**

#### Backend:
- [ ] Add user vehicle preferences to UserProfile
- [ ] Filter available cabs based on preferences

#### Frontend:
- [ ] Add vehicle preference settings:
  - Preferred vehicle types
  - AC/Non-AC preference
  - Pet-friendly option
  - Accessibility needs
- [ ] Show matching vehicles with badge "Matches Your Preference"

---

## 📁 Project Structure (New Files)

```
src/main/java/com/cabease/
├── entity/
│   ├── RideSchedule.java ✅
│   ├── PaymentMethod.java ✅
│   ├── UserProfile.java ✅
│   ├── WalletTransaction.java
│   ├── DriverProfile.java
│   ├── PromoCode.java
│   ├── PromoCodeUsage.java
│   ├── Notification.java
│   ├── SOSAlert.java
│   ├── SavedAddress.java
│   ├── SupportTicket.java
│   ├── SupportMessage.java
│   ├── RideTracking.java
│   └── FareRule.java
├── repository/
│   ├── RideScheduleRepository.java
│   ├── PaymentMethodRepository.java
│   ├── UserProfileRepository.java
│   ├── WalletTransactionRepository.java
│   ├── DriverProfileRepository.java
│   ├── PromoCodeRepository.java
│   ├── NotificationRepository.java
│   ├── SOSAlertRepository.java
│   ├── SupportTicketRepository.java
│   ├── RideTrackingRepository.java
│   └── FareRuleRepository.java
├── service/
│   ├── RideScheduleService.java
│   ├── PaymentService.java
│   ├── UserProfileService.java
│   ├── DriverProfileService.java
│   ├── RatingService.java
│   ├── PromoCodeService.java
│   ├── NotificationService.java
│   ├── SOSService.java
│   ├── SupportService.java
│   ├── RideTrackingService.java
│   ├── FareCalculationService.java
│   └── InvoiceService.java
├── controller/
│   ├── RideScheduleController.java
│   ├── PaymentController.java
│   ├── UserProfileController.java
│   ├── DriverProfileController.java
│   ├── RatingController.java
│   ├── PromoCodeController.java
│   ├── NotificationController.java
│   ├── SOSController.java
│   ├── SupportController.java
│   ├── RideTrackingController.java
│   └── FareEstimationController.java
├── dto/
│   ├── RideScheduleDTO.java
│   ├── PaymentMethodDTO.java
│   ├── UserProfileDTO.java
│   ├── RatingDTO.java
│   ├── PromoCodeDTO.java
│   ├── NotificationDTO.java
│   └── SOSAlertDTO.java
└── websocket/
    ├── WebSocketConfig.java
    └── LocationWebSocketHandler.java

src/main/resources/
├── db/migration/
│   └── V4__add_premium_features.sql ✅
└── static/
    ├── js/
    │   ├── ride-tracking.js
    │   ├── notifications.js
    │   ├── payment.js
    │   └── theme-switcher.js
    └── css/
        └── dark-mode.css

src/main/resources/templates/
├── booking-dashboard.html (ENHANCED)
├── profile.html
├── ride-history.html
├── support.html
└── offers.html
```

---

## 🚀 Implementation Priority Order

### Phase 1: Foundation (Days 1-2)
1. ✅ Database schema and entities
2. Enhanced booking dashboard (merge admin features)
3. User profile management
4. Dark/light mode toggle

### Phase 2: Core Features (Days 3-5)
1. Ride scheduling system
2. Multiple payment methods with wallet
3. Fare estimation engine
4. Rating & feedback system

### Phase 3: Premium Features (Days 6-8)
1. Live ride tracking with WebSocket
2. Ride history with invoices
3. Driver profiles with photos
4. Promo codes & offers

### Phase 4: Safety & Support (Days 9-10)
1. SOS emergency button
2. In-app notifications
3. Customer support chat
4. Enhanced security features

### Phase 5: Polish & Testing (Days 11-12)
1. Enhanced statistics dashboard
2. Vehicle preferences
3. Integration testing
4. UI/UX refinements
5. Performance optimization

---

## 🔧 Dependencies to Add

```xml
<!-- WebSocket for live tracking -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>

<!-- PDF generation for invoices -->
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>itextpdf</artifactId>
    <version>5.5.13.3</version>
</dependency>

<!-- SMS Gateway (Twilio) -->
<dependency>
    <groupId>com.twilio.sdk</groupId>
    <artifactId>twilio</artifactId>
    <version>9.2.0</version>
</dependency>

<!-- File upload handling -->
<dependency>
    <groupId>commons-fileupload</groupId>
    <artifactId>commons-fileupload</artifactId>
    <version>1.5</version>
</dependency>

<!-- JSON processing -->
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
</dependency>
```

---

## 📝 Configuration Updates

### application-prod.properties additions:
```properties
# WebSocket
spring.websocket.allowed-origins=http://localhost:8080

# File Upload
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
spring.servlet.multipart.enabled=true

# Twilio SMS (for SOS alerts)
twilio.account-sid=YOUR_ACCOUNT_SID
twilio.auth-token=YOUR_AUTH_TOKEN
twilio.phone-number=YOUR_PHONE_NUMBER

# Email (for invoices and notifications)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=YOUR_EMAIL
spring.mail.password=YOUR_APP_PASSWORD
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# File Storage
file.upload-dir=./uploads
file.profile-photos-dir=./uploads/profiles
file.driver-photos-dir=./uploads/drivers

# Notification Settings
notification.batch-size=100
notification.fetch-interval=30000
```

---

## 🎨 UI/UX Guidelines

### Maintain Luxury Theme:
- **Colors**: Keep gradient backgrounds (`linear-gradient(135deg, #667eea 0%, #764ba2 100%)`)
- **Animations**: Smooth transitions on all interactions
- **Cards**: Glass-morphism effect with backdrop blur
- **Icons**: Font Awesome 6.4.0 consistent usage
- **Typography**: Inter/Poppins fonts for modern look
- **Spacing**: Generous padding/margins for premium feel

### Dark Mode Palette:
- **Background**: `#1a1a2e` (primary), `#16213e` (secondary)
- **Text**: `#ffffff` (primary), `#a0a0a0` (secondary)
- **Accent**: Maintain gradient but adjust brightness
- **Cards**: `#0f3460` with reduced opacity

---

## ✅ Testing Checklist

### Unit Tests:
- [ ] Test all service methods
- [ ] Test fare calculation logic
- [ ] Test promo code validation
- [ ] Test payment processing

### Integration Tests:
- [ ] Test ride scheduling flow
- [ ] Test booking + payment flow
- [ ] Test rating submission
- [ ] Test WebSocket live tracking
- [ ] Test SOS alert flow

### UI/UX Tests:
- [ ] Test on Chrome, Firefox, Safari
- [ ] Test responsive design (mobile, tablet, desktop)
- [ ] Test dark mode toggle
- [ ] Test all forms and validations
- [ ] Test accessibility (WCAG compliance)

### Performance Tests:
- [ ] Load test with 100+ concurrent users
- [ ] Test WebSocket with multiple connections
- [ ] Test database query performance
- [ ] Test file upload limits

---

## 🎯 Success Metrics

- ✅ All 17 premium features implemented and functional
- ✅ Zero critical bugs
- ✅ Page load time < 2 seconds
- ✅ 100% responsive design
- ✅ Dark mode fully functional
- ✅ WebSocket tracking working with < 5s latency
- ✅ All payment methods integrated
- ✅ SOS alerts trigger within 1 second
- ✅ Maintain luxury UI theme consistency

---

## 📚 Documentation to Create

- [ ] API documentation (Swagger/OpenAPI)
- [ ] User guide for new features
- [ ] Admin guide for promo code management
- [ ] Driver guide for profile setup
- [ ] Deployment guide
- [ ] Troubleshooting guide

---

## 🚀 Ready to Start!

Let me know which feature you'd like to implement first! I recommend starting with:

1. **Enhanced Booking Dashboard** (merging admin features) - Foundation
2. **Multiple Payment Methods** - Critical user need
3. **Live Ride Tracking** - Premium differentiator
4. **SOS Emergency Button** - Safety feature

I'm ready to implement any of these features step-by-step with full backend + frontend + database integration! 🎉
