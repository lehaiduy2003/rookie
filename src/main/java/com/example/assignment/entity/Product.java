package com.example.assignment.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "products")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
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
    private int quantity;
    @Column(name = "image_url")
    private String imageUrl;
    private boolean isActive;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "owner_id", nullable = false)
    private ShopOwner shopOwner;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    // Add any additional fields or relationships as needed
}
