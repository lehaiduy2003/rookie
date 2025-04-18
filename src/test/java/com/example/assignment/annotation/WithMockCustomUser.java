package com.example.assignment.annotation;

import com.example.assignment.config.WithMockCustomerUserSecurityContextFactory;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation for creating a mock user in security tests.
 * This annotation can be used to specify the user ID, username, and roles.
 * It is used in conjunction with the WithMockCustomerUserSecurityContextFactory
 * to create a SecurityContext with the specified user details.
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomerUserSecurityContextFactory.class)
public @interface WithMockCustomUser {
    long id();
    String username();
    String[] roles();
}