package com.example.assignment.entity;

import com.example.assignment.enums.ProductPriority;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "feature_products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FeatureProduct extends Product {
    private Date startDate;
    private Date endDate;
    @Enumerated(EnumType.STRING)
    private ProductPriority priority;
    @OneToOne
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;
}
