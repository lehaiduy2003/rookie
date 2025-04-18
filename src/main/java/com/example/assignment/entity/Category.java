package com.example.assignment.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "categories")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Category extends BaseEntityAudit {
    @Column(nullable = false, unique = true)
    private String name;
    private String description;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Category> subCategories;
}
