package com.example.assignment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableCaching
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@EnableAspectJAutoProxy
public class AssignmentApplication {
    public static void main(String[] args) {
        SpringApplication.run(AssignmentApplication.class, args);
    }

}
