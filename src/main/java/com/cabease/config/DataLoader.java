package com.cabease.config;
import com.cabease.models.User;
import com.cabease.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
@Component
public class DataLoader implements CommandLineRunner {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.existsByEmail("admin@cabease.com")) {
            User admin = User.builder()
                .username("admin")
                .name("Admin User")
                .email("admin@cabease.com")
                .password(passwordEncoder.encode("admin123"))
                .role("ROLE_ADMIN")
                .build();
            userRepository.save(admin);
            System.out.println("Created admin user: admin@cabease.com / admin123");
        }
        if (!userRepository.existsByEmail("testuser@cabease.com")) {
            User testUser = User.builder()
                .username("testuser")
                .name("Test User")
                .email("testuser@cabease.com")
                .password(passwordEncoder.encode("user123"))
                .role("ROLE_USER")
                .build();
            userRepository.save(testUser);
            System.out.println("Created test user: testuser@cabease.com / user123");
        }
        if (!userRepository.existsByEmail("john@example.com")) {
            User john = User.builder()
                .username("john")
                .name("John Doe")
                .email("john@example.com")
                .password(passwordEncoder.encode("test123"))
                .role("ROLE_USER")
                .build();
            userRepository.save(john);
            System.out.println("Created john user: john@example.com / test123");
        }
        System.out.println("=== LOGIN CREDENTIALS ===");
        System.out.println("Admin: admin@cabease.com / admin123");
        System.out.println("User:  testuser@cabease.com / user123");
        System.out.println("User:  john@example.com / test123");
        System.out.println("========================");
    }
}
