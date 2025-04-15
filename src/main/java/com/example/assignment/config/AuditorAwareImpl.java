package com.example.assignment.config;

import com.example.assignment.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("auditorProvider")
public class AuditorAwareImpl implements AuditorAware<User> {
    @PersistenceContext
    private EntityManager entityManager;

    public AuditorAwareImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * TODO: Implement this method to return the current auditor.
     * Get the current auditor.
     * @return the current auditor
     */
    @Override
    public Optional<User> getCurrentAuditor() {
        // hardcode the user for now
        // TODO: replace with actual user retrieval logic
        User user = entityManager.find(User.class, 552L); // hardcoded user ID
        return Optional.ofNullable(user);
    }
}

