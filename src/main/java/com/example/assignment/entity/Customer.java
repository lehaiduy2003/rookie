package com.example.assignment.entity;

import com.example.assignment.enums.MemberTier;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;


@Entity
@Table(name = "customers")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer extends User {
    @Enumerated(EnumType.STRING)
    private MemberTier memberTier;
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    @ToString.Exclude
    private Set<Order> orders;
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    @ToString.Exclude
    private Set<ShippingAddress> shippingAddresses;
}
