package com.couponmanager.coupon_management_system.service;

import com.couponmanager.coupon_management_system.model.User;
import com.couponmanager.coupon_management_system.model.CouponRedemption;
import com.couponmanager.coupon_management_system.repository.UserRepository;
import com.couponmanager.coupon_management_system.repository.CouponRedemptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CouponRedemptionRepository couponRedemptionRepository;

    public User createUser(User user) {
        // Check if email already exists
        User existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser != null) {
            throw new RuntimeException("Email already exists: " + user.getEmail());
        }
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void deleteUser(User user) {
        // First delete all redemption records for this user
        List<CouponRedemption> userRedemptions = couponRedemptionRepository.findByUserUserId(user.getUserId());
        if (!userRedemptions.isEmpty()) {
            couponRedemptionRepository.deleteAll(userRedemptions);
            System.out.println(
                    "✅ Deleted " + userRedemptions.size() + " redemption records for user ID: " + user.getUserId());
        }

        // Then delete the user
        userRepository.delete(user);
        System.out.println("✅ Deleted user with ID: " + user.getUserId());
    }

    public void deleteUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            deleteUser(user.get());
        } else {
            throw new RuntimeException("User not found with ID: " + id);
        }
    }
}