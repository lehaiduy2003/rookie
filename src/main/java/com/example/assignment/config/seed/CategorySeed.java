package com.example.assignment.config.seed;

import com.example.assignment.entity.Category;
import com.example.assignment.repository.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class CategorySeed implements CommandLineRunner {
    private final CategoryRepository categoryRepository;

    public CategorySeed(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // Check if categories already exist
        if (categoryRepository.count() != 0) {
            return;
        }
        // Create parent categories
        Category electronics = Category.builder()
            .name("Electronics")
            .description("Devices and gadgets")
            .build();
        Category fashion = Category.builder()
            .name("Fashion")
            .description("Clothing and accessories")
            .build();
        Category others = Category.builder()
            .name("Others")
            .description("Miscellaneous items")
            .build();

        // Save parent categories
        categoryRepository.saveAll(List.of(electronics, fashion, others));

        // Create child categories for Electronics
        Category mobiles = Category.builder()
            .name("Mobiles")
            .description("Mobile phones and accessories")
            .parent(electronics)
            .build();
        Category laptops = Category.builder()
            .name("Laptops")
            .description("Laptops and accessories")
            .parent(electronics)
            .build();

        // Create child categories for Fashion
        Category menClothing = Category.builder()
            .name("Men's Clothing")
            .description("Description for men's clothing")
            .parent(fashion)
            .build();
        Category womenClothing = Category.builder()
            .name("Women's Clothing")
            .description("Clothing for women")
            .parent(fashion)
            .build();

        // Save child categories
        categoryRepository.saveAll(List.of(mobiles, laptops, menClothing, womenClothing));

        // Add two more levels for Electronics -> Mobiles
        Category smartphones = Category.builder()
            .name("Smartphones")
            .description("Smartphones of various brands")
            .parent(mobiles)
            .build();
        Category accessories = Category.builder()
            .name("Accessories")
            .description("Mobile accessories like chargers and cases")
            .parent(mobiles)
            .build();

        // Add two more levels for Electronics -> Laptops
        Category gamingLaptops = Category.builder()
            .name("Gaming Laptops")
            .description("High-performance laptops for gaming")
            .parent(laptops)
            .build();
        Category ultrabooks = Category.builder()
            .name("Ultrabooks")
            .description("Lightweight and portable laptops")
            .parent(laptops)
            .build();

        // Add two more levels for Fashion -> Men's Clothing
        Category formalWear = Category.builder()
            .name("Formal Wear")
            .description("Suits, shirts, and ties")
            .parent(menClothing)
            .build();
        Category casualWear = Category.builder()
            .name("Casual Wear")
            .description("T-shirts, jeans, and jackets")
            .parent(menClothing)
            .build();

        // Add two more levels for Fashion -> Women's Clothing
        Category dresses = Category.builder()
            .name("Dresses")
            .description("Various types of dresses")
            .parent(womenClothing)
            .build();
        Category accessoriesWomen = Category.builder()
            .name("necklaces")
            .description("Jewelry, handbags, and more")
            .parent(womenClothing)
            .build();

        // Save the new levels
        categoryRepository.saveAll(List.of(
            smartphones, accessories, gamingLaptops, ultrabooks,
            formalWear, casualWear, dresses, accessoriesWomen
        ));
    }
}
