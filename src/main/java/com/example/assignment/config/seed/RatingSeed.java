package com.example.assignment.config.seed;

import com.example.assignment.entity.Customer;
import com.example.assignment.entity.Product;
import com.example.assignment.entity.Rating;
import com.example.assignment.entity.User;
import com.example.assignment.enums.Role;
import com.example.assignment.repository.ProductRepository;
import com.example.assignment.repository.RatingRepository;
import com.example.assignment.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RatingSeed implements CommandLineRunner {
    private final RatingRepository ratingRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void run(String... args) {
        // Check if ratings already exist
        if (ratingRepository.count() > 0) {
            log.info("Ratings already exist, skipping seed");
            return;
        }

        // Find the customer with the email "customer@example.com"
        User user = userRepository.findByEmail("customer@example.com");

        if (user == null || user.getRole() != Role.CUSTOMER) {
            log.error("Customer with email customer@example.com not found, skipping rating seed");
            return;
        }

        Customer customer = (Customer) user;

        // Get all products
        List<Product> products = productRepository.findAll();
        if (products.isEmpty()) {
            log.error("No products found, skipping rating seed");
            return;
        }

        List<Rating> ratings = new ArrayList<>();

        // Create ratings for each product
        for (int i = 0; i < Math.min(products.size(), 3); i++) {
            Product product = products.get(i);

            // Create a rating with score 5
            Rating rating5 = new Rating();
            rating5.setScore(5);
            rating5.setComment("Excellent product! Highly recommended.");
            rating5.setProduct(product);
            rating5.setCustomer(customer);
            // Create a rating with score 3
            Rating rating3 = new Rating();
            rating3.setScore(3);
            rating3.setComment("Average product. Could be better.");
            rating3.setProduct(product);
            rating3.setCustomer(customer);

            // Create a rating with score 1
            Rating rating1 = new Rating();
            rating1.setComment("Poor quality. Not recommended.");
            rating1.setScore(1);
            rating1.setProduct(product);
            rating1.setCustomer(customer);

            ratings.add(rating5);
            ratings.add(rating3);
            ratings.add(rating1);
        }

        // Save all ratings
        ratingRepository.saveAll(ratings);
        log.info("Created {} ratings for testing", ratings.size());
    }
}
