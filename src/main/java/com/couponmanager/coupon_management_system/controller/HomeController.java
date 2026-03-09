package com.couponmanager.coupon_management_system.controller;

import com.couponmanager.coupon_management_system.model.*;
import com.couponmanager.coupon_management_system.service.*;
import com.couponmanager.coupon_management_system.dto.RedemptionRequestDTO;
import com.couponmanager.coupon_management_system.dto.RedemptionResponseDTO;
import com.couponmanager.coupon_management_system.repository.CouponRedemptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponRedemptionService couponRedemptionService;

    @Autowired
    private CouponRedemptionRepository couponRedemptionRepository;

    // Login Page
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // Home Page
    @GetMapping("/")
    public String homePage(Model model) {
        List<User> users = userService.getAllUsers();
        List<Product> products = productService.getAllProducts();
        List<Coupon> coupons = couponService.getAllCoupons();
        List<CouponRedemption> redemptions = couponRedemptionService.getAllRedemptions();

        // Calculate totals using Service (FIXED: Moved logic to Service)
        BigDecimal totalSavings = couponRedemptionService.calculateTotalSavings();
        BigDecimal totalRevenue = couponRedemptionService.calculateTotalRevenue();

        model.addAttribute("appName", "Coupon Management System");
        model.addAttribute("users", users);
        model.addAttribute("products", products);
        model.addAttribute("coupons", coupons);
        model.addAttribute("redemptions", redemptions);
        model.addAttribute("usersCount", users.size());
        model.addAttribute("productsCount", products.size());
        model.addAttribute("couponsCount", coupons.size());
        model.addAttribute("redemptionsCount", redemptions.size());
        model.addAttribute("totalSavings", totalSavings);
        model.addAttribute("totalRevenue", totalRevenue);

        return "home";
    }

    // Users Management Page
    @GetMapping("/users")
    public String usersPage(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "users";
    }

    // Create User
    @PostMapping("/users")
    public String createUser(@ModelAttribute User user, Model model) {
        try {
            userService.createUser(user);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            List<User> users = userService.getAllUsers();
            model.addAttribute("users", users);
            return "users";
        }
        return "redirect:/users";
    }

    // Delete User - FIXED VERSION
    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUserById(id);
            redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting user: " + e.getMessage());
            System.out.println("❌ Error deleting user: " + e.getMessage());
        }
        return "redirect:/users";
    }

    // Products Management Page
    @GetMapping("/products")
    public String productsPage(Model model) {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "products";
    }

    // Create Product
    @PostMapping("/products")
    public String createProduct(@ModelAttribute Product product, Model model) {
        try {
            productService.createProduct(product);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            List<Product> products = productService.getAllProducts();
            model.addAttribute("products", products);
            return "products";
        }
        return "redirect:/products";
    }

    // Delete Product - FIXED VERSION
    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProductById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Product deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting product: " + e.getMessage());
            System.out.println("❌ Error deleting product: " + e.getMessage());
        }
        return "redirect:/products";
    }

    // Coupons Management Page
    @GetMapping("/coupons")
    public String couponsPage(Model model) {
        List<Coupon> coupons = couponService.getAllCoupons();
        model.addAttribute("coupons", coupons);
        return "coupons";
    }

    // Create Coupon
    @PostMapping("/coupons")
    public String createCoupon(@ModelAttribute Coupon coupon, Model model) {
        try {
            // Manual date validation
            if (coupon.getEndDate() != null && coupon.getStartDate() != null &&
                    coupon.getEndDate().isBefore(coupon.getStartDate())) {
                model.addAttribute("error", "End date must be after start date");
                List<Coupon> coupons = couponService.getAllCoupons();
                model.addAttribute("coupons", coupons);
                return "coupons";
            }

            couponService.createCoupon(coupon);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            List<Coupon> coupons = couponService.getAllCoupons();
            model.addAttribute("coupons", coupons);
            return "coupons";
        }
        return "redirect:/coupons";
    }

    // Delete Coupon - FIXED VERSION
    @PostMapping("/coupons/delete/{id}")
    public String deleteCoupon(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            couponService.deleteCouponById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Coupon deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting coupon: " + e.getMessage());
            System.out.println("❌ Error deleting coupon: " + e.getMessage());
        }
        return "redirect:/coupons";
    }

    // Redemption Page
    @GetMapping("/redeem")
    public String redeemPage(Model model) {
        List<User> users = userService.getAllUsers();
        List<Product> products = productService.getAllProducts();
        List<Coupon> coupons = couponService.getAllCoupons();

        model.addAttribute("users", users);
        model.addAttribute("products", products);
        model.addAttribute("coupons", coupons);
        model.addAttribute("redemptionRequest", new RedemptionRequestDTO());

        return "redeem";
    }

    // Apply Coupon
    @PostMapping("/apply-coupon")
    public String applyCoupon(@ModelAttribute RedemptionRequestDTO request, Model model) {
        RedemptionResponseDTO response = couponRedemptionService.applyCoupon(request);

        // Get updated lists for the page
        List<User> users = userService.getAllUsers();
        List<Product> products = productService.getAllProducts();
        List<Coupon> coupons = couponService.getAllCoupons();

        model.addAttribute("users", users);
        model.addAttribute("products", products);
        model.addAttribute("coupons", coupons);
        model.addAttribute("redemptionRequest", new RedemptionRequestDTO());
        model.addAttribute("redemptionResponse", response);

        return "redeem";
    }

    // Redemption History Page
    @GetMapping("/history")
    public String redemptionHistory(Model model) {
        try {
            List<CouponRedemption> redemptions = couponRedemptionService.getAllRedemptions();
            List<User> users = userService.getAllUsers();

            // Use Service for calculations (FIXED: Consistency)
            BigDecimal totalSavings = couponRedemptionService.calculateTotalSavings();
            BigDecimal totalRevenue = couponRedemptionService.calculateTotalRevenue();

            model.addAttribute("redemptions", redemptions);
            model.addAttribute("users", users);
            model.addAttribute("totalSavings", totalSavings);
            model.addAttribute("totalRevenue", totalRevenue);
            model.addAttribute("totalRedemptions", redemptions.size());

            System.out.println("Loaded " + redemptions.size() + " redemption records");

        } catch (Exception e) {
            System.out.println("Error loading history: " + e.getMessage());
            model.addAttribute("error", "Error loading redemption history");
        }

        return "history";
    }

    // Delete Redemption Record - FIXED VERSION
    @PostMapping("/history/delete/{id}")
    public String deleteRedemption(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // Check if redemption exists before deleting
            boolean exists = couponRedemptionRepository.existsById(id);

            if (exists) {
                couponRedemptionRepository.deleteById(id);
                redirectAttributes.addFlashAttribute("successMessage", "Redemption record deleted successfully!");
                System.out.println("✅ Deleted redemption record with ID: " + id);
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Redemption record not found!");
                System.out.println("❌ Redemption record not found with ID: " + id);
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting redemption record: " + e.getMessage());
            System.out.println("❌ Error deleting redemption: " + e.getMessage());
            e.printStackTrace();
        }

        return "redirect:/history";
    }
}