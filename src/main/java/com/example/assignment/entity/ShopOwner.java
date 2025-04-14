package com.example.assignment.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "shop_owners")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopOwner extends User {
    @OneToMany(mappedBy = "shopOwner")
    @ToString.Exclude
    private Set<Product> products;
}
