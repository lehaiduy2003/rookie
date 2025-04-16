package com.example.assignment.repository;

import com.example.assignment.entity.Customer;
import com.example.assignment.enums.MemberTier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Page<Customer> findByMemberTier(MemberTier memberTier, Pageable pageable);
}
