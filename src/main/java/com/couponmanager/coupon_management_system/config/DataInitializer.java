package com.couponmanager.coupon_management_system.config;

import com.couponmanager.coupon_management_system.model.User;
import com.couponmanager.coupon_management_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Create default admin user if it doesn't exist
        if (userRepository.findByEmail("admin@example.com") == null) {
            User admin = new User();
            admin.setName("Admin User");
            admin.setEmail("admin@example.com");
            admin.setContactNo("9999999999");
            admin.setPassword(passwordEncoder.encode("admin777"));
            admin.setRole("ROLE_ADMIN");
            userRepository.save(admin);
            System.out.println("✅ Default admin user created: admin@example.com / admin777");
        } else {
            System.out.println("ℹ️ Admin user already exists.");
        }
    }
}
