package com.cabease.config;
import com.cabease.models.User;
import com.cabease.models.Driver;
import com.cabease.models.Cab;
import com.cabease.models.VehicleType;
import com.cabease.entity.UserProfile;
import com.cabease.services.UserService;
import com.cabease.repository.DriverRepository;
import com.cabease.repository.CabRepository;
import com.cabease.repository.UserProfileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.Random;
@Component
@Slf4j
public class DataInitializer implements CommandLineRunner {
    @Autowired
    private UserService userService;
    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private CabRepository cabRepository;
    @Autowired
    private UserProfileRepository userProfileRepository;
    private static final String[] TAMIL_NAMES = {
        "Karthik Kumar", "Rajesh Murugan", "Vijay Selvam", "Suresh Rajan", "Anbu Arasan",
        "Dinesh Pandian", "Ganesan Venkat", "Hari Krishnan", "Ilango Muthu", "Jagan Prakash",
        "Kumar Saravanan", "Lakshman Anand", "Mahesh Babu", "Naveen Kumar", "Om Prakash",
        "Prabhu Deva", "Ramesh Kannan", "Senthil Velu", "Tamil Arasan", "Udhay Kumar",
        "Vignesh Raja", "Vishnu Vardhan", "Xavier Thomas", "Yogesh Sundar", "Arun Vijay",
        "Bala Ganesh", "Chandran Mani", "Dhanush Raj", "Elango Surya", "Feroz Khan",
        "Gopal Krishna", "Hariharan Iyer", "Immanuel David", "Jeeva Anand", "Kali Murugan",
        "Lokesh Kanagaraj", "Mani Ratnam", "Narayanan Pillai", "Oviyam Kannan", "Parthiban Radha",
        "Raghavan Srinivas", "Sakthi Vel", "Thirumurugan Durai", "Ulaganathan Ravi", "Venkatesh Babu",
        "Arjun Reddy", "Bharat Ram", "Chandra Sekar", "Deva Rajan", "Eswar Prasad",
        "Francis Xavier", "Ganesh Kumar", "Hari Shankar", "Inian Paramesh", "Jaya Surya",
        "Kannan Gopalan", "Lenin Babu", "Murugan Raj", "Natarajan Venkat", "Oormila Kannan",
        "Pandi Raj", "Raman Krishnan", "Sasi Kumar", "Thiru Arjun", "Utham Kumar",
        "Vasu Devan", "Winston David", "Yuvraj Singh", "Aadhavan Murthy", "Barath Kumar",
        "Cheran Pandian", "Dharma Raj", "Ezhil Vendhan", "Franklin Joseph", "Gautham Karthik",
        "Harishankar Menon", "Ilamurugu Vel", "Jayachandran Pillai", "Kamal Hassan", "Lingaraj Murthy",
        "Muthuraman Srinivas", "Nagarajan Swamy", "Ojas Kumar", "Pranav Mohanlal", "Rajini Kanth",
        "Sarathy Venkat", "Thalapathy Vijay", "Udayanidhi Stalin", "Vaibhav Reddy", "Winson Paul",
        "Yogi Babu", "Ajith Kumar", "Balaji Sakthivel", "Chandru Manickam", "Devaraj Urs",
        "Eshwar Gowda", "Franklin Raj", "Gnana Prakash", "Hari Om", "Indrajith Sukumaran",
        "Jayam Ravi", "Karthi Sivakumar", "Lingusamy Pandian", "Manoj Bharathiraja", "Narain Kumar"
    };
    private static final String[] VEHICLE_MODELS = {"Swift", "i20", "City", "Innova", "Dzire", "Verna", "Amaze", "Etios", "EcoSport", "Duster"};
    private static final String[] VEHICLE_BRANDS = {"Maruti", "Hyundai", "Honda", "Toyota", "Ford", "Renault", "Mahindra", "Tata", "Kia", "MG"};
    private static final String[] TN_DISTRICTS = {
        "01", "02", "03", "04", "05", "07", "09", "10", "11", "12",
        "14", "15", "16", "18", "19", "20", "21", "22", "23", "24"
    };
    @Override
    public void run(String... args) throws Exception {
        createTestUserIfNotExists("Admin User", "admin123", "admin@cabease.com", "ROLE_ADMIN");
        createTestUserIfNotExists("Test User", "user123", "testuser@cabease.com", "ROLE_USER");
        createTestUserIfNotExists("John Doe", "test123", "john@example.com", "ROLE_USER");
        log.info("Test users initialized successfully");
        createUserProfiles();
        createDriversAndCabs();
    }
    private void createTestUserIfNotExists(String name, String password, String email, String role) {
        try {
            if (!userService.existsByEmail(email)) {
                User user = User.builder()
                    .name(name)
                    .password(password)
                    .email(email)
                    .role(role)
                    .build();
                userService.registerUser(user);
                log.info("Created test user: {} ({})", name, email);
            } else {
                log.info("Test user already exists: {}", email);
            }
        } catch (Exception e) {
            log.error("Error creating test user {}: {}", email, e.getMessage());
        }
    }
    private void createDriversAndCabs() {
        try {
            long driverCount = driverRepository.count();
            if (driverCount >= 100) {
                log.info("Drivers already initialized: {} drivers exist", driverCount);
                return;
            }
            Random random = new Random();
            int driversCreated = 0;
            VehicleType[] vehicleTypes = VehicleType.values();
            for (int i = 0; i < 100; i++) {
                String driverName = TAMIL_NAMES[i % TAMIL_NAMES.length];
                if (i >= TAMIL_NAMES.length) {
                    driverName += " " + (i / TAMIL_NAMES.length + 1);
                }
                String licenseNumber = "TN" + String.format("%02d", random.nextInt(33) + 1) + String.format("%013d", 2020000000000L + random.nextInt(999999999));
                String phoneNumber = "+91" + (9000000000L + random.nextInt(999999999));
                String email = driverName.toLowerCase().replace(" ", ".") + i + "@cabease.com";
                if (driverRepository.existsByEmail(email) ||
                    driverRepository.existsByLicenseNumber(licenseNumber)) {
                    continue;
                }
                Driver driver = Driver.builder()
                    .name(driverName)
                    .licenseNumber(licenseNumber)
                    .phoneNumber(phoneNumber)
                    .email(email)
                    .available(random.nextInt(100) < 80)
                    .build();
                driver = driverRepository.save(driver);
                driversCreated++;
                String districtCode = TN_DISTRICTS[random.nextInt(TN_DISTRICTS.length)];
                String vehicleNumber = "TN" + districtCode + " " +
                                     (char)('A' + random.nextInt(26)) +
                                     (char)('A' + random.nextInt(26)) + " " +
                                     String.format("%04d", 1000 + random.nextInt(9000));
                String model = VEHICLE_MODELS[random.nextInt(VEHICLE_MODELS.length)];
                String brand = VEHICLE_BRANDS[random.nextInt(VEHICLE_BRANDS.length)];
                VehicleType vehicleType = vehicleTypes[random.nextInt(vehicleTypes.length)];
                double rating = 3.5 + (random.nextDouble() * 1.5);
                rating = Math.round(rating * 10.0) / 10.0;
                BigDecimal latitude = BigDecimal.valueOf(13.0827 + (random.nextDouble() - 0.5) * 0.2);
                BigDecimal longitude = BigDecimal.valueOf(80.2707 + (random.nextDouble() - 0.5) * 0.2);
                Cab cab = Cab.builder()
                    .cabNumber(vehicleNumber)
                    .model(model)
                    .brand(brand)
                    .vehicleType(vehicleType)
                    .capacity(vehicleType.getMaxCapacity())
                    .pricePerKm(BigDecimal.valueOf(vehicleType.getBaseFare()))
                    .driver(driver)
                    .available(driver.getAvailable())
                    .averageRating(rating)
                    .currentLatitude(latitude)
                    .currentLongitude(longitude)
                    .verified(true)
                    .acAvailable(random.nextBoolean())
                    .musicSystem(random.nextBoolean())
                    .gpsEnabled(true)
                    .surgeEligible(true)
                    .build();
                cabRepository.save(cab);
            }
            log.info("========================================");
            log.info("✅ Created {} new drivers with Chennai-based data", driversCreated);
            log.info("✅ Total drivers: {}", driverRepository.count());
            log.info("✅ Total cabs: {} with TN registration numbers", cabRepository.count());
            log.info("✅ Default location: Chennai, India (13.0827°N, 80.2707°E)");
            log.info("========================================");
        } catch (Exception e) {
            log.error("Error creating drivers and cabs: {}", e.getMessage(), e);
        }
    }
    private void createUserProfiles() {
        try {
            String[] emails = {"admin@cabease.com", "testuser@cabease.com", "john@example.com"};
            for (String email : emails) {
                User user = userService.findByEmail(email).orElse(null);
                if (user != null) {
                    if (!userProfileRepository.existsByUserId(user.getId())) {
                        UserProfile profile = new UserProfile();
                        profile.setUser(user);
                        profile.setPhoneNumber("+919876543210");
                        profile.setCity("Chennai");
                        profile.setState("Tamil Nadu");
                        profile.setCountry("India");
                        profile.setZipCode("600001");
                        profile.setPreferredLanguage("en");
                        profile.setTotalBookings(0);
                        profile.setTotalSpent(BigDecimal.ZERO);
                        profile.setLoyaltyPoints(0);
                        profile.setMembershipTier("SILVER");
                        profile.setThemePreference("dark");
                        userProfileRepository.save(profile);
                        log.info("✅ Created profile for user: {}", email);
                    }
                }
            }
            log.info("✅ User profiles initialized successfully");
        } catch (Exception e) {
            log.error("Error creating user profiles: {}", e.getMessage(), e);
        }
    }
}
