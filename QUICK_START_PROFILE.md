# 🚀 Quick Start Guide - User Profile System

## ✅ What's New?

You now have a **complete User Profile Management System** with:
- Personal information management
- Address and emergency contacts
- Theme customization (light/dark/auto)
- Notification preferences
- Loyalty points and membership tiers
- Statistics dashboard
- Beautiful, responsive UI

---

## 🎯 How to Use

### **1. Start the Application**

```bash
cd E:\CabEase
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

Wait for: `Started CabEaseApplication in X seconds`

---

### **2. Login**

Open browser: `http://localhost:8080/login`

**Test Accounts:**
- User: `testuser@cabease.com` / `user123`
- Admin: `admin@cabease.com` / `admin123`

---

### **3. Access Your Profile**

**Option A:** Direct URL
- Navigate to: `http://localhost:8080/profile`

**Option B:** From Navigation
- Go to Wallet or Payment Methods page
- Click "Profile" in the top navigation bar

---

### **4. Complete Your Profile**

#### **Personal Information:**
1. Enter your phone number
2. Add alternate phone (optional)
3. Set date of birth
4. Select gender

#### **Address:**
1. Fill in address details
2. City, state, ZIP code
3. Country (default: India)

#### **Emergency Contact:**
1. Enter emergency contact name
2. Add their phone number
3. Specify relationship

#### **Preferences:**
1. **Theme:** Choose Light, Dark, or Auto
2. **Language:** Select preferred language
3. **Notifications:** Toggle email/SMS/push notifications

#### **Save:**
Click the **"Save Changes"** button at the bottom

---

## 📊 View Your Statistics

After booking rides and using the app, you'll see:
- **Total Rides:** Number of completed bookings
- **Total Spent:** Money spent on rides
- **Loyalty Points:** Earned points (1 point per ₹10 spent)
- **Membership Tier:** SILVER → GOLD → PLATINUM → DIAMOND

---

## 🎨 Upload Profile Photo

1. Click the **camera icon** on your avatar
2. Enter image URL when prompted
3. Your avatar will update instantly

*Note: File upload feature coming soon!*

---

## 🏆 Membership Tiers

**How it works:**
- Complete rides → Earn loyalty points
- 1 point per ₹10 spent
- Auto-upgrade when you reach milestones

**Tier Requirements:**
- 🥈 **SILVER:** 0 - 1,999 points (starting tier)
- 🥇 **GOLD:** 2,000 - 4,999 points
- 💎 **PLATINUM:** 5,000 - 9,999 points
- 💠 **DIAMOND:** 10,000+ points

---

## 🔗 Navigation Links

From the Profile page, you can quickly access:
- **Dashboard:** `/booking-dashboard` - Book rides
- **Wallet:** `/wallet` - Manage wallet balance
- **Logout:** Exit application

---

## 🧪 Test the APIs

### **Get Your Profile:**
```bash
curl -X GET http://localhost:8080/api/profile \
  -H "Cookie: JSESSIONID=your_session_cookie"
```

### **Update Profile:**
```bash
curl -X PUT http://localhost:8080/api/profile \
  -H "Content-Type: application/json" \
  -H "Cookie: JSESSIONID=your_session_cookie" \
  -d '{
    "phoneNumber": "+91 98765 43210",
    "city": "Mumbai",
    "gender": "Male",
    "addressLine1": "123 Main Street"
  }'
```

### **Get Statistics:**
```bash
curl -X GET http://localhost:8080/api/profile/stats \
  -H "Cookie: JSESSIONID=your_session_cookie"
```

---

## 🎨 Theme Customization

**Three Theme Options:**

1. **Light Theme** ☀️
   - Bright, clean interface
   - Perfect for daytime use
   - Default theme

2. **Dark Theme** 🌙
   - Easy on the eyes
   - Great for night use
   - Reduces eye strain

3. **Auto Theme** 🔄
   - Matches system preferences
   - Switches automatically
   - Best of both worlds

**To Change:**
1. Go to Preferences section
2. Click your preferred theme
3. Click "Save Changes"

---

## 📱 Mobile Access

The profile page is **fully responsive**:
- Works on phones, tablets, desktops
- Touch-friendly buttons
- Optimized layouts
- Smooth animations

**Try it on your phone:**
1. Open `http://your-server:8080/profile`
2. Login with your credentials
3. Edit profile on the go!

---

## 🔔 Notification Preferences

Control what notifications you receive:

**Email Notifications:** ✅
- Ride confirmations
- Payment receipts
- Promotional offers

**SMS Notifications:** ✅
- Ride status updates
- Driver arrival alerts
- Critical updates

**Push Notifications:** ✅
- Real-time updates
- In-app notifications
- Breaking news

**To Toggle:**
1. Scroll to Preferences section
2. Use toggle switches
3. Save changes

---

## 🚨 Emergency Contact

**Why it's important:**
- Safety during rides
- Emergency situations
- Quick contact access

**What to include:**
1. Trusted contact name
2. Working phone number
3. Relationship (parent, spouse, friend)

---

## 💡 Pro Tips

### **Tip 1: Complete Your Profile**
A complete profile helps us serve you better!
- Better ride matching
- Personalized recommendations
- Faster support

### **Tip 2: Keep Contact Info Updated**
Update your phone number if it changes:
- Essential for ride coordination
- Driver contact
- OTP verification

### **Tip 3: Earn More Points**
Maximize your loyalty points:
- Book rides regularly
- Refer friends (coming soon)
- Complete challenges (coming soon)

### **Tip 4: Check Statistics**
Monitor your usage:
- Track spending
- View ride history
- Plan budget

---

## 🔒 Privacy & Security

Your data is safe:
- ✅ Encrypted connections (HTTPS in production)
- ✅ Secure authentication
- ✅ Data protection
- ✅ No data sharing without consent

**You control:**
- What information to share
- Notification preferences
- Profile visibility (coming soon)

---

## ❓ FAQ

### **Q: Can I delete my profile?**
A: Contact support. We'll help you with account deletion.

### **Q: Why can't I upload a photo file?**
A: File upload is coming soon! For now, use an image URL.

### **Q: How are loyalty points calculated?**
A: You earn 1 point for every ₹10 spent on rides.

### **Q: Can I transfer points to another account?**
A: Not currently. Points are non-transferable.

### **Q: What happens when I upgrade tiers?**
A: You get a fancy new badge! More benefits coming soon.

### **Q: Can I change my theme anytime?**
A: Yes! Change it as often as you like.

---

## 🐛 Troubleshooting

### **Profile not loading?**
1. Check your internet connection
2. Refresh the page (Ctrl+F5)
3. Clear browser cache
4. Try incognito mode

### **Changes not saving?**
1. Check all required fields are filled
2. Verify you clicked "Save Changes"
3. Look for error messages
4. Check browser console (F12)

### **Can't see statistics?**
1. Statistics appear after your first ride
2. Reload the page
3. Check API endpoint: `/api/profile/stats`

---

## 📞 Need Help?

**Application Logs:**
Check console output for any errors

**API Testing:**
Use browser DevTools (F12) → Network tab

**Support:**
Contact your system administrator

---

## 🎉 You're All Set!

Enjoy your new profile management system! 🚀

**Next Steps:**
1. ✅ Complete your profile
2. ✅ Customize preferences
3. ✅ Book rides and earn points
4. ✅ Reach DIAMOND tier! 💎

---

**Happy Riding with CabEase!** 🚖✨
