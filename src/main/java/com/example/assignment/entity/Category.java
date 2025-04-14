package com.example.assignment.entity;

import jakarta.persistence.*;
import lombok.*;

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
    @OneToOne
    @JoinColumn(name = "parent_id")
    private Category parent;
}
