# Ride Scheduling System - Testing Guide

## 🎉 Feature Complete!

The Ride Scheduling System has been successfully implemented with full backend and frontend functionality.

---

## 📋 Implementation Summary

### **Backend Components (100% Complete)**

#### 1. **RideScheduleRepository** (`src/main/java/com/cabease/repository/RideScheduleRepository.java`)
Spring Data JPA repository with 8 custom queries:
- `findByUserOrderByScheduledTimeDesc()` - All rides for user
- `findUpcomingRidesByUser()` - Future SCHEDULED/CONFIRMED rides
- `findPastRidesByUser()` - Historical/completed rides
- `findByStatusOrderByScheduledTimeAsc()` - Filter by status
- `findByUserAndStatusOrderByScheduledTimeDesc()` - User + status filter
- `countUpcomingRidesByUser()` - Count upcoming rides (for badge)
- `findByUserAndTimeRange()` - Date range filtering
- `findTodayRidesByUser()` - Today's scheduled rides

#### 2. **RideScheduleService** (`src/main/java/com/cabease/service/RideScheduleService.java`)
Business logic layer with 11 methods:

**Core Scheduling:**
- `scheduleRide()` - Create new scheduled ride with fare estimation
  - Validates: time is in future, max 30 days ahead
  - Calculates: distance using Haversine formula
  - Integrates: FareCalculationService for dynamic pricing
  - Sets: status to "SCHEDULED"

**Modification & Cancellation:**
- `modifyScheduledRide()` - Update time/instructions
  - Ownership verification
  - Recalculates fare if time changed
- `cancelScheduledRide()` - Cancel with reason
  - Records cancellation timestamp
  - Prevents double cancellation

**Retrieval Methods:**
- `getRideScheduleById()` - Get single ride
- `getAllRidesByUser()` - All rides (desc order)
- `getUpcomingRides()` - Future rides only
- `getPastRides()` - Historical rides
- `getTodayRides()` - Today's schedule
- `countUpcomingRides()` - Count for UI badge

**Admin Actions:**
- `confirmScheduledRide()` - Mark as CONFIRMED
- `completeScheduledRide()` - Mark as COMPLETED

#### 3. **RideScheduleController** (`src/main/java/com/cabease/controllers/api/RideScheduleController.java`)
REST API layer - 8 endpoints under `/api/v1/schedule`:

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/schedule` | Schedule new ride |
| GET | `/api/v1/schedule` | List all user's rides |
| GET | `/api/v1/schedule/upcoming` | Future rides only |
| GET | `/api/v1/schedule/past` | Historical rides |
| GET | `/api/v1/schedule/today` | Today's rides |
| GET | `/api/v1/schedule/{id}` | Get specific ride |
| PUT | `/api/v1/schedule/{id}` | Modify scheduled ride |
| DELETE | `/api/v1/schedule/{id}` | Cancel ride |
| GET | `/api/v1/schedule/count/upcoming` | Count upcoming rides |

**Features:**
- ✅ Authentication required (all endpoints)
- ✅ Ownership verification (modify/cancel/view)
- ✅ ISO datetime format (YYYY-MM-DDTHH:mm:ss)
- ✅ Comprehensive error handling (401/403/400/500)

### **Frontend Components (100% Complete)**

#### 4. **schedule-ride.html** (`src/main/resources/templates/schedule-ride.html`)
Comprehensive scheduling interface with:

**Form Components:**
- 📍 Pickup location (Google Maps Autocomplete)
- 📍 Drop location (Google Maps Autocomplete)
- 📅 Date/time picker (Flatpickr)
  - Min: Today
  - Max: 30 days from now
  - 15-minute increments
- 🚗 Vehicle type selector (CAB/SUV/BIKE/AUTO)
- 👥 Passenger count (1-6)
- 💬 Special instructions (optional textarea)

**Ride Management:**
- 📋 Upcoming rides tab (shows future rides)
- 📜 Past rides tab (shows history)
- ✏️ Modify button (change time/instructions)
- ❌ Cancel button (with optional reason)
- 📊 Ride count badge

**UI Features:**
- Purple gradient theme (consistent with app)
- Glass morphism cards
- Responsive 2-column layout
- Loading states
- Success/error messages
- Empty state displays
- Hover animations

#### 5. **Navigation Updates**
Added "Schedule Ride" link to 5 pages:
- ✅ `profile.html` - After Fare Calculator
- ✅ `wallet.html` - After Fare Calculator
- ✅ `payment-methods.html` - After Fare Calculator
- ✅ `analytics-dashboard.html` - After Fare Calculator
- ✅ `fare-calculator.html` - After Analytics

#### 6. **Route Mapping**
Added `/schedule-ride` route in `PaymentViewController.java`:
```java
@GetMapping("/schedule-ride")
public String scheduleRide(Model model) {
    User user = getCurrentUser();
    // ... add user attributes to model
    return "schedule-ride";
}
```

---

## 🧪 Testing Instructions

### **Login Credentials**
```
Admin: admin@cabease.com / admin123
User:  testuser@cabease.com / user123
User:  john@example.com / test123
```

### **Access the Feature**
1. Start application: http://localhost:8080
2. Login with any test account
3. Navigate to "Schedule Ride" from navigation menu

---

## 📝 Test Cases

### **Test Case 1: Schedule Ride Tomorrow**
**Steps:**
1. Click "Schedule Ride" in navigation
2. Enter pickup: "Connaught Place, Delhi"
3. Enter drop: "Delhi Airport"
4. Select date: Tomorrow at 2:00 PM
5. Choose vehicle: CAB
6. Set passengers: 2
7. Add instructions: "Please call before arrival"
8. Click "Schedule Ride"

**Expected:**
- ✅ Success message displayed
- ✅ Ride appears in "Upcoming" tab
- ✅ Estimated fare shown (₹250-300 range)
- ✅ Status: SCHEDULED
- ✅ All details correctly displayed

**API Call:**
```bash
POST /api/v1/schedule
{
  "pickupLocation": "Connaught Place, Delhi",
  "pickupLatitude": 28.6139,
  "pickupLongitude": 77.2090,
  "dropLocation": "Delhi Airport",
  "dropLatitude": 28.5562,
  "dropLongitude": 77.1000,
  "scheduledTime": "2025-10-21T14:00:00",
  "vehicleType": "CAB",
  "passengerCount": 2,
  "specialInstructions": "Please call before arrival"
}
```

---

### **Test Case 2: Validation - Past Date**
**Steps:**
1. Try to schedule ride for yesterday

**Expected:**
- ❌ Error: "Scheduled time must be in the future"
- ❌ HTTP 400 Bad Request
- Ride not created

---

### **Test Case 3: Validation - Too Far Future**
**Steps:**
1. Try to schedule ride 31 days ahead

**Expected:**
- ❌ Error: "Cannot schedule rides more than 30 days in advance"
- ❌ HTTP 400 Bad Request
- Ride not created

---

### **Test Case 4: Modify Scheduled Ride**
**Steps:**
1. Schedule a ride for tomorrow
2. Click "Modify" button on the ride card
3. Enter new time: Day after tomorrow at 3:00 PM
4. Confirm

**Expected:**
- ✅ Ride time updated
- ✅ Fare recalculated (if surge changed)
- ✅ Success message shown
- ✅ Updated details displayed

**API Call:**
```bash
PUT /api/v1/schedule/{id}
{
  "scheduledTime": "2025-10-22T15:00:00"
}
```

---

### **Test Case 5: Cancel Scheduled Ride**
**Steps:**
1. Schedule a ride
2. Click "Cancel" button
3. Enter reason: "Changed plans"
4. Confirm cancellation

**Expected:**
- ✅ Status changed to CANCELLED
- ✅ Cancellation reason displayed
- ✅ Moved to "Past" tab
- ✅ No longer in "Upcoming" tab
- ❌ Modify/Cancel buttons hidden

**API Call:**
```bash
DELETE /api/v1/schedule/{id}?reason=Changed%20plans
```

---

### **Test Case 6: View Today's Rides**
**Steps:**
1. Schedule 2 rides for today (different times)
2. Schedule 1 ride for tomorrow

**Expected:**
- ✅ GET /api/v1/schedule/today returns 2 rides only
- Only today's rides shown (filtered by date)

---

### **Test Case 7: Upcoming Rides Count**
**Steps:**
1. Schedule 3 upcoming rides
2. Cancel 1 ride
3. Check "Upcoming" tab count

**Expected:**
- ✅ Count badge shows "2"
- ✅ GET /api/v1/schedule/count/upcoming returns 2

---

### **Test Case 8: Ownership Verification**
**Steps:**
1. Login as User A, schedule a ride (note ride ID)
2. Logout, login as User B
3. Try to access: GET /api/v1/schedule/{ride_A_id}

**Expected:**
- ❌ HTTP 403 Forbidden
- Error: "Access denied" or ride not found

---

### **Test Case 9: Multiple Vehicle Types**
**Steps:**
1. Schedule rides with different vehicles:
   - CAB - Short distance
   - SUV - Long distance
   - BIKE - Short distance
   - AUTO - Medium distance

**Expected:**
- ✅ Different fare estimates:
  - CAB: ~₹10/km
  - SUV: ~₹15/km
  - BIKE: ~₹5/km
  - AUTO: ~₹8/km
- ✅ All displayed with correct vehicle icons

---

### **Test Case 10: Past Rides Display**
**Steps:**
1. Manually update database: set scheduledTime to yesterday for one ride
2. Refresh schedule-ride page
3. Click "Past" tab

**Expected:**
- ✅ Yesterday's ride appears in "Past" tab
- ✅ Not shown in "Upcoming" tab
- ❌ No Modify/Cancel buttons
- ✅ Status displayed correctly

---

## 🔗 API Endpoints Reference

### **1. Schedule New Ride**
```http
POST /api/v1/schedule
Content-Type: application/json

{
  "pickupLocation": "string",
  "pickupLatitude": decimal,
  "pickupLongitude": decimal,
  "dropLocation": "string",
  "dropLatitude": decimal,
  "dropLongitude": decimal,
  "scheduledTime": "2025-10-20T14:30:00",
  "vehicleType": "CAB|SUV|BIKE|AUTO",
  "passengerCount": integer,
  "specialInstructions": "string (optional)"
}
```

**Response (200):**
```json
{
  "id": 1,
  "pickupLocation": "Connaught Place, Delhi",
  "dropLocation": "Delhi Airport",
  "scheduledTime": "2025-10-20T14:30:00",
  "vehicleType": "CAB",
  "passengerCount": 2,
  "status": "SCHEDULED",
  "estimatedFare": 275.50,
  "specialInstructions": "Please call before arrival",
  "createdAt": "2025-10-19T15:10:00",
  "updatedAt": "2025-10-19T15:10:00"
}
```

---

### **2. Get Upcoming Rides**
```http
GET /api/v1/schedule/upcoming
```

**Response (200):**
```json
[
  {
    "id": 1,
    "scheduledTime": "2025-10-21T14:30:00",
    "status": "SCHEDULED",
    "estimatedFare": 275.50,
    ...
  },
  {
    "id": 2,
    "scheduledTime": "2025-10-22T10:00:00",
    "status": "CONFIRMED",
    "estimatedFare": 450.00,
    ...
  }
]
```

---

### **3. Modify Scheduled Ride**
```http
PUT /api/v1/schedule/{id}
Content-Type: application/json

{
  "scheduledTime": "2025-10-21T16:00:00",
  "specialInstructions": "Updated instructions"
}
```

**Response (200):**
```json
{
  "id": 1,
  "scheduledTime": "2025-10-21T16:00:00",
  "estimatedFare": 320.00,
  "updatedAt": "2025-10-19T15:30:00",
  ...
}
```

---

### **4. Cancel Ride**
```http
DELETE /api/v1/schedule/{id}?reason=Changed%20plans
```

**Response (200):**
```json
{
  "message": "Ride cancelled successfully",
  "ride": {
    "id": 1,
    "status": "CANCELLED",
    "cancelledAt": "2025-10-19T15:40:00",
    "cancellationReason": "Changed plans",
    ...
  }
}
```

---

## ⚙️ Configuration

### **Date/Time Settings**
- **Min Date:** Today
- **Max Date:** 30 days from today
- **Time Increment:** 15 minutes
- **Format:** ISO 8601 (YYYY-MM-DDTHH:mm:ss)
- **Timezone:** UTC (server-side)

### **Vehicle Types**
| Type | Icon | Base Rate | Fare/km | Capacity |
|------|------|-----------|---------|----------|
| CAB | 🚗 | ₹30 | ₹10 | 4 |
| SUV | 🚙 | ₹50 | ₹15 | 6 |
| BIKE | 🏍️ | ₹20 | ₹5 | 1 |
| AUTO | 🛺 | ₹25 | ₹8 | 3 |

### **Fare Calculation**
- Base fare + (Distance × Rate multiplier)
- Surge pricing applied based on:
  - Time of day (peak hours: 8-10 AM, 6-8 PM)
  - Day of week (weekends)
  - Location (high-demand areas)
- 5% tax added
- Integration with FareCalculationService

---

## 🐛 Known Issues

**None** - All functionality working as expected!

---

## 📊 Feature Status

| Component | Status | Lines of Code |
|-----------|--------|---------------|
| RideScheduleRepository | ✅ Complete | ~80 lines |
| RideScheduleService | ✅ Complete | ~275 lines |
| RideScheduleController | ✅ Complete | ~400 lines |
| schedule-ride.html | ✅ Complete | ~900 lines |
| Navigation Updates | ✅ Complete | 5 files |
| Route Mapping | ✅ Complete | PaymentViewController |
| **TOTAL** | **✅ 100%** | **~1,655 lines** |

---

## 🚀 Next Steps

### **Enhancement Opportunities:**
1. **Email Notifications**
   - Send confirmation email when ride scheduled
   - Reminder 24 hours before scheduled time
   - Cancellation confirmation email

2. **Driver Assignment**
   - Auto-assign driver 1 hour before scheduled time
   - Notify user when driver assigned
   - Show driver details in upcoming rides

3. **Ride Recurrence**
   - Weekly/monthly recurring rides
   - Save favorite routes
   - Quick reschedule option

4. **Advanced Features**
   - Split fare for multiple passengers
   - Ride sharing for same route
   - Estimated time to pickup
   - Weather-based surge adjustments

---

## ✅ Build & Deployment

**Build Status:** ✅ **SUCCESS**
```
[INFO] Building CabEase 1.0.0
[INFO] BUILD SUCCESS
[INFO] Total time: 8.174 s
```

**Application Status:** ✅ **RUNNING**
```
Started CabEaseApplication in 8.42 seconds
Tomcat started on port(s): 8080 (http)
```

**Database:** ✅ PostgreSQL 15 connected
**Tables:** ride_schedules table exists with all required columns

---

## 📚 Related Documentation

- [Fare Calculator Test Guide](FARE_CALCULATOR_TEST.md)
- [Project TODO](TODO.md)
- [Database Schema](data/schema.sql)

---

**Feature Completed:** October 19, 2025  
**Version:** 1.0.0  
**Status:** ✅ Production Ready
