package com.example.assignment.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "products")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product extends BaseEntityAudit {
    @Column(nullable = false)
    private String name;
    private String description;
    private double price;
    @Column(name = "average_rating")
    private double avgRating;
    @Column(name = "rating_count")
    private long ratingCount;
    @Column(name = "is_featured")
    private boolean featured;
    private int quantity;
    @Column(name = "image_url")
    private String imageUrl;
    @Column(name = "is_active")
    private Boolean isActive;
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    // Add any additional fields or relationships as needed

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<Rating> ratings;


    @Override
    public void prePersist() {
        super.prePersist();
        this.featured = false;
        this.isActive = true; // Set default value for isActive
        this.avgRating = 0.0; // Set default value for avgRating
        this.ratingCount = 0; // Set default value for ratingCount
    }
}
