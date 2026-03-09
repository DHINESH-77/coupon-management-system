package com.couponmanager.coupon_management_system.service;

import com.couponmanager.coupon_management_system.model.Product;
import com.couponmanager.coupon_management_system.model.CouponRedemption;
import com.couponmanager.coupon_management_system.repository.ProductRepository;
import com.couponmanager.coupon_management_system.repository.CouponRedemptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CouponRedemptionRepository couponRedemptionRepository;

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public void deleteProduct(Product product) {
        // First delete all redemption records for this product
        List<CouponRedemption> productRedemptions = couponRedemptionRepository
                .findByProductProductId(product.getProductId());
        if (!productRedemptions.isEmpty()) {
            couponRedemptionRepository.deleteAll(productRedemptions);
            System.out.println("✅ Deleted " + productRedemptions.size() + " redemption records for product ID: "
                    + product.getProductId());
        }

        // Then delete the product
        productRepository.delete(product);
        System.out.println("✅ Deleted product with ID: " + product.getProductId());
    }

    public void deleteProductById(Long id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            deleteProduct(product.get());
        } else {
            throw new RuntimeException("Product not found with ID: " + id);
        }
    }
}