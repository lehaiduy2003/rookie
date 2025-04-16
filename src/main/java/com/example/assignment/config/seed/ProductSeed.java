package com.example.assignment.config.seed;

import com.example.assignment.entity.Category;
import com.example.assignment.entity.Product;
import com.example.assignment.repository.CategoryRepository;
import com.example.assignment.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class ProductSeed implements CommandLineRunner {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductSeed(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }
    @Override
    public void run(String... args) {
        if (productRepository.count() > 0) {
            return;
        }

        String categoryName = "Others";
        Category category = categoryRepository.findCategoryByNameContainingIgnoreCase(categoryName);
        // Create sample products
        Product product1 = new Product();
        product1.setName("Product A");
        product1.setDescription("Description for Product A");
        product1.setPrice(19.99);
        product1.setQuantity(10);
        product1.setIsActive(true);
        product1.setCategory(category);

        Product product2 = new Product();
        product2.setName("Product B");
        product2.setDescription("Description for Product B");
        product2.setPrice(29.99);
        product2.setQuantity(10);
        product2.setIsActive(true);
        product2.setCategory(category);

        Product product3 = new Product();
        product3.setName("Product C");
        product3.setDescription("Description for Product C");
        product3.setPrice(39.99);
        product3.setQuantity(10);
        product3.setIsActive(true);
        product3.setCategory(category);

        // Save products to the database
        productRepository.saveAll(List.of(product1, product2, product3));
    }
}
